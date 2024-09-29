package ru.yandex.app.model;

import java.util.Objects;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String taskName, String taskDescription, int epicId) {
        super(taskName, taskDescription, TaskState.NEW);
        this.epicId = epicId;
    }

    public SubTask(String taskName, String taskDescription, TaskState taskState, int epicId) {
        super(taskName, taskDescription, taskState);
        this.epicId = epicId;
    }

    public SubTask(int taskId, String taskName, String taskDescription, int epicId) {
        super(taskId, taskName, taskDescription, TaskState.NEW);
        this.epicId = epicId;
    }

    public SubTask(int taskId, String taskName, String taskDescription, TaskState taskState, int epicId) {
        super(taskId, taskName, taskDescription, taskState);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "ru.yandex.app.model.SubTask{" +
                "taskID=" + taskID +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskState=" + taskState +
                ", epicId=" + epicId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
