package ru.practicum.manager;

import ru.practicum.task.Epic;
import ru.practicum.task.SubTask;
import ru.practicum.task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager implements ITaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    @Override
    public void create(Task task) {
        if (task == null)
            return;

        if (task.getClass() == Task.class) {
            tasks.put(task.getId(), task);
        } else if (task.getClass() == Epic.class) {
            epics.put(task.getId(), (Epic)task);
        } else if (task.getClass() == SubTask.class) {
            subTasks.put(task.getId(), (SubTask) task);
        }
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
                elementsCopy.add((T)element.clone());
            else
                elementsCopy.add((T)element);

        return elementsCopy;
    }

    // поиск
    private <T extends Task> T getElement(Class<? extends Task> type, int id, boolean isClone) {
        T result = null;

//        ArrayList<T> elements = getValuesForType( type);
//
//        if (!elements.isEmpty()) {
//            result = (T) elements.get(id);
//            if (result != null)
//                // для обычного поиска возвращаем клон
//                if (isClone)
//                    result = (T) result.clone();
//        }

        result = (T) (getElements(type, true).get(id));
        if (isClone && result != null)
            result = (T) result.clone();

        return result;
    }

    private <T extends Task> void modifyElement(Class<? extends Task> type, T element) {
        T oldElement;

        if (element != null) {
            oldElement = getElement(type, element.getId(),false);
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
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {

        // TODO Необходимо реализовать удаление в связанных эпиках
        subTasks.clear();
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
}
