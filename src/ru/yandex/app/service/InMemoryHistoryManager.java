package ru.yandex.app.service;

import ru.yandex.app.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    ArrayList<Task> taskHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (taskHistory.size() == 10) {
            taskHistory.removeFirst();
        }

        taskHistory.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory;
    }
}
