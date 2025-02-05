package ru.practicum.manager;

import ru.practicum.task.Epic;
import ru.practicum.task.SubTask;
import ru.practicum.task.Task;

import java.util.ArrayList;

public interface ITaskManager {
    void create(Task task);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTasks();

    //ArrayList<SubTask> getSubTasksForEpic(Epic epic);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void modifyTask(Task task);

    void modifyEpic(Epic epic);

    void modifySubTask(SubTask subTask);
}
