public class Task {

    protected int taskID = -1;

    protected String taskName;

    protected TaskState taskState = TaskState.NEW; //По умолчанию всегда инициализируем NEW

    public Task(String taskName) {
        this.taskName = taskName;
    }

    public Task(String taskName, TaskState taskState) {
        this.taskName = taskName;
        this.taskState = taskState;
    }

    public Task(int taskId, String taskName) {
        this.taskID = taskId;
        this.taskName = taskName;
    }

    public Task(int taskId, String taskName, TaskState taskState) {
        this.taskID = taskId;
        this.taskName = taskName;
        this.taskState = taskState;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskID == task.taskID && taskName.equals(task.taskName) && taskState == task.taskState;
    }

    @Override
    public int hashCode() {
        return taskID + taskName.hashCode() + taskState.hashCode(); //Пока что не нужно вычислять контрольную сумму - достаточно только ID таска
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskID=" + taskID +
                ", taskName='" + taskName + '\'' +
                ", taskState=" + taskState +
                '}';
    }
}