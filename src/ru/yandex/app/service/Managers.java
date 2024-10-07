package ru.yandex.app.service;

import ru.yandex.app.model.TaskManagerTypes;

public class Managers {
    public TaskManager createTaskManger(TaskManagerTypes taskManagerType) {
        switch (taskManagerType) {
            case IN_MEMORY_TASK_MANAGER:
                return new InMemoryTaskManager();
            default:
                return getDefault();
        }
    }

    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
