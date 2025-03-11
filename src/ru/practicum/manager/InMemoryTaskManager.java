package ru.practicum.manager;

import ru.practicum.exception.NotFoundException;
import ru.practicum.history.HistoryManager;
import ru.practicum.task.Epic;
import ru.practicum.task.SubTask;
import ru.practicum.task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();

    @Override
    public void createTask(Task task) {
        if (task == null)
            throw new NullPointerException();

        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic == null)
            throw new NullPointerException();

        epics.put(epic.getId(), epic);
        // добавляем подзадачи, вдруг еще не присутствуют
        // без цикла не получится getSubTasks() возвращает ArrayList
        for (SubTask subTask : epic.getSubTasks())
            subTasks.put(subTask.getId(), subTask);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        Epic epic = null;

        if (subTask == null)
            throw new NullPointerException();

        // эпик для этой задачи
        epic = epics.get(subTask.getParentId());
        if (epic == null)
            throw new NotFoundException();

        subTasks.put(subTask.getId(), subTask);
    }

    // получаем все values конкретного HasMap
    private <T extends Task> ArrayList<T> getValuesForType(Class<? extends Task> type) {
        ArrayList<T> elements = new ArrayList<>();

        if (type == Task.class) {
            elements = (ArrayList<T>) new ArrayList<>(tasks.values());
        } else if (type == Epic.class) {
            elements = (ArrayList<T>) new ArrayList<>(epics.values());
        } else if (type == SubTask.class) {
            elements = (ArrayList<T>) new ArrayList<>(subTasks.values());
        }

        return elements;
    }

    private <T extends Task> ArrayList<T> getElements(Class<? extends Task> type, boolean isClone) {
        ArrayList<T> elementsCopy = new ArrayList<>();


        for (Task element : getValuesForType(type))
            // возвращаем клонов, кроме изменения
            if (isClone)
                elementsCopy.add((T) element.clone());
            else
                elementsCopy.add((T) element);

        return elementsCopy;
    }

    // поиск
    private <T extends Task> T getElement(Class<? extends Task> type, int id, boolean isClone) {
        T result = null;

        HashMap<Integer, T> elements = null;

        if (type == Task.class) {
            elements = (HashMap<Integer, T>) tasks;
        } else if (type == Epic.class) {
            elements = (HashMap<Integer, T>) epics;
        } else if (type == SubTask.class) {
            elements = (HashMap<Integer, T>) subTasks;
        }

        result = (T) elements.get(id);
        if (isClone && result != null)
            result = (T) result.clone();

        history.add(result.clone());

        return result;
    }

    private <T extends Task> void modifyElement(Class<? extends Task> type, T element) {
        T oldElement;

        if (element != null) {
            oldElement = getElement(type, element.getId(), false);
            if (oldElement != null)
                oldElement.update(element);
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return getElements(Task.class, true);
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return getElements(Epic.class, true);
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return getElements(SubTask.class, true);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values())
            history.remove(task.getId());

        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubTasks();

        for (Epic epic : epics.values()) {
            history.remove(epic.getId());
            epics.remove(epic.getId());
        }

        //epics.clear();
        //subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        Collection<SubTask> subTasksList = new ArrayList<>(subTasks.values());

        for (SubTask subTask : subTasksList) {
            history.remove(subTask.getId());

            epics.get(subTask.getParentId()).deleteSubTask(subTask.getId());
            subTasks.remove(subTask.getId());
        }
    }

    @Override
    public Task getTask(int id) {
        return getElement(Task.class, id, true);
    }

    @Override
    public Epic getEpic(int id) {
        return getElement(Epic.class, id, true);
    }

    @Override
    public SubTask getSubTask(int id) {
        return getElement(SubTask.class, id, true);
    }

    @Override
    public void modifyTask(Task task) {
        modifyElement(Task.class, task);
    }

    @Override
    public void modifyEpic(Epic epic) {
        modifyElement(Epic.class, epic);
    }

    @Override
    public void modifySubTask(SubTask subTask) {
        modifyElement(SubTask.class, subTask);
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }
}
