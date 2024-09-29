package ru.yandex.app.model;

import java.util.ArrayList;
import java.util.Objects;

public class EpicTask extends Task {

    private final ArrayList<Integer> subTaskIds = new ArrayList<>();

    public EpicTask(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskState.NEW);
    }

    public EpicTask(int taskId, String taskName, String taskDescription) {
        super(taskId, taskName, taskDescription, TaskState.NEW);
    }

    public void addSubTask(Integer subTaskID) {
        subTaskIds.add(subTaskID);
    }

    public void cleanSubTasks() {
        subTaskIds.clear();
    }

    public void removeSubTask(Integer subTaskID) {
        subTaskIds.remove(subTaskID);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    @Override
    public String toString() {
        return "ru.yandex.app.model.EpicTask{"  +
                "taskID=" + taskID +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskState=" + taskState +
                ", subTaskIds=" + subTaskIds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EpicTask epicTask = (EpicTask) o;
        return Objects.equals(subTaskIds, epicTask.subTaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIds);
    }
}
