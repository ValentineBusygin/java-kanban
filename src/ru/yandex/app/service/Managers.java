package ru.yandex.app.service;

import ru.yandex.app.model.TaskManagerTypes;

public class Managers {

    private Managers() {

    }

    public static TaskManager createTaskManger(TaskManagerTypes taskManagerType) {
        switch (taskManagerType) {
            case IN_MEMORY_TASK_MANAGER:
                return new InMemoryTaskManager();
            case FILE_BACKED_TASK_MANAGER:
                return new FileBackedTaskManager("./tst.csv");
            default:
                return getDefault();
        }
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
