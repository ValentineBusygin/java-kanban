package ru.yandex.app.service.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.app.exceptions.TaskAddingException;
import ru.yandex.app.exceptions.TaskNotFoundException;
import ru.yandex.app.model.EpicTask;
import ru.yandex.app.model.Task;
import ru.yandex.app.model.TaskTypes;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class EpicTaskHandler extends BaseHandler {
    public EpicTaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
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
