package ru.yandex.app.model;

import org.junit.jupiter.api.Test;
import org.testng.Assert;

class SubTaskTest {

    @Test
    void getEpicId() {
        SubTask newSubTask = new SubTask("getEpicId","getEpicIdDesc", 100500);
        Assert.assertEquals(newSubTask.getEpicId(), 100500);
    }

    @Test
    void setEpicId() {
        SubTask newSubTask = new SubTask("getEpicId","getEpicIdDesc", 100500);
        newSubTask.setEpicId(1050);
        Assert.assertEquals(newSubTask.getEpicId(), 1050);
    }
}