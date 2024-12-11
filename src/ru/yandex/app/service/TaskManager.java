package ru.yandex.app.service;

import ru.yandex.app.model.*;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    void addTask(Task task);

    List<Task> getTasks();

    void removeTask(int taskID);

    void addEpicTask(EpicTask epicTask);

    List<EpicTask> getEpicTasks();

    List<SubTask> getEpicSubTasks(int epicTaskId);

    void removeEpicTask(int epicTaskId);

    boolean isEpicTaskExists(int epicTaskId);

    boolean isSubTaskExists(int subTaskId);

    void addSubTask(SubTask subTask);

    List<SubTask> getSubTasks();

    void removeSubTask(int subTaskID);

    void clearAllTasks();

    void clearTasks();

    void clearEpicTasks();

    void clearSubTasks();

    TaskTypes getTaskTypeById(int taskId);

    Task getTaskById(int taskId);

    EpicTask getEpicTaskById(int epicId);

    SubTask getSubTaskById(int subTaskId);

    void updateTask(Task newTask);

    void updateEpicTask(EpicTask newEpicTask);

    void updateSubTask(SubTask newSubTask);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
