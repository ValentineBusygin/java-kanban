package ru.yandex.app.service;

import java.util.HashMap;
import java.util.ArrayList;

import ru.yandex.app.model.*;

public class TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    private int nextTaskId = 1;

    public void addTask(Task task) {
        int taskId = getNextTaskId();
        task.setTaskID(taskId);

        tasks.put(taskId, task);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void removeTask(int taskID) {
        tasks.remove(taskID);
    }

    public void addEpicTask(EpicTask epicTask) {
        int epicTaskId = getNextTaskId();
        epicTask.setTaskID(epicTaskId);

        epicTasks.put(epicTaskId, epicTask);
    }

    public ArrayList<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<SubTask> getEpicSubTasks(int epicTaskId) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();

        EpicTask epicTask = epicTasks.get(epicTaskId);
        if (epicTask != null) {
            for (int epicSubTaskId : epicTask.getSubTaskIds()) {
                epicSubTasks.add(subTasks.get(epicSubTaskId));
            }
        }

        return epicSubTasks;
    }

    public void removeEpicTask(int epicTaskId) {
        EpicTask epicTask = epicTasks.get(epicTaskId);
        if (epicTask != null) {
            removeEpicTask(epicTask);
        }
    }

    public boolean isEpicTaskExists(int epicTaskId) {
        return epicTasks.containsKey(epicTaskId);
    }

    public boolean isSubTaskExists(int subTaskId) {
        return subTasks.containsKey(subTaskId);
    }

    public void addSubTask(SubTask subTask) {
        int subTaskId = getNextTaskId();
        subTask.setTaskID(subTaskId);

        subTasks.put(subTaskId, subTask);

        EpicTask subTaskEpic = epicTasks.get(subTask.getEpicId());
        if (subTaskEpic != null) {
            subTaskEpic.addSubTask(subTaskId);
            recalculateEpicStatus(subTaskEpic);
        }
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void removeSubTask(int subTaskID) {
        SubTask subTask = subTasks.get(subTaskID);
        if (subTask != null) {
            removeSubTask(subTask);
        }
    }

    public void clearAllTasks() {
        clearTasks();
        clearEpicTasks();
        clearSubTasks();
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpicTasks() {
        subTasks.clear();
        epicTasks.clear();
    }

    public void clearSubTasks() {
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.cleanSubTasks();
        }

        subTasks.clear();
    }

    public TaskTypes getTaskTypeById(int taskId) {
        if (tasks.containsKey(taskId)) {
            return TaskTypes.TASK;
        }
        if (epicTasks.containsKey(taskId)) {
            return TaskTypes.EPIC_TASK;
        }
        if (subTasks.containsKey(taskId)) {
            return TaskTypes.SUB_TASK;
        }

        return null;
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public EpicTask getEpicTaskById(int epicId) {
        return epicTasks.get(epicId);
    }

    public SubTask getSubTaskById(int subTaskId) {
        return subTasks.get(subTaskId);
    }

    public void updateTask(Task newTask) {
        int taskId = newTask.getTaskID();
        if (tasks.containsKey(taskId)) {
            Task oldTask = tasks.get(taskId);
            if (oldTask.equals(newTask)) {
                return;
            }

            tasks.replace(taskId, newTask);
        }
    }

    public void updateEpicTask(EpicTask newEpicTask) {
        int epicTaskId = newEpicTask.getTaskID();
            final EpicTask oldEpicTask = epicTasks.get(epicTaskId);
            if (oldEpicTask == null) {
                return;
            }

            if (oldEpicTask.equals(newEpicTask)) {
                return;
            }

            oldEpicTask.setTaskName(newEpicTask.getTaskName());
            oldEpicTask.setTaskDescription(newEpicTask.getTaskDescription());

            recalculateEpicStatus(oldEpicTask);
    }

    public void updateSubTask(SubTask newSubTask) {
        int subTaskId = newSubTask.getTaskID();
        if (subTasks.containsKey(subTaskId)) {
            SubTask oldSubTask = subTasks.get(subTaskId);
            if (oldSubTask.equals(newSubTask)) {
                return;
            }

            //Обновляем массивы подзадач в эпиках
            //В ТЗ нет четкого указания на состав обновляемых полей - поэтому проверяем/обновляем вообще всё
            if (oldSubTask.getEpicId() != newSubTask.getEpicId()) {
                EpicTask oldSubTaskEpic = epicTasks.get(oldSubTask.getEpicId());
                oldSubTaskEpic.removeSubTask(subTaskId);
                recalculateEpicStatus(oldSubTaskEpic);
                EpicTask newSubTaskEpic = epicTasks.get(newSubTask.getEpicId());
                newSubTaskEpic.addSubTask(subTaskId);
                recalculateEpicStatus(newSubTaskEpic);
            }

            subTasks.replace(subTaskId, newSubTask);
        }
    }

    private int getNextTaskId() {
        return nextTaskId++;
    }

    private void removeEpicTask(EpicTask epicTask) {
        int epicTaskId = epicTask.getTaskID();

        if (epicTaskId != -1) {

            ArrayList<Integer> epicSubTaskIds = epicTask.getSubTaskIds();
            for (int epicSubTaskId : epicSubTaskIds) {
                subTasks.remove(epicSubTaskId);
            }

            epicTasks.remove(epicTaskId);
        }
    }

    private void removeSubTask(SubTask subTask) {
        int subTaskId = subTask.getTaskID();
        int subTaskEpicId = subTask.getEpicId();

        EpicTask subTaskEpicTask = epicTasks.get(subTaskEpicId);
        if (subTaskEpicTask != null) {
            subTaskEpicTask.removeSubTask(subTaskId);
            recalculateEpicStatus(subTaskEpicTask);
        }

        subTasks.remove(subTaskId);
    }

    private void recalculateEpicStatus(EpicTask epicTask) {
        ArrayList<Integer> subTaskIds = epicTask.getSubTaskIds();

        if (subTaskIds.isEmpty()) {
            epicTask.setTaskState(TaskState.NEW);
            return;
        }

        boolean epicDone = true;
        boolean epicNew = true;

        for (int subTaskId : subTaskIds) {
            SubTask subTask = subTasks.get(subTaskId);
            switch (subTask.getTaskState()) {
                case NEW:
                    epicDone = false;
                    break;
                case IN_PROGRESS:
                    epicTask.setTaskState(TaskState.NEW);
                    return;
                case DONE:
                    epicNew = false;
                    break;
            }
        }

        if (epicDone) {
            epicTask.setTaskState(TaskState.DONE);
        } else if (epicNew) {
            epicTask.setTaskState(TaskState.NEW);
        } else {
            epicTask.setTaskState(TaskState.IN_PROGRESS);
        }
    }
}