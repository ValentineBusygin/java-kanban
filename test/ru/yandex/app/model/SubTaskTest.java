package ru.yandex.app.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void getEpicId() {
        SubTask newSubTask = new SubTask("getEpicId","getEpicIdDesc", 100500);
        assertEquals(newSubTask.getEpicId(), 100500);
    }

    @Test
    void setEpicId() {
        SubTask newSubTask = new SubTask("getEpicId","getEpicIdDesc", 100500);
        newSubTask.setEpicId(1050);
        assertEquals(newSubTask.getEpicId(), 1050);
    }
}