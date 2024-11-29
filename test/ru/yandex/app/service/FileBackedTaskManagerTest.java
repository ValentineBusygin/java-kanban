package ru.yandex.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest  extends TaskManagerTest<FileBackedTaskManager> {

    File tmpFile;

    @BeforeEach
    @Override
    public void initTest() {
        try {
            tmpFile = File.createTempFile("tmpFile", ".csv");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        taskManager = new FileBackedTaskManager(tmpFile);
    }

    @Test
    void testEmptyLoad() {
        assertEquals(0, taskManager.getEpicTasks().size(),  "Массив эпиков не пустой для пустого файла");
        assertEquals(0, taskManager.getTasks().size(), "Массив тасков не пустой для пустого файла");
        assertEquals(0, taskManager.getSubTasks().size(), "Массив сабтасков не пустой для пустого файла");
    }

    @Test
    void testEmptySaveLoad() {
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

        FileBackedTaskManager taskManagerForRead = new FileBackedTaskManager(tmpFile);

        epicTasks = taskManagerForRead.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size(), "Массив эпиков не пустой после загрузки пустого файла");
    }

    @Test
    void testUnEmptySaveLoad() {
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

        FileBackedTaskManager taskManagerForRead = new FileBackedTaskManager(tmpFile);

        epicTasks = taskManagerForRead.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(1, epicTasks.size(), "Массив эпиков не пустой после загрузки не пустого файла");

        List<SubTask> subTasks = taskManagerForRead.getSubTasks();
        assertNotNull(subTasks);
        assertEquals(1, subTasks.size(), "Массив сабтасков не пустой после загрузки не пустого файла");

        List<Task> tasks = taskManagerForRead.getTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size(), "Массив тасков не пустой после загрузки не пустого файла");

    }
}