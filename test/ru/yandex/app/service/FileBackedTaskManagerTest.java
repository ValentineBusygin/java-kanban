package ru.yandex.app.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    TaskManager taskManager;
    File tmpFile;

    @Test
    void testEmptyLoad() {
        try {
            tmpFile = File.createTempFile("tmpFile", ".csv");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        taskManager = new FileBackedTaskManager(tmpFile);

        assertEquals(0,taskManager.getEpicTasks().size(),  "Массив эпиков не пустой для пустого файла");
        assertEquals(0,taskManager.getTasks().size(), "Массив тасков не пустой для пустого файла");
        assertEquals(0,taskManager.getSubTasks().size(), "Массив сабтасков не пустой для пустого файла");
    }

    @Test
    void testEmptySaveLoad() {
        try {
            tmpFile = File.createTempFile("tmpFile", ".csv");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        taskManager = new FileBackedTaskManager(tmpFile);

        List<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size(), "Массив эпиков не пустой после инициализации");

        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(1, epicTasks.size(), "Эпик не добавился в массив");

        taskManager.clearEpicTasks();
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size(), "Массив эпиков не пустой после удаления единственного элемента");

        taskManager = new FileBackedTaskManager(tmpFile);

        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size(), "Массив эпиков не пустой после загрузки пустого файла");
    }

    @Test
    void testUnEmptySaveLoad() {
        try {
            tmpFile = File.createTempFile("tmpFile", ".csv");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        taskManager = new FileBackedTaskManager(tmpFile);

        List<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size(), "Массив эпиков не пустой после инициализации");

        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(1, epicTasks.size(), "Эпик не добавился в массив");

        SubTask newSubTask = new SubTask("SubTask", "SubTaskDesc", TaskState.IN_PROGRESS, newEpicTask.getTaskID());
        taskManager.addSubTask(newSubTask);

        Task newTask = new Task("Task");
        taskManager.addTask(newTask);

        taskManager = new FileBackedTaskManager(tmpFile);

        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(1, epicTasks.size(), "Массив эпиков не пустой после загрузки не пустого файла");

        List<SubTask> subTasks = taskManager.getSubTasks();
        assertNotNull(subTasks);
        assertEquals(1, subTasks.size(), "Массив сабтасков не пустой после загрузки не пустого файла");

        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size(), "Массив тасков не пустой после загрузки не пустого файла");

    }
}