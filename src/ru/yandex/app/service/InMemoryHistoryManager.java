package ru.yandex.app.service;

import ru.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int historySize = 10;

    List<Task> taskHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (taskHistory.size() == historySize) {
                taskHistory.removeFirst();
            }

            taskHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(taskHistory);
    }
}
