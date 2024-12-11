package ru.yandex.app.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import ru.yandex.app.exceptions.TaskAddingException;
import ru.yandex.app.exceptions.TaskNotFoundException;
import ru.yandex.app.model.*;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager;

    protected int nextTaskId = 1;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();

    protected final Set<Task> sortedByPriorityTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsFirst(Comparator.naturalOrder())));

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(Task task) throws TaskAddingException {
        isTaskCrossedExisting(tasks, task); //При наличии пересечения - пробрасываем исключение выше

        int taskId = getNextTaskId();
        task.setTaskID(taskId);

        tasks.put(taskId, task);

        if (task.getStartTime() != null) {
            sortedByPriorityTasks.add(task);
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeTask(int taskId) throws TaskNotFoundException {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new TaskNotFoundException("Не найден таск с ID " + taskId);
        }

        removeTask(task);
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
        return Optional.ofNullable(epicTasks.get(epicTaskId))
                .map(EpicTask::getSubTaskIds)
                .orElse(List.of())
                .stream()
                .map(subTasks::get)
                .toList();
    }

    @Override
    public void removeEpicTask(int epicTaskId) throws TaskNotFoundException {
        EpicTask epicTask = epicTasks.get(epicTaskId);
        if (epicTask == null) {
            throw new TaskNotFoundException("Не найден эпик с ID " + epicTaskId);
        }

        removeEpicTask(epicTask);
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
    public void addSubTask(SubTask subTask) throws TaskAddingException {
        isTaskCrossedExisting(tasks, subTask); //При наличии пересечения - пробрасываем исключение выше

        int subTaskId = getNextTaskId();
        subTask.setTaskID(subTaskId);

        subTasks.put(subTaskId, subTask);

        EpicTask subTaskEpic = epicTasks.get(subTask.getEpicId());
        if (subTaskEpic != null) {
            subTaskEpic.addSubTask(subTaskId);
            recalculateEpicStatus(subTaskEpic);
            recalculateEpicEndTimeAndDuration(subTaskEpic);
        }

        if (subTask.getStartTime() != null) {
            sortedByPriorityTasks.add(subTask);
        }
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeSubTask(int subTaskId) throws TaskNotFoundException {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask == null) {
            throw new TaskNotFoundException("Не найден таск с ID " + subTaskId);
        }

        removeSubTask(subTask);
    }

    @Override
    public void clearAllTasks() {
        clearTasks();
        clearEpicTasks();
        clearSubTasks();

        nextTaskId = 1;
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getTaskID());
        }
        tasks.clear();
    }

    @Override
    public void clearEpicTasks() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getTaskID());
        }
        subTasks.clear();

        for (EpicTask epicTask : epicTasks.values()) {
            historyManager.remove(epicTask.getTaskID());
        }
        epicTasks.clear();
    }

    @Override
    public void clearSubTasks() {
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.cleanSubTasks();
        }

        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getTaskID());
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
        if (task == null) {
            throw new TaskNotFoundException("Не найден таск с ID " + taskId);
        }

        historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicTaskById(int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        if (epicTask == null) {
            throw new TaskNotFoundException("Не найден эпик с ID " + epicId);
        }

        historyManager.add(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask == null) {
            throw new TaskNotFoundException("Не найден эпик с ID " + subTaskId);
        }

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

            sortedByPriorityTasks.remove(oldTask);
            sortedByPriorityTasks.add(newTask);
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
        recalculateEpicEndTimeAndDuration(oldEpicTask);
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
                recalculateEpicEndTimeAndDuration(oldSubTaskEpic);

                EpicTask newSubTaskEpic = epicTasks.get(newSubTask.getEpicId());
                newSubTaskEpic.addSubTask(subTaskId);
                recalculateEpicStatus(newSubTaskEpic);
                recalculateEpicEndTimeAndDuration(newSubTaskEpic);
            }

            subTasks.replace(subTaskId, newSubTask);

            sortedByPriorityTasks.remove(oldSubTask);
            sortedByPriorityTasks.add(newSubTask);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return sortedByPriorityTasks;
    }

    private int getNextTaskId() {
        return nextTaskId++;
    }

    private void removeTask(Task task) {
        if (task != null) {
            int taskId = task.getTaskID();

            tasks.remove(taskId);
            historyManager.remove(taskId);

            sortedByPriorityTasks.remove(task);
        }
    }

    private void removeEpicTask(EpicTask epicTask) {
        int epicTaskId = epicTask.getTaskID();

        if (epicTaskId != -1) {

            List<Integer> epicSubTaskIds = epicTask.getSubTaskIds();
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
            recalculateEpicEndTimeAndDuration(subTaskEpicTask);
        }

        subTasks.remove(subTaskId);
        historyManager.remove(subTaskId);

        try {
            sortedByPriorityTasks.remove(subTask);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //removeTaskFromPrioritized(subTask);
    }

    /*private void removeTaskFromPrioritized(Task task) {
        if (sortedByPriorityTasks.contains(task)) {
            sortedByPriorityTasks.remove(task);
        }
    }*/

    protected void recalculateEpicStatus(EpicTask epicTask) {
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

    protected void recalculateEpicEndTimeAndDuration(EpicTask epicTask) {
        List<Integer> subTaskIds = epicTask.getSubTaskIds();

        if (subTaskIds.isEmpty()) {
            epicTask.setEndTime(null);
            return;
        }

        LocalDateTime endTime = null;
        LocalDateTime startTime = null;

        for (int subTaskId : subTaskIds) {
            SubTask subTask = subTasks.get(subTaskId);

            LocalDateTime subTaskEndTime = subTask.getEndTime();
            if (subTaskEndTime != null) {
                if (endTime == null) {
                    endTime = subTaskEndTime;
                } else if (endTime.isBefore(subTaskEndTime)) {
                    endTime = subTaskEndTime;
                }
            }

            LocalDateTime subTaskStartTime = subTask.getStartTime();
            if (subTaskStartTime != null) {
                if (startTime == null) {
                    startTime = subTaskStartTime;
                } else if (startTime.isAfter(subTaskStartTime)) {
                    startTime = subTaskStartTime;
                }
            }
        }

        epicTask.setEndTime(endTime);
        epicTask.setStartTime(startTime);

        if (startTime != null && endTime != null) {
            epicTask.setDuration(Duration.between(startTime, endTime));
        }
    }

    protected void isTaskCrossedExisting(Map<Integer, ? extends Task> tasks, Task task) {
        Optional<Integer> foundCrossingTask = tasks.entrySet().stream()
                .filter(existTask -> checkTasksForCrossing(existTask.getValue(), task))
                .map(Map.Entry::getKey)
                .findFirst();

        if (foundCrossingTask.isPresent()) {
            throw new TaskAddingException("Time already taken");
        }

    }

    protected boolean checkTasksForCrossing(Task taskOne, Task taskTwo) {
        Long oneStartTime;
        try {
            oneStartTime = taskOne.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (NullPointerException e) {
            oneStartTime = null;
        }

        Long oneEndTime;
        try {
            oneEndTime = taskOne.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (NullPointerException e) {
            oneEndTime = null;
        }

        Long twoStartTime;
        try {
            twoStartTime = taskTwo.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (NullPointerException e) {
            twoStartTime = null;
        }

        Long twoEndTime;
        try {
            twoEndTime = taskTwo.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (NullPointerException e) {
            twoEndTime = null;
        }

        return oneStartTime != null && oneEndTime != null && twoStartTime != null && twoEndTime != null
                && Math.min(oneEndTime, twoEndTime) - Math.max(oneStartTime, twoStartTime) > 0;
    }
}