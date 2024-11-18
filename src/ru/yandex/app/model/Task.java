package ru.yandex.app.model;

import ru.yandex.app.service.TaskToString;

public class Task implements TaskToString {

    protected int taskID = -1;

    protected String taskName;

    protected String taskDescription;

    protected TaskState taskState = TaskState.NEW; //По умолчанию всегда инициализируем NEW

    public Task(String taskName) {
        this.taskName = taskName;
    }

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public Task(String taskName, String taskDescription, TaskState taskState) {
        this.taskName = taskName;
        this.taskState = taskState;
        this.taskDescription = taskDescription;
    }

    public Task(int taskId, String taskName, String taskDescription) {
        this.taskID = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public Task(int taskId, String taskName, String taskDescription, TaskState taskState) {
        this.taskID = taskId;
        this.taskName = taskName;
        this.taskState = taskState;
        this.taskDescription = taskDescription;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getTaskID() {
        return taskID;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskID == task.taskID
                && taskName.equals(task.taskName)
                && taskDescription.equals(task.taskDescription)
                && taskState == task.taskState;
    }

    @Override
    public int hashCode() {
        return taskID + taskName.hashCode() + taskDescription.hashCode() + taskState.hashCode(); //Пока что не нужно вычислять контрольную сумму - достаточно только ID таска
    }

    @Override
    public String toString() {
        return "ru.yandex.app.model.Task{" +
                "taskID=" + taskID +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskState=" + taskState +
                '}';
    }

    @Override
    public String taskToString() {
        StringBuilder sb = new StringBuilder();

        sb.append(TaskTypes.TASK);
        sb.append(",");
        sb.append(taskID);
        sb.append(",");
        sb.append(taskName);
        sb.append(",");
        sb.append(taskDescription);
        sb.append(",");
        sb.append(taskState);

        return sb.toString();
    }
}