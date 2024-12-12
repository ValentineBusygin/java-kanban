package ru.yandex.app.service.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.app.model.Task;
import ru.yandex.app.model.TaskTypes;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHandler implements HttpHandler {

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected enum Endpoint { GET, GET_ALL, GET_SUBTASKS, CREATE_OR_UPDATE, DELETE, UNKNOWN }

    public BaseHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
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
