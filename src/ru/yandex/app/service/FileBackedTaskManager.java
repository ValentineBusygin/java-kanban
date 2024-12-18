package ru.yandex.app.service;

import ru.yandex.app.exceptions.ManagerLoadException;
import ru.yandex.app.exceptions.ManagerSaveException;
import ru.yandex.app.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File dbFile = null;

    private static final String DB_FILE_HEADER = "type,id,name,description,status,startdate,duration,epic\n";

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
            writer.write(DB_FILE_HEADER);
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
                throw new ManagerLoadException("Произошла ошибка во время создания пустого файла");
            }
        }
    }

    private void taskFromString(String value) {
        String[] taskElems = value.split(",");
        try {
            TaskTypes taskType = TaskTypes.valueOf(taskElems[0]);
            int taskId = Integer.parseInt(taskElems[1]);
            String taskName = taskElems[2];
            String taskDescription = taskElems[3];
            TaskState taskState = TaskState.valueOf(taskElems[4]);
            LocalDateTime taskStartTime = taskElems[5].isBlank() ? null : LocalDateTime.parse(taskElems[5], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            Duration taskDuration = taskElems[6].isBlank() ? null : Duration.ofMinutes(Integer.parseInt(taskElems[6]));
            int taskEpicId = (taskType == TaskTypes.SUB_TASK) ? Integer.parseInt(taskElems[7]) : 0;
            switch (taskType) {
                case TASK ->
                        tasks.put(taskId, new Task(taskId, taskName, taskDescription, taskState, taskStartTime, taskDuration));
                case SUB_TASK -> {
                    subTasks.put(taskId, new SubTask(taskId, taskName, taskDescription, taskState, taskStartTime, taskDuration, taskEpicId));

                    EpicTask subTaskEpic = epicTasks.get(taskEpicId);
                    if (subTaskEpic != null) {
                        subTaskEpic.addSubTask(taskId);
                        recalculateEpicStatus(subTaskEpic);
                        recalculateEpicEndTimeAndDuration(subTaskEpic);
                    }
                }
                case EPIC_TASK -> epicTasks.put(taskId, new EpicTask(taskId, taskName, taskDescription));
            }
            if (nextTaskId < taskId) {
                //+1 потому что у нас уже есть таск с указанным ID, а значит возвращать этот же ID некорректно
                nextTaskId = taskId + 1;
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
