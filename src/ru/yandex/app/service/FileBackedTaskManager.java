package ru.yandex.app.service;

import ru.yandex.app.exceptions.ManagerSaveException;
import ru.yandex.app.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private File dbFile = null;

    public FileBackedTaskManager() {
        dbFile = new File("./tmpFile.csv");
        load();
    }

    public FileBackedTaskManager(String dbFileName) {
        dbFile = new File(dbFileName);
        load();
    }

    public FileBackedTaskManager(File dbFile) {
        this.dbFile = dbFile;
        load();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void removeTask(int taskID) {
        super.removeTask(taskID);
        save();
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        super.addEpicTask(epicTask);
        save();
    }

    @Override
    public void removeEpicTask(int epicTaskId) {
        super.removeEpicTask(epicTaskId);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void removeSubTask(int subTaskID) {
        super.removeSubTask(subTaskID);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpicTasks() {
        super.clearEpicTasks();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask newEpicTask) {
        super.updateEpicTask(newEpicTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask newSubTask) {
        super.updateSubTask(newSubTask);
        save();
    }

    private void save() throws ManagerSaveException {
        try (FileWriter writer = new FileWriter(dbFile, StandardCharsets.UTF_8, false)) {
            writer.write("type,id,name,description,status,epic\n");
            for (Task task : getTasks()) {
                writer.write(task.taskToString() + "\n");
            }
            for (Task task : getEpicTasks()) {
                writer.write(task.taskToString() + "\n");
            }
            for (Task task : getSubTasks()) {
                writer.write(task.taskToString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void load() {
        if (dbFile.exists()) {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(dbFile, StandardCharsets.UTF_8))) {
                while (fileReader.ready()) {
                    String line = fileReader.readLine();
                    taskFromString(line);
                }
            } catch (IOException e) {
                System.out.println("Произошла ошибка во время чтения файла");
            }
        } else {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Произошла ошибка во время создания пустого файла");
            }
        }
    }

    private void taskFromString(String value) {
        String[] taskElems = value.split(",");
        try {
            TaskTypes taskType = TaskTypes.valueOf(taskElems[0]);
            int taskID = Integer.parseInt(taskElems[1]);
            String taskName = taskElems[2];
            String taskDescription = taskElems[3];
            TaskState taskState = Set.of(TaskTypes.TASK, TaskTypes.SUB_TASK).contains(taskType) ? TaskState.valueOf(taskElems[4]) : null;
            int taskEpicId = (taskType == TaskTypes.EPIC_TASK) ? Integer.parseInt(taskElems[5]) : null;
            switch (taskType) {
                case TASK ->
                        tasks.put(taskID, new Task(taskID, taskName, taskDescription, taskState));
                case SUB_TASK -> {
                    subTasks.put(taskID, new SubTask(taskID, taskName, taskDescription, taskState, taskEpicId));

                    EpicTask subTaskEpic = epicTasks.get(taskEpicId);
                    if (subTaskEpic != null) {
                        subTaskEpic.addSubTask(taskID);
                        recalculateEpicStatus(subTaskEpic);
                    }
                }
                case EPIC_TASK -> epicTasks.put(taskID, new EpicTask(taskID, taskName, taskDescription));
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
