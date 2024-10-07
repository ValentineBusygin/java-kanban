package ru.yandex.app.model;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {

    EpicTask epic;
    @BeforeEach
    void createEpic() {
        epic = new EpicTask("EpicName", "EpicDesc");
    }

    @Test
    void addSubTask() {
        //SubTask subTask = new SubTask("subName", "subDesc", 100500);
        assertEquals(epic.getSubTaskIds().size(), 0);
        epic.addSubTask(10);
        assertEquals(epic.getSubTaskIds().size(), 1);
    }

    @Test
    void cleanSubTasks() {
        epic.addSubTask(10);
        epic.addSubTask(20);
        ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
        assertEquals(subTaskIds.size(), 2);

        epic.cleanSubTasks();
        subTaskIds = epic.getSubTaskIds();
        assertEquals(subTaskIds.size(), 0);
    }

    @Test
    void removeSubTask() {
        epic.addSubTask(10);
        ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
        assertEquals(subTaskIds.size(), 1);
        assertEquals(subTaskIds.getFirst(), 10);

        epic.addSubTask(20);
        subTaskIds = epic.getSubTaskIds();
        assertEquals(subTaskIds.size(), 2);
        assertEquals(subTaskIds.get(1), 20);

        epic.removeSubTask(10);subTaskIds = epic.getSubTaskIds();
        assertEquals(subTaskIds.size(), 1);
        assertEquals(subTaskIds.getFirst(), 20);
    }

    @Test
    void getSubTaskIds() {
        epic.addSubTask(10);
        ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
        assertEquals(subTaskIds.size(), 1);
        assertEquals(subTaskIds.getFirst(), 10);

        epic.addSubTask(20);
        subTaskIds = epic.getSubTaskIds();
        assertEquals(subTaskIds.size(), 2);
        assertEquals(subTaskIds.get(1), 20);
    }
}