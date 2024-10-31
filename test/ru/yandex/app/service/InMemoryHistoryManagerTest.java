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

        Task newTask = new Task(0,"TestTask","TestTask");
        historyManager.add(newTask);
        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(tasksHistory.size(), 1);
        assertEquals(tasksHistory.get(0), newTask);

        for (int i = 1; i < 10; i++) {
            newTask = new Task(i,"TestTask","TestTask");
            historyManager.add(newTask);
        }

        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(tasksHistory.size(), 10);

        newTask = new Task(11,"TestTask","TestTask");
        historyManager.add(newTask); //11
        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(tasksHistory.size(), 11);

        historyManager.remove(11);
        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(tasksHistory.size(), 10);
    }
}