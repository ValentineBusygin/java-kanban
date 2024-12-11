package ru.yandex.app.service.HttpServer;

import org.junit.jupiter.api.Test;
import ru.yandex.app.model.EpicTask;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.TaskState;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerSubTaskTest extends HttpTaskServerTest {

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
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

        SubTask subTask = new SubTask("Test subtask", "Testting Subtask", 1);
        String subTaskJson = gson.toJson(subTask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> tasksFromManager = manager.getSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test subtask", tasksFromManager.get(0).getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
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

        SubTask subTask = new SubTask("Test subtask", "Testting Subtask", 1);
        String subTaskJson = gson.toJson(subTask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> subTasksFromManager = manager.getSubTasks();

        assertNotNull(subTasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество задач");

        SubTask addedTaskWithId = subTasksFromManager.get(0);
        assertEquals("Test subtask", addedTaskWithId.getTaskName(), "Некорректное имя задачи");
        addedTaskWithId.setTaskName("Test subtask update");

        subTaskJson = gson.toJson(addedTaskWithId);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        subTasksFromManager = manager.getSubTasks();

        assertEquals("Test subtask update", subTasksFromManager.get(0).getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubTask() throws IOException, InterruptedException {
        // создаём задачу
        EpicTask epicTask = new EpicTask("Test 2", "Testing epic 2");
        manager.addEpicTask(epicTask);

        SubTask subTask = new SubTask("Test subtask", "Testting Subtask", 1);
        manager.addSubTask(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        String responseBody = response.body();

        SubTask receivedSubTask = gson.fromJson(responseBody, SubTask.class);

        assertNotNull(receivedSubTask, "Ответ некорректно преобразован из JSON");
        assertEquals(2, receivedSubTask.getTaskID(), "Некорректный ID задачи");
        assertEquals("Test subtask", receivedSubTask.getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetAllSubTasks() throws IOException, InterruptedException {
        // создаём задачи
        EpicTask epicTask = new EpicTask("Test 1", "Testing epic 1");
        manager.addEpicTask(epicTask);

        epicTask = new EpicTask("Test 2", "Testing epic 2");
        manager.addEpicTask(epicTask);

        SubTask subTask = new SubTask("Test subtask", "Testting Subtask", 1);
        manager.addSubTask(subTask);

        subTask = new SubTask("Test subtask 2", "Testting Subtask 2", 1);
        manager.addSubTask(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        String responseBody = response.body();

        String[] tasks = responseBody.split("\n");
        assertEquals(2, tasks.length, "Должно быть 2 сабтаска");
    }

    @Test
    public void testGetUnexistsSubTask() throws IOException, InterruptedException {
        // создаём задачи
        EpicTask epicTask = new EpicTask("Test 1", "Testing epic 1");
        manager.addEpicTask(epicTask);

        SubTask subTask = new SubTask("Test subtask", "Testting Subtask", 1);
        manager.addSubTask(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/10");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        // создаём задачи
        EpicTask epicTask = new EpicTask("Test 1", "Testing epic 1");
        manager.addEpicTask(epicTask);

        SubTask subTask = new SubTask("Test subtask", "Testting Subtask", 1);
        manager.addSubTask(subTask);

        subTask = new SubTask("Test subtask 2", "Testting Subtask 2", 1);
        manager.addSubTask(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        String responseBody = response.body();

        String[] tasks = responseBody.split("\n");
        assertEquals(2, tasks.length, "Должно быть 2 сабтаска");

        //Пробуем удалить несуществующий таск
        url = URI.create("http://localhost:8080/subtasks/4");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        //Пробуем удалить существующий таск
        url = URI.create("http://localhost:8080/subtasks/3");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        //Получаем список задач
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        responseBody = response.body();

        tasks = responseBody.split("\n");
        assertEquals(1, tasks.length, "Должен быть 1 сабтаск");
    }
}
