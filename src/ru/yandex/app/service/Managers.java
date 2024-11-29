package ru.yandex.app.service;

import ru.yandex.app.model.TaskManagerTypes;

public class Managers {

    private Managers() {

    }

    public static TaskManager createTaskManger(TaskManagerTypes taskManagerType) {
        return switch (taskManagerType) {
            case IN_MEMORY_TASK_MANAGER -> new InMemoryTaskManager();
            case FILE_BACKED_TASK_MANAGER -> new FileBackedTaskManager("./tst.csv");
            default -> getDefault();
        };
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
