package ru.yandex.app.service.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public class HistoryHandler extends BaseHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
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
