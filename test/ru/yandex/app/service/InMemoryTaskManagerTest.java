package ru.yandex.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.EpicTask;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;
import ru.yandex.app.model.TaskTypes;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    InMemoryTaskManager taskManager;

    @BeforeEach
    void initTest() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void workWithTask() {
        ArrayList<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(tasks.size(), 0);

        Task newTask = new Task("Task");
        taskManager.addTask(newTask);
        tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(tasks.size(), 1);

        taskManager.removeTask(newTask.getTaskID());
        tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(tasks.size(), 0);
    }

    @Test
    void workWIthEpicTaskAndSubTask() {
        ArrayList<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 0);

        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 1);
        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID

        SubTask newSubTask = new SubTask("SubTask", "SubTaskDesc", newEpicTask.getTaskID());
        taskManager.addSubTask(newSubTask);
        ArrayList<SubTask> epicSubTasks = taskManager.getEpicSubTasks(newEpicTask.getTaskID());
        assertNotNull(epicSubTasks);
        assertEquals(epicSubTasks.size(), 1);
        assertEquals(epicSubTasks.get(0), newSubTask);

        taskManager.removeEpicTask(newEpicTask.getTaskID());
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 0);
    }

    @Test
    void clearAllTasks() {
        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        ArrayList<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 1);

        SubTask newSubTask = new SubTask("SubTask", "SubTaskDesc", newEpicTask.getTaskID());
        taskManager.addSubTask(newSubTask);
        ArrayList<SubTask> subTasks = taskManager.getSubTasks();
        assertNotNull(subTasks);
        assertEquals(subTasks.size(), 1);

        Task newTask = new Task("Task");
        taskManager.addTask(newTask);
        ArrayList<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(tasks.size(), 1);

        taskManager.clearAllTasks();
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 0);
        subTasks = taskManager.getSubTasks();
        assertNotNull(subTasks);
        assertEquals(subTasks.size(), 0);
        tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(tasks.size(), 0);
    }

    @Test
    void getTaskTypeById() {
        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        ArrayList<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 1);
        newEpicTask = epicTasks.get(0);

        assertEquals(taskManager.getTaskTypeById(newEpicTask.getTaskID()), TaskTypes.EPIC_TASK);
    }

    @Test
    void updateTask() {
        Task newTask = new Task("Task");
        taskManager.addTask(newTask);
        ArrayList<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(tasks.size(), 1);

        newTask = tasks.get(0);
        newTask.setTaskName("TaskName");
        newTask.setTaskDescription("TaskDesc");
        taskManager.updateTask(newTask);

        tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(tasks.size(), 1);

        newTask = tasks.get(0);
        assertEquals(newTask.getTaskName(), "TaskName");
        assertEquals(newTask.getTaskDescription(), "TaskDesc");
    }
}