package ru.yandex.app.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Task;

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
        assertEquals(0, tasksHistory.size(), "Ожидался пустой массив");

        Task newTask = new Task(0,"TestTask","TestTask");
        historyManager.add(newTask);
        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(1, tasksHistory.size(), "Ожидался массив с 1 элементом");
        assertEquals(tasksHistory.get(0), newTask);

        for (int i = 1; i < 10; i++) {
            newTask = new Task(i,"TestTask","TestTask");
            historyManager.add(newTask);
        }

        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(10, tasksHistory.size(), "Ожидался массив с 10ю элементами");

        newTask = new Task(11,"TestTask","TestTask");
        historyManager.add(newTask); //11
        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(11, tasksHistory.size(), "Ожидался массив с 11ю элементами");

        historyManager.add(newTask); //11
        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(11, tasksHistory.size(), "При добавлении дубликата размер массива (11 элементов) не должен поменяться");

        historyManager.remove(11);
        tasksHistory = historyManager.getHistory();
        assertNotNull(tasksHistory);
        assertEquals(10, tasksHistory.size(), "Ожидался массив с 10ю элементами");

        historyManager.remove(1);
        tasksHistory = historyManager.getHistory();
        for (Task task : tasksHistory) {
            assertNotEquals(1, task.getTaskID(), "Ожидалось что таск с ID 1 удален из массива");
        }

        historyManager.remove(5);
        tasksHistory = historyManager.getHistory();
        for (Task task : tasksHistory) {
            assertNotEquals(5, task.getTaskID(), "Ожидалось что таск с ID 5 удален из массива");
            assertNotNull(tasksHistory);
            assertEquals(8, tasksHistory.size(), "Ожидался массив с 8 элементами");
        }
    }
}