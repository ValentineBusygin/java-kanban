package ru.yandex.app.service;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import ru.yandex.app.model.*;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    private final HistoryManager historyManager;

    private int nextTaskId = 1;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(Task task) {
        int taskId = getNextTaskId();
        task.setTaskID(taskId);

        tasks.put(taskId, task);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeTask(int taskID) {
        tasks.remove(taskID);
        historyManager.remove(taskID);
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        int epicTaskId = getNextTaskId();
        epicTask.setTaskID(epicTaskId);

        epicTasks.put(epicTaskId, epicTask);
    }

    @Override
    public List<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicTaskId) {
        List<SubTask> epicSubTasks = new ArrayList<>();

        EpicTask epicTask = epicTasks.get(epicTaskId);
        if (epicTask != null) {
            for (int epicSubTaskId : epicTask.getSubTaskIds()) {
                epicSubTasks.add(subTasks.get(epicSubTaskId));
            }
        }

        return epicSubTasks;
    }

    @Override
    public void removeEpicTask(int epicTaskId) {
        EpicTask epicTask = epicTasks.get(epicTaskId);
        if (epicTask != null) {
            removeEpicTask(epicTask);
        }
    }

    @Override
    public boolean isEpicTaskExists(int epicTaskId) {
        return epicTasks.containsKey(epicTaskId);
    }

    @Override
    public boolean isSubTaskExists(int subTaskId) {
        return subTasks.containsKey(subTaskId);
    }

    @Override
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

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeSubTask(int subTaskID) {
        SubTask subTask = subTasks.get(subTaskID);
        if (subTask != null) {
            removeSubTask(subTask);
        }
    }

    @Override
    public void clearAllTasks() {
        clearTasks();
        clearEpicTasks();
        clearSubTasks();
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpicTasks() {
        subTasks.clear();
        epicTasks.clear();
    }

    @Override
    public void clearSubTasks() {
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.cleanSubTasks();
        }

        subTasks.clear();
    }

    @Override
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

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicTaskById(int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        historyManager.add(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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
                historyManager.remove(epicSubTaskId);
            }

            epicTasks.remove(epicTaskId);
            historyManager.remove(epicTaskId);
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
        historyManager.remove(subTaskId);
    }

    private void recalculateEpicStatus(EpicTask epicTask) {
        List<Integer> subTaskIds = epicTask.getSubTaskIds();

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
                    epicTask.setTaskState(TaskState.IN_PROGRESS);
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