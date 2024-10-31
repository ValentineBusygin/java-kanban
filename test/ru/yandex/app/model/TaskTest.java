package ru.yandex.app.model;

import org.junit.jupiter.api.Test;
import org.testng.Assert;

class TaskTest {

    @Test
    void testTasksEquals() {
        Task newTaskOne = new Task(100500,"getTaskID", "getTaskIDDesc");
        Task newTaskTwo = new Task(100500,"getTaskID", "getTaskIDDesc");
        Task newTaskThree = new Task(1050,"getTaskID", "getTaskIDDesc");
        Assert.assertEquals(newTaskOne, newTaskTwo);
        Assert.assertNotEquals(newTaskOne, newTaskThree);
        Assert.assertNotEquals(newTaskTwo, newTaskThree);
    }

    @Test
    void setTaskID() {
        Task newTask = new Task("setTaskID");
        newTask.setTaskID(100500);
        Assert.assertEquals(newTask.getTaskID(), 100500);
    }

    @Test
    void getTaskID() {
        Task newTask = new Task("getTaskID");
        Assert.assertEquals(newTask.getTaskID(), -1);

        Task newTaskWithID = new Task(100500,"getTaskID", "getTaskIDDesc");
        Assert.assertEquals(newTaskWithID.getTaskID(), 100500);
    }

    @Test
    void getTaskState() {
        Task newTask = new Task("getTaskState");
        Assert.assertEquals(newTask.getTaskState(), TaskState.NEW);
    }

    @Test
    void setTaskState() {
        Task newTask = new Task("setTaskState");
        newTask.setTaskState(TaskState.DONE);
        Assert.assertEquals(newTask.getTaskState(), TaskState.DONE);
    }

    @Test
    void getTaskName() {
        Task newTask = new Task("getTaskName");
        Assert.assertEquals(newTask.getTaskName(), "getTaskName");
    }

    @Test
    void setTaskName() {
        Task newTask = new Task("getTaskName");
        newTask.setTaskName("setTaskName");
        Assert.assertEquals(newTask.getTaskName(), "setTaskName");
    }

    @Test
    void getTaskDescription() {
        Task newTask = new Task("getTaskDescription", "getTaskDescription");
        Assert.assertEquals(newTask.getTaskDescription(), "getTaskDescription");
    }

    @Test
    void setTaskDescription() {
        Task newTask = new Task("setTaskDescription", "getTaskDescription");
        newTask.setTaskDescription("setTaskDescription");
        Assert.assertEquals(newTask.getTaskDescription(), "setTaskDescription");
    }
}