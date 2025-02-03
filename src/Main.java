import ru.practucum.task.*;

import java.util.ArrayList;

public class Main {
    static TaskManager manager = new TaskManager();
    static Task task;
    static Epic epic;
    static SubTask subTask;
    static int id;

    public static void main(String[] args) {
        System.out.println("До создания");
        printCountStatistic();
        init();

        System.out.println("До всех операций");
        printCountStatistic();
        System.out.println("Все элементы: -----------------------------------------------------");
        printAll();
        System.out.println(" ");

        checkFindElemenets();
        checkChange();
        System.out.println("После изменения --------------------------------------------------------");
        printAll();
        System.out.println(" ");


        System.out.println("После удаления подзадач");
        manager.deleteAllSubTasks();
        printCountStatistic();
        printAll();
        System.out.println(" ");

        System.out.println("После удаления задач");
        manager.deleteAllTasks();
        printCountStatistic();
        printAll();
        System.out.println(" ");

        System.out.println("После удаления эпиков");
        manager.deleteAllEpics();
        printCountStatistic();
        printAll();

    }

    public static void init() {
        for (int i = 0; i < 2; i++) {
            task = new Task("Обычная задача № " + (i + 1), "Выполнить задачу обязательно " + (i + 1));
            manager.create(task);
        }

        epic = new Epic("Эпик № 1", "Будем тестировать эпик 1, шаги: ");

        printCountStatistic();
        System.out.println("");

        for (int i = 0; i < 2; i++) {
            subTask = new SubTask("Подзадача № " + (i + 1),
                    "необходимо выполнить " + (i + 1) + " действие", epic);
        }
        manager.create(epic);

        epic = new Epic("Эпик № 2", "Будем тестировать эпик 2, шаги: ");
        subTask = new SubTask("Подзадача № 1",
                " Что то необходимо выполнить ", epic);
        manager.create(epic);
    }

    public static void printCountStatistic() {
        System.out.println("Статистика --------------------------------------------------");
        System.out.println("Кол-во задач - "
                + (manager.getTasks() != null ? manager.getTasks().size() : 0) + "\n"
                + "Кол-во эпиков - "
                + (manager.getEpics() != null ? manager.getEpics().size() : 0) + "\n"
                + "Кол-во подзадач - "
                + (manager.getSubTasks() != null ? manager.getSubTasks().size() : 0));
        System.out.println(" ");
    }

    public static void printAll() {
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

    public static void checkFindElemenets() {
        System.out.println("Проверяем поиск --------------------------------------------------------");
        System.out.println();

        System.out.println("Задачи :");
        id = 2;
        task = manager.getTask(id);
        printInfoFind(task, id);
        id = 5;
        task = manager.getTask(id);
        printInfoFind(task, id);
        System.out.println(" ");

        System.out.println("Эпики :");
        id = 3;
        epic = manager.getEpic(id);
        printInfoFind(epic, id);
        id = 5;
        epic = manager.getEpic(id);
        printInfoFind(epic, id);
        System.out.println(" ");

        System.out.println("Подзадачи :");
        id = 1;
        subTask = manager.getSubTask(id);
        printInfoFind(subTask, id);
        id = 5;
        subTask = manager.getSubTask(id);
        printInfoFind(subTask, id);
        System.out.println(" ");

        System.out.println("Граничные условия :");
        id = 0;
        task = manager.getTask(id);
        printInfoFind(task, id);
        System.out.println(" ");

        id = 50;
        task = manager.getTask(id);
        printInfoFind(task, id);
        System.out.println(" ");

    }

    public static void checkChange() {
        System.out.println("Проверяем работу изменения ---------------------------------------------------------- ");

        id = 2;
        System.out.println("Задачи:");
        System.out.println(" ");
        System.out.println("Проверим что изменяется только копия");
        task = manager.getTask(id);
        System.out.println("До изменения: " + task);
        task.setName("НОВОЕ ЗНАЧЕНИЕ");
        task = manager.getTask(id);
        System.out.println("После изменения : " + task);
        System.out.println(" ");

        task = manager.getTask(id);
        task.setName(task.getName() + " ИЗМЕНЕНОЕ ЗНАЧЕНИЕ!!!!!!!!!!!!!!!!!");
        task.setStatus(Status.DONE);
        manager.modifyTask(task);
        task = manager.getTask(id);
        System.out.println("После изменения задачи");
        System.out.println(task);
        System.out.println(" ");


        id = 3;
        System.out.println("Эпик:");
        System.out.println(" ");
        System.out.println("Проверим что изменяется только копия");
        epic = manager.getEpic(id);
        System.out.println("До изменения: " + epic);
        epic.setName(epic.getName() + " ИЗМЕНЕНОЕ ЗНАЧЕНИЕ!!!!!!!!!!!!!!!!!");
        epic = manager.getEpic(id);
        System.out.println("После изменения : " + epic);
        System.out.println(" ");

        epic = manager.getEpic(id);
        epic.setName(epic.getName() + " ИЗМЕНЕНОЕ ЗНАЧЕНИЕ!!!!!!!!!!!!!!!!!");
        manager.modifyEpic(epic);
        task = manager.getEpic(id);
        System.out.println("После изменения: ");
        System.out.println(epic);
        System.out.println(" ");

        id = 7;
        System.out.println("Позадача:");
        System.out.println(" ");
        System.out.println("Проверим что изменяется только копия");
        subTask = manager.getSubTask(id);
        System.out.println("До изменения: " + subTask);
        subTask.setName(subTask.getName() + " ИЗМЕНЕНОЕ ЗНАЧЕНИЕ!!!!!!!!!!!!!!!!!");
        subTask = manager.getSubTask(id);
        System.out.println("После изменения : " + subTask);
        System.out.println(" ");

        subTask = manager.getSubTask(id);
        subTask.setName(subTask.getName() + " ИЗМЕНЕНОЕ ЗНАЧЕНИЕ!!!!!!!!!!!!!!!!!");
        subTask.setStatus(Status.IN_PROGRESS);
        manager.modifySubTask(subTask);
        subTask = manager.getSubTask(id);
        System.out.println("После изменения: ");
        System.out.println(subTask);
        System.out.println(" ");

        id = 4;
        subTask = manager.getSubTask(id);
        subTask.setStatus(Status.IN_PROGRESS);
        epic = manager.getEpic(3);
        System.out.println(epic);
        System.out.println(" ");

        id = 4;
        subTask = manager.getSubTask(id);
        subTask.setStatus(Status.IN_PROGRESS);
        manager.modifySubTask(subTask);
        subTask = manager.getSubTask(id);
        System.out.println(" ");



    }
}
