package ru.yandex.app.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task implements Comparable<Task> {

    protected int taskID = -1;

    protected String taskName;

    protected String taskDescription;

    protected TaskState taskState = TaskState.NEW; //По умолчанию всегда инициализируем NEW

    protected LocalDateTime startTime;

    protected Duration duration;

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

    public Task(int taskID, String taskName, String taskDescription, TaskState taskState, LocalDateTime startTime, Duration duration) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskState = taskState;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String taskName, String taskDescription, TaskState taskState, LocalDateTime startTime, Duration duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskState = taskState;
        this.startTime = startTime;
        this.duration = duration;
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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
    public int compareTo(Task task) {
        return startTime.compareTo(task.getStartTime());
    }

    public String taskToString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getType());
        sb.append(",");
        sb.append(taskID);
        sb.append(",");
        sb.append(taskName);
        sb.append(",");
        sb.append(taskDescription);
        sb.append(",");
        sb.append(taskState);
        sb.append(",");
        sb.append(startTime == null ? " " : startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        sb.append(",");
        sb.append(duration == null ? " " : duration.toMinutes());

        return sb.toString();
    }

    public LocalDateTime getEndTime() {
        return startTime == null || duration == null ? null : startTime.plus(duration);
    }

    protected TaskTypes getType() {
        return TaskTypes.TASK;
    }
}