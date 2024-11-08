package ru.yandex.app.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testTasksEquals() {
        Task newTaskOne = new Task(100500,"getTaskID", "getTaskIDDesc");
        Task newTaskTwo = new Task(100500,"getTaskID", "getTaskIDDesc");
        Task newTaskThree = new Task(1050,"getTaskID", "getTaskIDDesc");
        assertEquals(newTaskOne, newTaskTwo);
        assertNotEquals(newTaskOne, newTaskThree);
        assertNotEquals(newTaskTwo, newTaskThree);
    }

    @Test
    void setTaskID() {
        Task newTask = new Task("setTaskID");
        newTask.setTaskID(100500);
        assertEquals(newTask.getTaskID(), 100500);
    }

    @Test
    void getTaskID() {
        Task newTask = new Task("getTaskID");
        assertEquals(newTask.getTaskID(), -1);

        Task newTaskWithID = new Task(100500,"getTaskID", "getTaskIDDesc");
        assertEquals(newTaskWithID.getTaskID(), 100500);
    }

    @Test
    void getTaskState() {
        Task newTask = new Task("getTaskState");
        assertEquals(newTask.getTaskState(), TaskState.NEW);
    }

    @Test
    void setTaskState() {
        Task newTask = new Task("setTaskState");
        newTask.setTaskState(TaskState.DONE);
        assertEquals(newTask.getTaskState(), TaskState.DONE);
    }

    @Test
    void getTaskName() {
        Task newTask = new Task("getTaskName");
        assertEquals(newTask.getTaskName(), "getTaskName");
    }

    @Test
    void setTaskName() {
        Task newTask = new Task("getTaskName");
        newTask.setTaskName("setTaskName");
        assertEquals(newTask.getTaskName(), "setTaskName");
    }

    @Test
    void getTaskDescription() {
        Task newTask = new Task("getTaskDescription", "getTaskDescription");
        assertEquals(newTask.getTaskDescription(), "getTaskDescription");
    }

    @Test
    void setTaskDescription() {
        Task newTask = new Task("setTaskDescription", "getTaskDescription");
        newTask.setTaskDescription("setTaskDescription");
        assertEquals(newTask.getTaskDescription(), "setTaskDescription");
    }
}