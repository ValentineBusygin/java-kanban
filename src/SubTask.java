import java.util.Objects;

public class SubTask extends Task {

    private int epicId = -1;

    public SubTask(String taskName) {
        super(taskName);
    }

    public SubTask(String taskName, int epicId) {
        super(taskName);
        setEpicId(epicId);
    }

    public SubTask(String taskName, TaskState taskState) {
        super(taskName, taskState);
    }

    public SubTask(String taskName, TaskState taskState, int epicId) {
        super(taskName, taskState);
        setEpicId(epicId);
    }

    public SubTask(int taskId, String taskName) {
        super(taskId, taskName);
    }

    public SubTask(int taskId, String taskName, int epicId) {
        super(taskId, taskName);
        setEpicId(epicId);
    }

    public SubTask(int taskId, String taskName, TaskState taskState) {
        super(taskId, taskName, taskState);
    }

    public SubTask(int taskId, String taskName, TaskState taskState, int epicId) {
        super(taskId, taskName, taskState);
        setEpicId(epicId);
    }


    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "taskID=" + taskID +
                ", taskName='" + taskName + '\'' +
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
