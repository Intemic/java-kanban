import ru.practucum.task.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Task task;
        Epic epic;
        SubTask subTask;
        int id;

        for (int i = 0; i < 2; i++) {
            task = new Task("Обычная задача № " + (i + 1), "Выполнить задачу обязательно " + (i + 1));
            manager.create(task);
        }

        epic = new Epic("Эпик № 1", "Будем тестировать эпик 1, шаги: ");
        for (int i = 0; i < 2; i++) {
            subTask = new SubTask("Подзадача № " + (i + 1),
                    "необходимо выполнить " + (i + 1) + " действие", epic);
        }
        manager.create(epic);

        epic = new Epic("Эпик № 2", "Будем тестировать эпик 2, шаги: ");
        subTask = new SubTask("Подзадача № 1",
                " Что то необходимо выполнить ", epic);
        manager.create(epic);

        System.out.println("До всех операций");
        printCountStatistic(manager);
        printAll(manager);
        System.out.println(" ");

        // Task
        id = 2;
        task = manager.getTask(id);
        printInfoFind(task, id);
        id = 5;
        task = manager.getTask(id);
        printInfoFind(task, id);
        System.out.println(" ");

        // Epic
        id = 3;
        epic = manager.getEpic(id);
        printInfoFind(epic, id);
        id = 5;
        epic = manager.getEpic(id);
        printInfoFind(epic, id);
        System.out.println(" ");

        // SubTask
        id = 1;
        subTask = manager.getSubTask(id);
        printInfoFind(subTask, id);
        id = 5;
        subTask = manager.getSubTask(id);
        printInfoFind(subTask, id);
        System.out.println(" ");

        id = 0;
        task = manager.getTask(id);
        printInfoFind(task, id);
        System.out.println(" ");

        id = 50;
        task = manager.getTask(id);
        printInfoFind(task, id);
        System.out.println(" ");

        subTask = manager.getSubTask(4);
        subTask.setStatus(Status.DONE);
        System.out.println("После изменения подзадачи");
        printAll(manager);
        System.out.println(" ");

        subTask = manager.getSubTask(7);
        subTask.setStatus(Status.DONE);
        System.out.println("После изменения подзадачи");
        printAll(manager);
        System.out.println(" ");


        System.out.println("После удаления подзадач");
        manager.deleteAllSubTasks();
        printCountStatistic(manager);
        printAll(manager);
        System.out.println(" ");

        System.out.println("После удаления задач");
        manager.deleteAllTasks();
        printCountStatistic(manager);
        printAll(manager);
        System.out.println(" ");

        System.out.println("После удаления эпиков");
        manager.deleteAllEpics();
        printCountStatistic(manager);
        printAll(manager);

    }

    public static void printCountStatistic(TaskManager manager) {
        System.out.println("Кол-во задач - "
                + (manager.getTasks() != null ? manager.getTasks().size() : 0) + "\n"
                + "Кол-во эпиков - "
                + (manager.getEpics() != null ? manager.getEpics().size() : 0) + "\n"
                + "Кол-во подзадач - "
                + (manager.getSubTasks() != null ? manager.getSubTasks().size() : 0));
    }

    public static void printAll(TaskManager manager) {
        ArrayList<Task> tasksList = manager.getTasks();
        if (tasksList != null)
            for (Task element : tasksList)
                System.out.println(element);

        ArrayList<Epic> epicsList = manager.getEpics();
        if (epicsList != null)
            for (Epic element : epicsList)
                System.out.println(element);

        ArrayList<SubTask> subTasksList = manager.getSubTasks();
        if (subTasksList != null)
            for (SubTask element : subTasksList)
                System.out.println(element);
    }

    public static <T> void printInfoFind(T task, int id) {
        String type;

        if (task != null) {
            if (task.getClass() == Task.class)
                type = "Задача";
            else if (task.getClass() == Epic.class)
                type = "Эпик";
            else
                type = "Подзадача";

            System.out.println(type + " " + id + " найдена: " + task);
        } else
            System.out.println("Элемент " + id + " не найден");

    }
}
