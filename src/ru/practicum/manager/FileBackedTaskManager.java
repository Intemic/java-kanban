package ru.practicum.manager;

import ru.practicum.exception.ManagerLoadException;
import ru.practicum.exception.ManagerSaveException;
import ru.practicum.exception.SerializationException;
import ru.practicum.task.Epic;
import ru.practicum.task.SubTask;
import ru.practicum.task.Task;

import java.io.*;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    private FileBackedTaskManager() {
    }

    public FileBackedTaskManager(final String filename) {
        this(new File(filename));
    }

    public FileBackedTaskManager(File file) {
        if (file == null || file.getName().isBlank())
            throw new IllegalArgumentException("Некорректное имя файла");
        this.file = file;
    }

    public void save() {
        final String pattern = "Ошибка сохранения: %s";

        try (BufferedWriter buffered = new BufferedWriter(
                new FileWriter(file))) {

            List<Task> taskList = new ArrayList<>();
            taskList.addAll(tasks.values());
            taskList.addAll(epics.values());
            taskList.addAll(subTasks.values());

            for (Task task : taskList) {
                buffered.write(task.serialization());
                buffered.newLine();
            }

        } catch (SerializationException e) {
            throw new ManagerSaveException(
                    String.format(pattern, e.getMessage() != null ? e.getMessage() : e.toString()));
        } catch (IOException e) {
            throw new ManagerSaveException(
                    String.format(pattern, e.getMessage() != null ? e.getMessage() : e.toString()));
        }
    }

    public static FileBackedTaskManager loadFromFile(String filename) {
        return loadFromFile(new File(filename));
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final String pattern = "Ошибка загрузки: %s";

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            FileBackedTaskManager manager = new FileBackedTaskManager();
            manager.tasks = (HashMap) objectInputStream.readObject();
            manager.epics = (HashMap) objectInputStream.readObject();
            manager.subTasks = (HashMap) objectInputStream.readObject();
            return manager;

        } catch (FileNotFoundException e) {
            throw new ManagerLoadException(
                    String.format(pattern, "файл не найден"));
        } catch (IOException e) {
            throw new ManagerLoadException(
                    String.format(pattern, e.getMessage() != null ? e.getMessage() : e.toString()));
        } catch (ClassNotFoundException e) {
            throw new ManagerLoadException(
                    String.format(pattern, "не удалось загрузить класс"));
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void modifyTask(Task task) {
        super.modifyTask(task);
        save();
    }

    @Override
    public void modifyEpic(Epic epic) {
        super.modifyEpic(epic);
        save();
    }

    @Override
    public void modifySubTask(SubTask subTask) {
        super.modifySubTask(subTask);
        save();
    }

    public static void main(String[] args) {
        Task task = null;
        Epic epic = null;
        SubTask subTask = null;
        ArrayList<Task> listTask = new ArrayList<>();
        ArrayList<SubTask> listSubTask = new ArrayList<>();
        ArrayList<Epic> listEpic = new ArrayList<>();
        final String filename = "manager.tsk";

        try {
            FileBackedTaskManager manager = new FileBackedTaskManager(filename);

            for (int i = 0; i < 2; i++) {
                task = new Task("Обычная задача № " + (i + 1), "Выполнить задачу обязательно " + (i + 1));
                listTask.add(task);
                manager.createTask(task);
            }

            epic = new Epic("Эпик № 1", "Будем тестировать эпик 1, шаги: ");

            for (int i = 0; i < 2; i++) {
                subTask = new SubTask("Подзадача № " + (i + 1),
                        "необходимо выполнить " + (i + 1) + " действие", epic);
                listSubTask.add(subTask);
            }
            manager.createEpic(epic);
            listEpic.add(epic);

            subTask = new SubTask("Подзадача № 3",
                    " Что то необходимо выполнить ", epic);
            listSubTask.add(subTask);
            manager.createSubTask(subTask);

//            FileBackedTaskManager managerLoaded = loadFromFile(filename);
//            if (!listTask.equals(managerLoaded.getTasks()))
//                System.out.println("Ошибка загрузки задач");
//
//            if (!listSubTask.equals(managerLoaded.getSubTasks()))
//                System.out.println("Ошибка загрузки подзадач");
//
//            if (!listEpic.equals(managerLoaded.getEpics()))
//                System.out.println("Ошибка загрузки эпиков");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
