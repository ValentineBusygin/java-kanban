package ru.yandex.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    InMemoryTaskManager taskManager;

    @BeforeEach
    void initTest() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void workWithTask() {
        List<Task> tasks = taskManager.getTasks();
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
        List<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 0);

        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 1);

        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID
        assertEquals(newEpicTask.getTaskState(), TaskState.NEW);

        SubTask newSubTask = new SubTask("SubTask", "SubTaskDesc", TaskState.IN_PROGRESS, newEpicTask.getTaskID());
        taskManager.addSubTask(newSubTask);
        List<SubTask> epicSubTasks = taskManager.getEpicSubTasks(newEpicTask.getTaskID());
        assertNotNull(epicSubTasks);
        assertEquals(epicSubTasks.size(), 1);
        assertEquals(epicSubTasks.get(0), newSubTask);

        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);

        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID
        assertEquals(newEpicTask.getTaskState(), TaskState.IN_PROGRESS);

        taskManager.removeSubTask(newSubTask.getTaskID());
        epicTasks = taskManager.getEpicTasks();
        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID
        assertEquals(newEpicTask.getTaskState(), TaskState.NEW);

        newSubTask.setTaskState(TaskState.DONE);
        taskManager.addSubTask(newSubTask);
        taskManager.addSubTask(newSubTask);

        epicTasks = taskManager.getEpicTasks();
        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID
        assertEquals(newEpicTask.getTaskState(), TaskState.DONE);

        taskManager.removeEpicTask(newEpicTask.getTaskID());
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 0);
    }

    @Test
    void clearAllTasks() {
        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        List<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 1);

        SubTask newSubTask = new SubTask("SubTask", "SubTaskDesc", newEpicTask.getTaskID());
        taskManager.addSubTask(newSubTask);
        List<SubTask> subTasks = taskManager.getSubTasks();
        assertNotNull(subTasks);
        assertEquals(subTasks.size(), 1);

        Task newTask = new Task("Task");
        taskManager.addTask(newTask);
        List<Task> tasks = taskManager.getTasks();
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
    void removeSubTaskFromEpic() {
        List<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 0);

        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 1);

        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID

        SubTask newSubTask = new SubTask("SubTask", "SubTaskDesc", TaskState.IN_PROGRESS, newEpicTask.getTaskID());
        taskManager.addSubTask(newSubTask); //subtask id = 1
        List<SubTask> epicSubTasks = taskManager.getEpicSubTasks(newEpicTask.getTaskID());
        assertNotNull(epicSubTasks);
        assertEquals(epicSubTasks.size(), 1);
        assertEquals(epicSubTasks.get(0), newSubTask);

        newSubTask = epicSubTasks.get(0);
        taskManager.removeSubTask(newSubTask.getTaskID()); //removes 1

        newSubTask.setTaskState(TaskState.DONE);
        taskManager.addSubTask(newSubTask); //subtask id = 2
        taskManager.addSubTask(newSubTask); //subtask id = 3
        taskManager.addSubTask(newSubTask); //subtask id = 4

        epicSubTasks = taskManager.getEpicSubTasks(newEpicTask.getTaskID());
        newSubTask = epicSubTasks.getLast();
        taskManager.removeSubTask(newSubTask.getTaskID()); //removes 4

        taskManager.addSubTask(newSubTask); //subtask id = 5

        epicTasks = taskManager.getEpicTasks();
        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID
        assertEquals(newEpicTask.getTaskState(), TaskState.DONE);

        List<SubTask> subTasks = taskManager.getEpicSubTasks(newEpicTask.getTaskID());
        for (SubTask subTask : subTasks) {
            assertNotEquals(1, subTask.getTaskID());
            assertNotEquals(4, subTask.getTaskID());
        }

        taskManager.removeEpicTask(newEpicTask.getTaskID());
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 0);

    }

    @Test
    void getTaskTypeById() {
        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        List<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(epicTasks.size(), 1);
        newEpicTask = epicTasks.get(0);

        assertEquals(taskManager.getTaskTypeById(newEpicTask.getTaskID()), TaskTypes.EPIC_TASK);
    }

    @Test
    void updateTask() {
        Task newTask = new Task("Task");
        taskManager.addTask(newTask);
        List<Task> tasks = taskManager.getTasks();
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