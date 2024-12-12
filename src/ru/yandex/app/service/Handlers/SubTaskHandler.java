package ru.yandex.app.service.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.app.exceptions.TaskAddingException;
import ru.yandex.app.exceptions.TaskNotFoundException;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;
import ru.yandex.app.model.TaskTypes;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubTaskHandler extends BaseHandler {
    public SubTaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
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
