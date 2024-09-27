package ru.yandex.app.service;

import java.util.HashMap;
import java.util.ArrayList;
import ru.yandex.app.model.*;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
    private HashMap<Integer, EpicTask> epicTasks = new HashMap<Integer, EpicTask>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<Integer, SubTask>();

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
                SubTask epicSubTask = subTasks.get(epicSubTaskId);
                if (epicSubTask != null) {
                    epicSubTasks.add(epicSubTask);
                }
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
        //Сначала убираем все подзадачи и ссылки на них, потом очищаем эпики
        clearSubTasks();
        epicTasks.clear();
    }

    public void clearSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            EpicTask subTaskEpicTask = epicTasks.get(subTask.getEpicId());
            subTaskEpicTask.removeSubTask(subTask.getTaskID());
        }

        subTasks.clear();
    }

    public void showAllTasks() {
        showTasks();
        showEpicTasks();
        showSubTasks();
    }

    public void showTasks() {
        System.out.println(tasks);
    }

    public void showEpicTasks() {
        System.out.println(epicTasks);
    }

    public void showSubTasks() {
        System.out.println(subTasks);
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
        if (tasks.containsKey(taskId)) {
            return tasks.get(taskId);
        }

        return null;
    }

    public EpicTask getEpicTaskById(int epicId) {
        if (epicTasks.containsKey(epicId)) {
            return epicTasks.get(epicId);
        }

        return null;
    }

    public SubTask getSubTaskById(int subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            return subTasks.get(subTaskId);
        }

        return null;
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
        if (epicTasks.containsKey(epicTaskId)) {
            EpicTask oldEpicTask = epicTasks.get(epicTaskId);
            if (oldEpicTask.equals(newEpicTask)) {
                return;
            }

            //Обновляем ID эпиков в subtask`ах
            //В ТЗ нет четкого указания на состав обновляемых полей - поэтому проверяем/обновляем вообще всё
            ArrayList<Integer> oldEpicSubTaskIds = oldEpicTask.getSubTaskIds();
            ArrayList<Integer> newEpicSubTaskIds = newEpicTask.getSubTaskIds();
            if (!oldEpicSubTaskIds.equals(newEpicSubTaskIds)) {

                //Создаем копию массива подзадач "старого" эпика, потому что в дальнейшем потребуется фильтрация по
                // полному списку
                ArrayList<Integer> subTasksForRemoveEpic = (ArrayList) oldEpicSubTaskIds.clone();
                if (subTasksForRemoveEpic.removeAll(newEpicSubTaskIds)) {
                    for (int subTaskId : subTasksForRemoveEpic) {
                        SubTask subTask = subTasks.get(subTaskId);
                        subTask.setEpicId(-1);
                    }
                }

                //Создаем копию массива подзадач "нового" для единообразия обработки
                ArrayList<Integer> subTasksForAddEpic = (ArrayList) newEpicSubTaskIds.clone();
                if (subTasksForAddEpic.removeAll(oldEpicSubTaskIds)) {
                    for (int subTaskId : subTasksForAddEpic) {
                        SubTask subTask = subTasks.get(subTaskId);
                        subTask.setEpicId(epicTaskId);
                    }
                }
            }

            epicTasks.replace(epicTaskId, newEpicTask);
            recalculateEpicStatus(newEpicTask);
        }
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
                SubTask epicSubTask = subTasks.get(epicSubTaskId);
                if (epicSubTask != null) {
                    epicSubTask.setEpicId(-1);
                }
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

        boolean epicDone = true;
        boolean epicNew = true;

        for (int subTaskId : subTaskIds) {
            SubTask subTask = subTasks.get(subTaskId);
            switch (subTask.getTaskState()) {
                case NEW:
                    epicDone = false;
                    break;
                case IN_PROGRESS:
                    epicDone = false;
                    epicNew = false;
                    break;
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