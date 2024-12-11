package ru.yandex.app.service;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.app.exceptions.TaskAddingException;
import ru.yandex.app.exceptions.TaskNotFoundException;
import ru.yandex.app.model.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson;
    private final HttpServer httpServer;

    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm.ss");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
            if (localDate == null) {
                jsonWriter.value("");
            } else {
                jsonWriter.value(localDate.format(dtf));
            }
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            String nextString = jsonReader.nextString();
            if (nextString == null || nextString.isEmpty()) {
                return null;
            } else {
                return LocalDateTime.parse(nextString, dtf);
            }
        }
    }

    static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.value("");
            } else {
                jsonWriter.value(String.valueOf(duration.getSeconds()));
            }
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            String nextString = jsonReader.nextString();
            if (nextString == null || nextString.isEmpty()) {
                return null;
            } else {
                return Duration.ofSeconds(Long.parseLong(nextString));
            }
        }
    }

    public static void main(String[] args) {
        final TaskManager taskManager = Managers.createTaskManger(TaskManagerTypes.FILE_BACKED_TASK_MANAGER);

        HttpTaskServer server = new HttpTaskServer(taskManager);

        server.start();
    }

    public static Gson getGson() {
        return gson;
    }

    public HttpTaskServer(TaskManager inTaskManager) {

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        try {
            httpServer = HttpServer.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            httpServer.bind(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        httpServer.createContext("/tasks", new TaskHandler(inTaskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(inTaskManager));
        httpServer.createContext("/epics", new EpicTaskHandler(inTaskManager));
        httpServer.createContext("/history", new HistoryHandler(inTaskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(inTaskManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    static class BaseHandler implements HttpHandler {

        protected final TaskManager taskManager;

        enum Endpoint { GET, GET_ALL, GET_SUBTASKS, CREATE_OR_UPDATE, DELETE, UNKNOWN }

        public BaseHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

        }

        protected Task getTaskFromManager(Integer taskID, TaskTypes taskType) {
            switch (taskType) {
                case TASK -> {
                    return taskManager.getTaskById(taskID);
                }
                case SUB_TASK -> {
                    return taskManager.getSubTaskById(taskID);
                }
                case EPIC_TASK -> {
                    return taskManager.getEpicTaskById(taskID);
                }
                default -> {
                    return null;
                }
            }
        }

        protected void deleteTaskFromManager(Integer taskID, TaskTypes taskType) {
            switch (taskType) {
                case TASK -> {
                    taskManager.removeTask(taskID);
                }
                case SUB_TASK -> {
                    taskManager.removeSubTask(taskID);
                }
                case EPIC_TASK -> {
                    taskManager.removeEpicTask(taskID);
                }
            }
        }

        protected void sendOk(HttpExchange exchange) throws IOException {
            writeResponse(exchange, null, 200);
        }

        protected void sendText(HttpExchange exchange, String text) throws IOException {
            if (text == null || text.isBlank()) {
                sendOk(exchange);
            } else {
                writeResponse(exchange, text, 200);
            }

        }

        protected void sendUpdateSuccess(HttpExchange exchange) throws IOException {
            writeResponse(exchange, "", 201);
        }

        protected void sendNotFound(HttpExchange exchange) throws IOException {
            writeResponse(exchange, "", 404);
        }

        protected void sendHasInteractions(HttpExchange exchange) throws IOException {
            writeResponse(exchange, "", 406);
        }

        protected void sendWrongRequest(HttpExchange exchange) throws IOException {
            writeResponse(exchange, "", 400);
        }

        protected void writeResponse(HttpExchange exchange, String response, int responseCode) throws IOException {
            try (OutputStream outStream = exchange.getResponseBody()) {
                if (response != null && !response.isBlank()) {
                    exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");

                    byte[] responseBytes = response.getBytes(DEFAULT_CHARSET);

                    exchange.sendResponseHeaders(responseCode, responseBytes.length);
                    outStream.write(responseBytes);
                } else {
                    exchange.sendResponseHeaders(responseCode, 0);
                }
            }

            exchange.close();
        }

        protected Endpoint getEndpoint(String requestPath, String requestMethod, String handler) {
            String[] pathParts = requestPath.split("/");

            if (pathParts.length < 2 || !handler.equals(pathParts[1])) {
                return Endpoint.UNKNOWN;
            }

            switch (requestMethod) {
                case "GET":
                    if (pathParts.length == 2) {
                        return Endpoint.GET_ALL;
                    } else if (pathParts.length == 3) {
                        return Endpoint.GET;
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                case "POST":
                    return Endpoint.CREATE_OR_UPDATE;
                case "DELETE":
                    return pathParts.length == 3 ? Endpoint.DELETE : Endpoint.UNKNOWN;
                default:
                    return Endpoint.UNKNOWN;
            }
        }

        protected Optional<Integer> getElementId(String requestPath) {
            String[] pathParts = requestPath.split("/");
            if (pathParts.length != 3) {
                return Optional.empty();
            }

            try {
                return Optional.of(Integer.parseInt(pathParts[2]));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }
    }

    static class TaskHandler extends BaseHandler {
        public TaskHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();

            Endpoint endpoint = getEndpoint(requestPath, exchange.getRequestMethod(), "tasks");
            switch (endpoint) {
                case GET: {
                    Optional<Integer> elementIdOpt = getElementId(requestPath);
                    if (elementIdOpt.isPresent()) {
                        try {
                            Task task = getTaskFromManager(elementIdOpt.get(), TaskTypes.TASK);

                            String response = gson.toJson(task);

                            sendText(exchange, response);
                        } catch (TaskNotFoundException e) {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                }
                case GET_ALL: {
                    String response = taskManager.getTasks().stream()
                            .map(gson::toJson)
                            .collect(Collectors.joining("\n"));
                    sendText(exchange, response);

                    break;
                }
                case CREATE_OR_UPDATE: {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

                    Task task = gson.fromJson(requestBody, Task.class);
                    if (task.getTaskID() != -1) {
                        taskManager.updateTask(task);
                        sendUpdateSuccess(exchange);
                    } else {
                        try {
                            taskManager.addTask(task);
                            sendOk(exchange);
                        } catch (TaskAddingException e) {
                            sendHasInteractions(exchange);
                        }
                    }
                    break;
                }
                case DELETE: {
                    Optional<Integer> elementIdOpt = getElementId(requestPath);
                    if (elementIdOpt.isPresent()) {
                        try {
                            deleteTaskFromManager(elementIdOpt.get(), TaskTypes.TASK);

                            sendOk(exchange);
                        } catch (TaskNotFoundException e) {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                }
                case UNKNOWN: {
                    sendWrongRequest(exchange);
                }
            }
        }
    }

    static class SubTaskHandler extends BaseHandler {
        public SubTaskHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();

            Endpoint endpoint = getEndpoint(requestPath, exchange.getRequestMethod(), "subtasks");
            switch (endpoint) {
                case GET: {
                    Optional<Integer> elementIdOpt = getElementId(requestPath);
                    if (elementIdOpt.isPresent()) {
                        try {
                            Task task = getTaskFromManager(elementIdOpt.get(), TaskTypes.SUB_TASK);

                            String response = gson.toJson(task);

                            sendText(exchange, response);
                        } catch (TaskNotFoundException e) {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                }
                case GET_ALL: {
                    String response = taskManager.getSubTasks().stream()
                            .map(gson::toJson)
                            .collect(Collectors.joining("\n"));
                    sendText(exchange, response);

                    break;
                }
                case CREATE_OR_UPDATE: {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

                    SubTask subTask = gson.fromJson(requestBody, SubTask.class);
                    if (subTask.getTaskID() != -1) {
                        taskManager.updateSubTask(subTask);
                        sendUpdateSuccess(exchange);
                    } else {
                        try {
                            taskManager.addSubTask(subTask);
                            sendOk(exchange);
                        } catch (TaskAddingException e) {
                            sendHasInteractions(exchange);
                        }
                    }
                    break;
                }
                case DELETE: {
                    Optional<Integer> elementIdOpt = getElementId(requestPath);
                    if (elementIdOpt.isPresent()) {
                        try {
                            deleteTaskFromManager(elementIdOpt.get(), TaskTypes.SUB_TASK);

                            sendOk(exchange);
                        } catch (TaskNotFoundException e) {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                }
                case UNKNOWN: {
                    sendWrongRequest(exchange);
                }
            }
        }
    }

    static class EpicTaskHandler extends BaseHandler {
        public EpicTaskHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();

            Endpoint endpoint = getEndpoint(requestPath, exchange.getRequestMethod(), "epics");
            switch (endpoint) {
                case GET: {
                    Optional<Integer> elementIdOpt = getElementId(requestPath);
                    if (elementIdOpt.isPresent()) {
                        try {
                            Task task = getTaskFromManager(elementIdOpt.get(), TaskTypes.EPIC_TASK);

                            String response = gson.toJson(task);

                            sendText(exchange, response);
                        } catch (TaskNotFoundException e) {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                }
                case GET_ALL: {
                    String response = taskManager.getEpicTasks().stream()
                            .map(gson::toJson)
                            .collect(Collectors.joining("\n"));
                    sendText(exchange, response);

                    break;
                }
                case GET_SUBTASKS: {
                    Optional<Integer> elementIdOpt = getElementId(requestPath);
                    if (elementIdOpt.isPresent()) {
                        String response = taskManager.getEpicSubTasks(elementIdOpt.get()).stream()
                                .map(gson::toJson)
                                .collect(Collectors.joining("\n"));
                        sendText(exchange, response);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                }
                case CREATE_OR_UPDATE: {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

                    EpicTask epicTask = gson.fromJson(requestBody, EpicTask.class);
                    if (epicTask.getTaskID() != -1) {
                        taskManager.updateEpicTask(epicTask);
                        sendUpdateSuccess(exchange);
                    } else {
                        try {
                            taskManager.addEpicTask(epicTask);
                            sendOk(exchange);
                        } catch (TaskAddingException e) {
                            sendHasInteractions(exchange);
                        }
                    }
                    break;
                }
                case DELETE: {
                    Optional<Integer> elementIdOpt = getElementId(requestPath);
                    if (elementIdOpt.isPresent()) {
                        try {
                            deleteTaskFromManager(elementIdOpt.get(), TaskTypes.EPIC_TASK);

                            sendOk(exchange);
                        } catch (TaskNotFoundException e) {
                            sendNotFound(exchange);
                        }
                        sendOk(exchange);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                }
                case UNKNOWN: {
                    sendWrongRequest(exchange);
                }
            }
        }

        @Override
        protected Endpoint getEndpoint(String requestPath, String requestMethod, String handler) {
            String[] pathParts = requestPath.split("/");

            if (pathParts.length < 2 || !handler.equals(pathParts[1])) {
                return Endpoint.UNKNOWN;
            }

            switch (requestMethod) {
                case "GET":
                    if (pathParts.length == 2) {
                        return Endpoint.GET_ALL;
                    } else if (pathParts.length == 3) {
                        return Endpoint.GET;
                    } else if (pathParts.length == 4) {
                        return Endpoint.GET_SUBTASKS;
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                case "POST":
                    return Endpoint.CREATE_OR_UPDATE;
                case "DELETE":
                    return pathParts.length == 3 ? Endpoint.DELETE : Endpoint.UNKNOWN;
                default:
                    return Endpoint.UNKNOWN;
            }
        }
    }

    static class HistoryHandler extends BaseHandler {
        public HistoryHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();

            Endpoint endpoint = getEndpoint(requestPath, exchange.getRequestMethod(), "history");
            if (Objects.requireNonNull(endpoint) == Endpoint.GET_ALL) {
                String response = taskManager.getHistory().stream()
                        .map(gson::toJson)
                        .collect(Collectors.joining("\n"));

                sendText(exchange, response);
            } else {
                sendWrongRequest(exchange);
            }
        }

        @Override
        protected Endpoint getEndpoint(String requestPath, String requestMethod, String handler) {
            String[] pathParts = requestPath.split("/");

            if (pathParts.length < 2 || !handler.equals(pathParts[1])) {
                return Endpoint.UNKNOWN;
            }

            return (requestMethod.equals("GET")) ? Endpoint.GET_ALL : Endpoint.UNKNOWN;
        }
    }

    static class PrioritizedHandler extends BaseHandler {
        public PrioritizedHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();

            Endpoint endpoint = getEndpoint(requestPath, exchange.getRequestMethod(), "prioritized");
            if (Objects.requireNonNull(endpoint) == Endpoint.GET_ALL) {
                String response = taskManager.getPrioritizedTasks().stream()
                        .map(gson::toJson)
                        .collect(Collectors.joining("\n"));

                sendText(exchange, response);
            } else {
                sendWrongRequest(exchange);
            }
        }

        @Override
        protected Endpoint getEndpoint(String requestPath, String requestMethod, String handler) {
            String[] pathParts = requestPath.split("/");

            if (pathParts.length < 2 || !handler.equals(pathParts[1])) {
                return Endpoint.UNKNOWN;
            }
            return (requestMethod.equals("GET")) ? Endpoint.GET_ALL : Endpoint.UNKNOWN;
        }
    }
}
