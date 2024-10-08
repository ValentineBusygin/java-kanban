package ru.yandex.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ru.yandex.app.model.*;
import ru.yandex.app.service.*;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.createTaskManger(TaskManagerTypes.IN_MEMORY_TASK_MANAGER);

        EpicTask epic1 = new EpicTask("EpicTaskOne", "EpicTaskOneDescription");
        inMemoryTaskManager.addEpicTask(epic1);

        SubTask subTask1 = new SubTask("SubTaskOne", "SubTaskOneDescription", epic1.getTaskID());
        subTask1.setTaskState(TaskState.NEW);
        inMemoryTaskManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTaskTwo", "SubTaskTwoDescription", epic1.getTaskID());
        subTask2.setTaskState(TaskState.NEW);
        inMemoryTaskManager.addSubTask(subTask2);

        while (true) {
            printMenu();

            int actionId = scanner.nextInt();
            switch (actionId) {
                case 1:
                    getTasksList(scanner, inMemoryTaskManager);
                    break;
                case 2:
                    getTaskByID(scanner, inMemoryTaskManager);
                    break;
                case 3:
                    createTaskElement(scanner, inMemoryTaskManager);
                    break;
                case 4:
                    updateTaskElement(scanner, inMemoryTaskManager);
                    break;
                case 5:
                    removeTaskByID(scanner, inMemoryTaskManager);
                    break;
                case 6:
                    inMemoryTaskManager.clearAllTasks();
                    break;
                case 0:
                    return;
            }
        }
    }

    private static void printMenu() {
        System.out.println("Выберите действие:");
        System.out.println("1 - Получить список задач");
        System.out.println("2 - Получить задачу по идентификатору");
        System.out.println("3 - Создать задачу");
        System.out.println("4 - Обновить задачу");
        System.out.println("5 - Удалить задачу по идентификатору");
        System.out.println("6 - Удалить все задачи");
        System.out.println("0 - Выход");
    }

    private static void getTasksList(Scanner scanner, InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("Выберите данные для получения:");
        System.out.println("1 - Получить данные по обычным задачам");
        System.out.println("2 - Получить данные по эпикам");
        System.out.println("3 - Получить данные по подзадачам");
        System.out.println("4 - Получить данные по всем задачам независимо от типа");
        System.out.println("0 - Вернуться в меню");
        List<Task> tasks;
        List<EpicTask> epicTasks;
        List<SubTask> subTasks;

        int actionId = scanner.nextInt();
        switch (actionId) {
            case 1:
                tasks = inMemoryTaskManager.getTasks();
                System.out.println(tasks);
                break;
            case 2:
                epicTasks = inMemoryTaskManager.getEpicTasks();
                System.out.println(epicTasks);
                break;
            case 3:
                subTasks = inMemoryTaskManager.getSubTasks();
                System.out.println(subTasks);
                break;
            case 4:
                tasks = inMemoryTaskManager.getTasks();
                System.out.println(tasks);
                epicTasks = inMemoryTaskManager.getEpicTasks();
                System.out.println(epicTasks);
                subTasks = inMemoryTaskManager.getSubTasks();
                System.out.println(subTasks);
                break;
            case 0:
                return;
        }
    }

    public static void getTaskByID(Scanner scanner, InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("Введите идентификатор задачи, информацию по которой хотите получить:");
        int taskId = scanner.nextInt();
        TaskTypes taskType = inMemoryTaskManager.getTaskTypeById(taskId);
        switch (taskType) {
            case TaskTypes.TASK:
                System.out.println(inMemoryTaskManager.getTaskById(taskId));
                break;
            case TaskTypes.EPIC_TASK:
                System.out.println(inMemoryTaskManager.getEpicTaskById(taskId));
                break;
            case TaskTypes.SUB_TASK:
                System.out.println(inMemoryTaskManager.getSubTaskById(taskId));
                break;
            default:
                System.out.println("Задачи с указанным идентификатором не обнаружено");
        }
    }

    private static void createTaskElement(Scanner scanner, InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("Выберите тип задачи для создания:");
        System.out.println("1 - Простая задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Поддтаск");
        System.out.println("0 - Вернуться в меню");

        int actionId = scanner.nextInt();
        switch (actionId) {
            case 1:
                createTask(scanner, inMemoryTaskManager);
                break;
            case 2:
                createEpicTask(scanner, inMemoryTaskManager);
                break;
            case 3:
                createSubTask(scanner, inMemoryTaskManager);
                break;
            case 0:
                return;
        }
    }

    private static void createTask(Scanner scanner, InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("Введите название задачи");
        String taskName = scanner.nextLine();

        System.out.println("Введите описание задачи");
        String taskDescription = scanner.nextLine();

        Task newTask = new Task(taskName, taskDescription);
        inMemoryTaskManager.addTask(newTask);
    }

    private static void createEpicTask(Scanner scanner, InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("Введите название эпика");
        String epicTaskName = scanner.nextLine();

        System.out.println("Введите описание задачи");
        String taskDescription = scanner.nextLine();

        Task newEpicTask = new EpicTask(epicTaskName, taskDescription);
        inMemoryTaskManager.addTask(newEpicTask);
    }

    private static void createSubTask(Scanner scanner, InMemoryTaskManager inMemoryTaskManager) {
        int epicTaskId;
        while (true) {
            System.out.println("Введите идентификатор эпика");
            epicTaskId = scanner.nextInt();
            boolean epicTaskExists = inMemoryTaskManager.isEpicTaskExists(epicTaskId);
            if (epicTaskId != -1 && !epicTaskExists) {
                System.out.println("Эпика с указанным идентификатором не найдено. Введите существующий или '-1' для выхода");
                continue;
            } else if (epicTaskId == -1) {
                return;
            }

            break;
        }

        System.out.println("Введите название задачи");
        String taskName = scanner.nextLine();

        System.out.println("Введите описание задачи");
        String taskDescription = scanner.nextLine();

        SubTask subTask = new SubTask(taskName, taskDescription, epicTaskId);
        inMemoryTaskManager.addSubTask(subTask);
    }

    private static void updateTaskElement(Scanner scanner, InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("Введите идентификатор задачи для обновления");
        int taskId = scanner.nextInt();

        TaskTypes taskType = inMemoryTaskManager.getTaskTypeById(taskId);
        switch (taskType) {
            case TaskTypes.TASK:
                updateTask(scanner, inMemoryTaskManager, taskId);
                break;
            case TaskTypes.EPIC_TASK:
                updateEpicTaskElement(scanner, inMemoryTaskManager, taskId);
                break;
            case TaskTypes.SUB_TASK:
                updateSubTask(scanner, inMemoryTaskManager, taskId);
                break;
            default:
                System.out.println("Задачи с указанным идентификатором не обнаружено");
        }
    }

    private static void updateTask(Scanner scanner, InMemoryTaskManager inMemoryTaskManager, int taskId) {
        Task oldTask = inMemoryTaskManager.getTaskById(taskId);
        Task newTask = new Task(oldTask.getTaskID(), oldTask.getTaskName(), oldTask.getTaskDescription(), oldTask.getTaskState());

        while (true) {
            System.out.println("Выберите параметр для изменения:");
            System.out.println("1 - Название задачи");
            System.out.println("1 - Описание задачи");
            System.out.println("2 - Статус задачи");
            System.out.println("3 - Сохранить изменения и выйти");
            System.out.println("0 - Вернуться в меню без сохранения");

            int actionId = scanner.nextInt();
            switch (actionId) {
                case 1:
                    String newName = getNewTaskName(scanner);
                    newTask.setTaskName(newName);
                    break;
                case 2:
                    String newDescription = getNewTaskDescription(scanner);
                    newTask.setTaskDescription(newDescription);
                    break;
                case 3:
                    TaskState newState = getNewTaskStatus(scanner);
                    newTask.setTaskState(newState);
                    break;
                case 4:
                    inMemoryTaskManager.updateTask(newTask);
                    break;
                case 0:
                    return;
            }
        }
    }

    private static void updateEpicTaskElement(Scanner scanner, InMemoryTaskManager inMemoryTaskManager, int epicTaskId) {
        EpicTask oldEpicTask = inMemoryTaskManager.getEpicTaskById(epicTaskId);
        EpicTask newEpicTask = new EpicTask(oldEpicTask.getTaskID(), oldEpicTask.getTaskDescription(), oldEpicTask.getTaskName());

        while (true) {
            System.out.println("Выберите параметр для изменения:");
            System.out.println("1 - Название задачи");
            System.out.println("2 - Описание задачи");
            System.out.println("3 - Сохранить изменения и выйти");
            System.out.println("0 - Вернуться в меню без сохранения");

            int actionId = scanner.nextInt();
            switch (actionId) {
                case 1:
                    String newName = getNewTaskName(scanner);
                    newEpicTask.setTaskName(newName);
                    break;
                case 2:
                    String newDescription = getNewTaskDescription(scanner);
                    newEpicTask.setTaskDescription(newDescription);
                    break;
                case 3:
                    inMemoryTaskManager.updateEpicTask(newEpicTask);
                    break;
                case 0:
                    return;
            }
        }
    }

    private static void updateSubTask(Scanner scanner, InMemoryTaskManager inMemoryTaskManager, int subTaskId) {

        SubTask oldSubTask = inMemoryTaskManager.getSubTaskById(subTaskId);
        SubTask newSubTask = new SubTask(oldSubTask.getTaskID(), oldSubTask.getTaskName(), oldSubTask.getTaskDescription(), oldSubTask.getTaskState(), oldSubTask.getEpicId());

        while (true) {
            System.out.println("Выберите параметр для изменения:");
            System.out.println("1 - Название задачи");
            System.out.println("1 - Описание задачи");
            System.out.println("2 - Статус задачи");
            System.out.println("3 - Эпик, к которому относится задача");
            System.out.println("4 - Сохранить изменения и выйти");
            System.out.println("0 - Вернуться в меню без сохранения");

            int actionId = scanner.nextInt();
            switch (actionId) {
                case 1:
                    String newName = getNewTaskName(scanner);
                    newSubTask.setTaskName(newName);
                    break;
                case 2:
                    String newDescription = getNewTaskDescription(scanner);
                    newSubTask.setTaskDescription(newDescription);
                    break;
                case 3:
                    TaskState newState = getNewTaskStatus(scanner);
                    newSubTask.setTaskState(newState);
                    break;
                case 4:
                    int epicTaskId = getNewEpic(scanner, inMemoryTaskManager);
                    if (epicTaskId != -1) {
                        newSubTask.setEpicId(epicTaskId);
                    }
                    break;
                case 5:
                    inMemoryTaskManager.updateSubTask(newSubTask);
                    break;
                case 6:
                    return;
            }
        }
    }

    private static String getNewTaskName(Scanner scanner) {
        System.out.println("Введите новое имя:");
        return scanner.nextLine();
    }

    private static String getNewTaskDescription(Scanner scanner) {
        System.out.println("Введите новое описание:");
        return scanner.nextLine();
    }

    private static TaskState getNewTaskStatus(Scanner scanner) {
        System.out.println("Выберите новый статус (по умолчанию - 1):");
        System.out.println("1 - New");
        System.out.println("2 - In progress");
        System.out.println("3 - Done");

        int stateId = scanner.nextInt();

        return switch (stateId) {
            case 2 -> TaskState.IN_PROGRESS;
            case 3 -> TaskState.DONE;
            default -> TaskState.NEW;
        };
    }

    private static int getNewEpic(Scanner scanner, InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("Введите идентификатор эпика, на который вы хотите переназначить задачу:");

        int epicTaskId;
        while (true) {
            epicTaskId = scanner.nextInt();
            if (inMemoryTaskManager.isEpicTaskExists(epicTaskId) || epicTaskId == -1) {
                break;
            } else {
                System.out.println("Эпика с указанным идентификатором не существует, введите существующий (или -1 для выхода):");
            }
        }
        return epicTaskId;
    }

    private static void removeTaskByID(Scanner scanner, InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("Введите идентификатор задачи, которую хотите удалить:");
        int taskId = scanner.nextInt();
        TaskTypes taskType = inMemoryTaskManager.getTaskTypeById(taskId);
        switch (taskType) {
            case TaskTypes.TASK:
                inMemoryTaskManager.removeTask(taskId);
                break;
            case TaskTypes.EPIC_TASK:
                inMemoryTaskManager.removeEpicTask(taskId);
                break;
            case TaskTypes.SUB_TASK:
                inMemoryTaskManager.removeSubTask(taskId);
                break;
            default:
                System.out.println("Задачи с указанным идентификатором не обнаружено");
        }
    }
}