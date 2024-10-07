package ru.yandex.app.service;

import ru.yandex.app.model.*;

import java.util.ArrayList;

public interface TaskManager {
    void addTask(Task task);

    ArrayList<Task> getTasks();

    void removeTask(int taskID);

    void addEpicTask(EpicTask epicTask);

    ArrayList<EpicTask> getEpicTasks();

    ArrayList<SubTask> getEpicSubTasks(int epicTaskId);

    void removeEpicTask(int epicTaskId);

    boolean isEpicTaskExists(int epicTaskId);

    boolean isSubTaskExists(int subTaskId);

    void addSubTask(SubTask subTask);

    ArrayList<SubTask> getSubTasks();

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

    ArrayList<Task> getHistory();
}
