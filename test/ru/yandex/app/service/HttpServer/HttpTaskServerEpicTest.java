package ru.yandex.app.service.HttpServer;

import org.junit.jupiter.api.Test;
import ru.yandex.app.model.EpicTask;
import ru.yandex.app.model.Task;
import ru.yandex.app.model.TaskState;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerEpicTest extends HttpTaskServerTest {

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        EpicTask epicTask = new EpicTask("Test 2", "Testing epic 2");
        // конвертируем её в JSON
        String taskJson = gson.toJson(epicTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<EpicTask> tasksFromManager = manager.getEpicTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        // создаём задачу
        EpicTask epicTask = new EpicTask("Test 2", "Testing epic 2");
        // конвертируем её в JSON
        String taskJson = gson.toJson(epicTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за обновление задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<EpicTask> tasksFromManager = manager.getEpicTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        EpicTask addedTaskWithId = tasksFromManager.get(0);
        addedTaskWithId.setTaskName("Test 2 update");
        taskJson = gson.toJson(addedTaskWithId);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        tasksFromManager = manager.getEpicTasks();
        assertEquals("Test 2 update", tasksFromManager.get(0).getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        // создаём задачу
        EpicTask epicTask = new EpicTask("Test 2", "Testing epic 2");
        manager.addEpicTask(epicTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        String responseBody = response.body();

        EpicTask receivedEpicTask = gson.fromJson(responseBody, EpicTask.class);

        assertNotNull(receivedEpicTask, "Ответ некорректно преобразован из JSON");
        assertEquals(1, receivedEpicTask.getTaskID(), "Некорректный ID задачи");
        assertEquals("Test 2", receivedEpicTask.getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        // создаём задачи
        EpicTask epicTask = new EpicTask("Test 1", "Testing epic 1");
        manager.addEpicTask(epicTask);

        epicTask = new EpicTask("Test 2", "Testing epic 2");
        manager.addEpicTask(epicTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        String responseBody = response.body();

        String[] tasks = responseBody.split("\n");
        assertEquals(2, tasks.length, "Должно быть 2 эпика");
    }

    @Test
    public void testGetUnexistsEpic() throws IOException, InterruptedException {
        // создаём задачи
        EpicTask epicTask = new EpicTask("Test 1", "Testing epic 1");
        manager.addEpicTask(epicTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/10");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // создаём задачи
        EpicTask epicTask = new EpicTask("Test 1", "Testing epic 1");
        manager.addEpicTask(epicTask);

        epicTask = new EpicTask("Test 2", "Testing epic 2");
        manager.addEpicTask(epicTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        String responseBody = response.body();

        String[] tasks = responseBody.split("\n");
        assertEquals(2, tasks.length, "Должно быть 2 эпика");

        //Пробуем удалить несуществующий таск
        url = URI.create("http://localhost:8080/epics/4");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        //Пробуем удалить существующий таск
        url = URI.create("http://localhost:8080/epics/2");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        //Получаем список задач
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        responseBody = response.body();

        tasks = responseBody.split("\n");
        assertEquals(1, tasks.length, "Должен быть 1 эпик");
    }
}
