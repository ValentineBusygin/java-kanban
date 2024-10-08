package ru.yandex.app.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    InMemoryHistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void getHistory() {
        List<Task> tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(tasksHistory.size(), 0);

        Task newTask = new Task("TestTask");
        historyManager.add(newTask);
        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(tasksHistory.size(), 1);
        assertEquals(tasksHistory.get(0), newTask);

        historyManager.add(newTask); //2
        historyManager.add(newTask); //3
        historyManager.add(newTask); //4
        historyManager.add(newTask); //5
        historyManager.add(newTask); //6
        historyManager.add(newTask); //7
        historyManager.add(newTask); //8
        historyManager.add(newTask); //9
        historyManager.add(newTask); //10
        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(tasksHistory.size(), 10);

        historyManager.add(newTask); //11
        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(tasksHistory.size(), 10);
    }
}