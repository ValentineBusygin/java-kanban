package ru.yandex.app.service.HttpServer;

import org.junit.jupiter.api.Test;
import ru.yandex.app.model.EpicTask;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;
import ru.yandex.app.model.TaskState;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerPrioritizedTest extends HttpTaskServerTest {

    public HttpTaskServerPrioritizedTest() throws IOException {
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        // создаём задачи
        EpicTask epicTask = new EpicTask("Test 1", "Testing epic 1");
        manager.addEpicTask(epicTask);

        SubTask subTask = new SubTask(2, "SubTaskOne", "SubTaskOneDescription", TaskState.NEW, LocalDateTime.now(), Duration.ofMinutes(10), 1);
        manager.addSubTask(subTask);

        subTask = new SubTask(3, "SubTaskTwo", "SubTaskOneDescription", TaskState.NEW, LocalDateTime.now().minusMinutes(30), Duration.ofMinutes(10), 1);
        manager.addSubTask(subTask);

        manager.getSubTaskById(3);

        Task task = new Task("task", "descr", TaskState.NEW, LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));
        manager.addTask(task);

        manager.getTaskById(4);
        manager.getSubTaskById(2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        String responseBody = response.body();

        String[] tasks = responseBody.split("\n");
        assertEquals(3, tasks.length, "Должно быть 3 элемента в истории");

        SubTask resultSubTask = gson.fromJson(tasks[0], SubTask.class);
        assertEquals(3, resultSubTask.getTaskID(), "Первым должен идти сабтаск 3");

        resultSubTask = gson.fromJson(tasks[1], SubTask.class);
        assertEquals(2, resultSubTask.getTaskID(), "Вторым должен идти сабтаск 2");

        Task resultTask = gson.fromJson(tasks[2], Task.class);
        assertEquals(4, resultTask.getTaskID(), "Третьим должен идти таск 4");
    }
}
