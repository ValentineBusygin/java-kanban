package ru.yandex.app.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    InMemoryHistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void getHistory() {
        ArrayList<Task> tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(tasksHistory.size(), 0);

        Task newTask = new Task("TestTask");
        historyManager.add(newTask);
        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(tasksHistory.size(), 1);
        assertEquals(tasksHistory.get(0), newTask);
    }
}