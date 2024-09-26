import java.util.ArrayList;
import java.util.Objects;

public class EpicTask extends Task {

    protected ArrayList<Integer> subTaskIds = new ArrayList<>();

    public EpicTask(String taskName) {
        super(taskName);
    }

    public EpicTask(String taskName, TaskState taskState) {
        super(taskName, taskState);
    }

    public EpicTask(int taskId, String taskName) {
        super(taskId, taskName);
    }

    public EpicTask(int taskId, String taskName, TaskState taskState) {
        super(taskId, taskName, taskState);
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
        return "EpicTask{"  +
                "taskID=" + taskID +
                ", taskName='" + taskName + '\'' +
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
