package ru.practucum.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    // здесь будем хранить только Task и Epic
    private final HashMap<Integer, Task> tasks = new HashMap<>();

    // виртуальное создание
    public void create(Task task) {
        // будем добавлять только Тask и Epic
        if (task != null && (task.getClass() == Task.class || task.getClass() == Epic.class))
            tasks.put(task.getId(), task);
    }

    // все для вывода
    private <T extends Task> ArrayList<T> getElements(Class<? extends Task> type, boolean isClone) {
        ArrayList<T> result = new ArrayList<>();

        if (!tasks.isEmpty())
            for (Map.Entry<Integer, Task> entry : tasks.entrySet())
                if (entry.getValue().getClass() == type)
                    // добавляем копии, чтобы не изменили данные, менять можно только через modify
                    if (isClone)
                        result.add((T) entry.getValue().clone());
                    // изменение
                    else
                        result.add((T) entry.getValue());

        return !result.isEmpty() ? result : null;
    }

    public ArrayList<Task> getTasks() {
        return getElements(Task.class, true);
    }

    public ArrayList<Epic> getEpics() {
        return getElements(Epic.class, true);
    }

    public ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> subTasks = new ArrayList<>();

        try {
            for (Epic epic : getEpics())
                // так же добавляем копии
                for (SubTask subTask : epic.getSubTasks())
                    subTasks.add(subTask.clone());
        } catch (NullPointerException e) {
            return null;
        }

        return subTasks;
    }

    public ArrayList<SubTask> getSubTasksForEpic(Epic epic) {
        ArrayList<SubTask> subTasks = new ArrayList<>();

        try {
            for (SubTask subTask : epic.getSubTasks())
                subTasks.add(subTask.clone());
        } catch (NullPointerException e) {
            return null;
        }

        return subTasks;
    }

    // удаление
    public void deleteAllTasks() {
        try {
            for (Task task : getTasks())
                tasks.remove(task.getId());
        } catch (NullPointerException e) {
            //
        }
    }

    public void deleteAllEpics() {
        try {
            for (Epic epic : getEpics()) {
                // вычищаем подзадачи
                epic.deleteSubTasks();
                tasks.remove(epic.getId());
            }
        } catch (NullPointerException e) {
            //
        }
    }

    public void deleteAllSubTasks() {
        try {
            for (SubTask subTask : getSubTasks())
                subTask.getParent().deleteSubTask(subTask);
        } catch (NullPointerException e) {
            //
        }
    }

    // поиск
    private <T extends Task> T getElement(int id, boolean isClone) {
        T result = null;

        if (!tasks.isEmpty()) {
            result = (T) tasks.get(id);
            if (result != null)
                // для обычного поиска возвращаем клон
                if (isClone)
                    result = (T) result.clone();
        }

        return result;
    }

    public Task getTask(int id) {
        return getElement(id, true);
    }

    public Epic getEpic(int id) {
        return getElement(id, true);
    }

    private SubTask getSubTaskInner(int id, boolean isClone) {
        SubTask subTask = null;

        if (!tasks.isEmpty())
            try {
                for (Task epic : getElements(Epic.class, false)) {
                    subTask = ((Epic)epic).getSubTaskForId(id);
                    if (subTask != null)
                        // для обычного поиска возвращаем клон
                        if (isClone)
                            return subTask.clone();
                        else
                            return subTask;
                }
            } catch (NullPointerException e) {
                // не будем обрабатывать
            }

        return subTask;
    }

    public SubTask getSubTask(int id) {
        return getSubTaskInner(id, true);
    }

    // обновление
    public <T extends Task> void modifyElement(T element) {
        T oldElement;

        if (element != null) {
            oldElement = getElement(element.getId(), false);
            if (oldElement != null)
                oldElement.update(element);
        }
    }

    public void modifyTask(Task task) {
        modifyElement(task);
    }

    public void modifyEpic(Epic epic) {
        modifyElement(epic);
    }

    public void modifySubTask(SubTask subTask) {
        SubTask oldSubTask;

        if (subTask != null) {
            oldSubTask = getSubTaskInner(subTask.getId(), false);
            if (oldSubTask != null)
                oldSubTask.update(subTask);
        }
    }

}
