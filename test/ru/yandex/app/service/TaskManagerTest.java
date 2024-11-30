package ru.yandex.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.app.exceptions.TaskAddingException;
import ru.yandex.app.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    @BeforeEach
    void initTest() {
    }

    @Test
    void workWithTask() {
        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(0 , tasks.size(), "Ожидался пустой массив задач");

        Task newTask = new Task("Task");
        assertDoesNotThrow(() -> {taskManager.addTask(newTask);}, "При добавлении таска без даты не должно быть выброшено исключение");
        tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(1 , tasks.size(), "Ожидался массив задач с 1 элементом");

        taskManager.removeTask(newTask.getTaskID());
        tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(0 , tasks.size(), "Ожидался пустой массив задач");
    }

    @Test
    void workWIthEpicTaskAndSubTask() {
        List<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size(), "Ожидался пустой массив эпиков");

        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(1, epicTasks.size(), "Ожидался массив с 1 эпиком");

        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID
        assertEquals(TaskState.NEW, newEpicTask.getTaskState(), "У эпика ожидался статус NEW");

        SubTask newSubTaskOne = new SubTask("SubTask", "SubTaskDesc", TaskState.IN_PROGRESS, newEpicTask.getTaskID());
        SubTask newSubTaskTwo = new SubTask("SubTask", "SubTaskDesc", TaskState.IN_PROGRESS, newEpicTask.getTaskID());
        assertDoesNotThrow(() -> {taskManager.addSubTask(newSubTaskOne);}, "При добавлении сабтаска без даты не должно быть выброшено исключение");
        assertDoesNotThrow(() -> {taskManager.addSubTask(newSubTaskTwo);}, "При добавлении сабтаска без даты не должно быть выброшено исключение");
        List<SubTask> epicSubTasks = taskManager.getEpicSubTasks(newEpicTask.getTaskID());
        assertNotNull(epicSubTasks);
        assertEquals(2, epicSubTasks.size(), "Ожидался массив с 2 подзадачами");

        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);

        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID
        assertEquals(TaskState.IN_PROGRESS, newEpicTask.getTaskState(), "У эпика ожидался статус IN_PROGRESS");

        taskManager.removeSubTask(newSubTaskOne.getTaskID());
        taskManager.removeSubTask(newSubTaskTwo.getTaskID());
        epicTasks = taskManager.getEpicTasks();
        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID
        assertEquals(TaskState.NEW, newEpicTask.getTaskState(), "У эпика ожидался статус NEW");

        newSubTaskOne.setTaskState(TaskState.DONE);
        newSubTaskTwo.setTaskState(TaskState.DONE);;
        assertDoesNotThrow(() -> {taskManager.addSubTask(newSubTaskOne);}, "При добавлении сабтаска без даты не должно быть выброшено исключение");
        assertDoesNotThrow(() -> {taskManager.addSubTask(newSubTaskTwo);}, "При добавлении сабтаска без даты не должно быть выброшено исключение");

        epicTasks = taskManager.getEpicTasks();
        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID
        assertEquals(TaskState.DONE, newEpicTask.getTaskState(), "У эпика ожидался статус DONE");

        taskManager.removeSubTask(newSubTaskTwo.getTaskID());
        newSubTaskTwo.setTaskState(TaskState.IN_PROGRESS);
        assertDoesNotThrow(() -> {taskManager.addSubTask(newSubTaskTwo);}, "При добавлении сабтаска без даты не должно быть выброшено исключение");

        epicTasks = taskManager.getEpicTasks();
        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID
        assertEquals(TaskState.IN_PROGRESS, newEpicTask.getTaskState(), "У эпика ожидался статус IN_PROGRESS");

        taskManager.removeSubTask(newSubTaskTwo.getTaskID());
        newSubTaskTwo.setTaskState(TaskState.NEW);
        assertDoesNotThrow(() -> {taskManager.addSubTask(newSubTaskTwo);}, "При добавлении сабтаска без даты не должно быть выброшено исключение");

        epicTasks = taskManager.getEpicTasks();
        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID
        assertEquals(TaskState.IN_PROGRESS, newEpicTask.getTaskState(), "У эпика ожидался статус IN_PROGRESS");

        taskManager.removeEpicTask(newEpicTask.getTaskID());
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size(), "Ожидался пустой массив эпиков");

        List<SubTask> subTasks = taskManager.getSubTasks();
        assertNotNull(epicTasks);
        assertEquals(0, subTasks.size(), "Ожидался пустой массив подзадач");
    }

    @Test
    void clearAllTasks() {
        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        List<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(1, epicTasks.size(), "Ожидался массив с 1 эпиком");

        SubTask newSubTask = new SubTask("SubTask", "SubTaskDesc", newEpicTask.getTaskID());
        assertDoesNotThrow(() -> {taskManager.addSubTask(newSubTask);}, "При добавлении сабтаска без даты не должно быть выброшено исключение");
        List<SubTask> subTasks = taskManager.getSubTasks();
        assertNotNull(subTasks);
        assertEquals(1, subTasks.size(), "Ожидался массив с 1 подзадачей");

        Task newTask = new Task("Task");
        taskManager.addTask(newTask);
        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size(), "Ожидался массив с 1 задачей");

        taskManager.clearAllTasks();
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size(), "Ожидался пустой массив эпиков");
        subTasks = taskManager.getSubTasks();
        assertNotNull(subTasks);
        assertEquals(0, subTasks.size(), "Ожидался пустой массив подзадач");
        tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(0, tasks.size(), "Ожидался пустой массив задач");
    }

    @Test
    void removeSubTaskFromEpic() {
        List<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size(), "Ожидался пустой массив эпиков");

        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(1, epicTasks.size(), "Ожидался массив с 1 эпиком");

        newEpicTask = epicTasks.get(0); //Adding epic task changes task ID

        SubTask newSubTask = new SubTask("SubTask", "SubTaskDesc", TaskState.IN_PROGRESS, newEpicTask.getTaskID());
        taskManager.addSubTask(newSubTask); //subtask id = 1
        List<SubTask> epicSubTasks = taskManager.getEpicSubTasks(newEpicTask.getTaskID());
        assertNotNull(epicSubTasks);
        assertEquals(1, epicSubTasks.size(), "Ожидался массив с 1 эпиком");
        assertEquals(newSubTask, epicSubTasks.get(0), "Ожидалось, что в эпике лежит та же задача");

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
        assertEquals(TaskState.DONE, newEpicTask.getTaskState(), "У эпика ожидался статус DONE");

        List<SubTask> subTasks = taskManager.getEpicSubTasks(newEpicTask.getTaskID());
        for (SubTask subTask : subTasks) {
            assertNotEquals(1, subTask.getTaskID(), "Ожидалось, что в задача с ID 1 удалена");
            assertNotEquals(4, subTask.getTaskID(), "Ожидалось, что в задача с ID 4 удалена");
        }

        taskManager.removeEpicTask(newEpicTask.getTaskID());
        epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size(), "Ожидался пустой массив эпиков");

    }

    @Test
    void getTaskTypeById() {
        EpicTask newEpicTask = new EpicTask("EpicTask", "EpicDesc");
        taskManager.addEpicTask(newEpicTask);
        List<EpicTask> epicTasks = taskManager.getEpicTasks();
        assertNotNull(epicTasks);
        assertEquals(1, epicTasks.size(), "Ожидался массив с 1 эпиком");
        newEpicTask = epicTasks.get(0);

        assertEquals(TaskTypes.EPIC_TASK, taskManager.getTaskTypeById(newEpicTask.getTaskID()), "Ожидалось, что вернется тип EPIC_TASK");
    }

    @Test
    void updateTask() {
        Task newTask = new Task("Task");
        taskManager.addTask(newTask);
        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size(), "Ожидался массив с 1 задачей");

        newTask = tasks.get(0);
        newTask.setTaskName("TaskName");
        newTask.setTaskDescription("TaskDesc");
        taskManager.updateTask(newTask);

        tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size(), "Ожидался массив с 1 задачей");

        newTask = tasks.get(0);
        assertEquals(newTask.getTaskName(), "TaskName", "Ожидалось, что имя задачи будет изменено на \"TaskName\"");
        assertEquals(newTask.getTaskDescription(), "TaskDesc", "Ожидалось, что описание задачи будет изменено на \"TaskDesc\"");
    }

    @Test
    void checkIntervals() {

        LocalDateTime now = LocalDateTime.now();
        //Четкое совпадение начала и конца
        Task newTask = new Task("Task", "TaskDesc", TaskState.NEW, now, Duration.ofMinutes(30));
        taskManager.addTask(newTask);
        assertThrows(TaskAddingException.class, () -> {taskManager.addTask(newTask);}, "При пересечении задач должно быть выброшено исключение");

        //Конец нового таска попадает в существующий
        Task newTaskTwo = new Task("Task", "TaskDesc", TaskState.NEW, now.minusMinutes(10), Duration.ofMinutes(30));
        assertThrows(TaskAddingException.class, () -> {taskManager.addTask(newTaskTwo);}, "При пересечении задач должно быть выброшено исключение: Конец нового таска попадает в существующий");

        //Начало нового таска попадает в существующий
        Task newTaskThree = new Task("Task", "TaskDesc", TaskState.NEW, now.plusMinutes(10), Duration.ofMinutes(30));
        assertThrows(TaskAddingException.class, () -> {taskManager.addTask(newTaskThree);}, "При пересечении задач должно быть выброшено исключение: Начало нового таска попадает в существующий");;

        //Новый таск целиком внутри существующего
        Task newTaskFour = new Task("Task", "TaskDesc", TaskState.NEW, now.plusMinutes(10), Duration.ofMinutes(10));
        assertThrows(TaskAddingException.class, () -> {taskManager.addTask(newTaskFour);}, "При пересечении задач должно быть выброшено исключение: Новый таск целиком внутри существующего");

        //Новый таск вокруг существующего
        Task newTaskFive = new Task("Task", "TaskDesc", TaskState.NEW, now.minusMinutes(10), Duration.ofMinutes(50));
        assertThrows(TaskAddingException.class, () -> {taskManager.addTask(newTaskFive);}, "При пересечении задач должно быть выброшено исключение: Новый таск вокруг существующего");

        //Не пересекающийся таск
        Task newTaskSix = new Task("Task", "TaskDesc", TaskState.NEW, now.plusDays(1), Duration.ofMinutes(30));
        assertDoesNotThrow(() -> {taskManager.addTask(newTaskSix);}, "При непересечении задач не должно быть выброшено исключение: ");
    }
}
