package ru.yandex.app.service;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.app.model.*;
import ru.yandex.app.service.TypeAdapters.*;
import ru.yandex.app.service.Handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import com.google.gson.Gson;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static Gson gson;
    private final HttpServer httpServer;

    public static void main(String[] args) throws IOException {
        final TaskManager taskManager = Managers.createTaskManger(TaskManagerTypes.FILE_BACKED_TASK_MANAGER);

        HttpTaskServer server = new HttpTaskServer(taskManager);

        server.start();
    }

    public static Gson getGson() {
        return gson;
    }

    public HttpTaskServer(TaskManager inTaskManager) throws IOException {

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(inTaskManager, gson));
        httpServer.createContext("/subtasks", new SubTaskHandler(inTaskManager, gson));
        httpServer.createContext("/epics", new EpicTaskHandler(inTaskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(inTaskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(inTaskManager, gson));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
