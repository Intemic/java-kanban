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
    private <T extends Task> ArrayList<T> getElements(Class<? extends Task> type) {
        ArrayList<T> result = new ArrayList<>();

        if (!tasks.isEmpty())
            for (Map.Entry<Integer, Task> entry : tasks.entrySet())
                if (entry.getValue().getClass() == type)
                    result.add((T) entry.getValue());

        return !result.isEmpty() ? result : null;
    }

    public ArrayList<Task> getTasks() {
        return getElements(Task.class);
    }

    public ArrayList<Epic> getEpics() {
        return getElements(Epic.class);
    }

    public ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> subTasks = new ArrayList<>();

        try {
            for (Epic epic : getEpics())
                subTasks.addAll(epic.getSubTasks());
        } catch (NullPointerException e) {
            return null;
        }

        return !subTasks.isEmpty() ? subTasks : null;
    }

    public ArrayList<SubTask> getSubTasksForEpic(Epic epic) {
        return epic != null ? epic.getSubTasks() : null;
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
            for (SubTask subTask: getSubTasks())
                subTask.getParent().deleteSubTask(subTask);
        } catch (NullPointerException e) {
            //
        }
    }

    // поиск
    private <T extends  Task> T getElement(int id) {
        T result = null;

        if (!tasks.isEmpty())
            result = (T)tasks.get(id);

        return result;
    }

    public Task getTask(int id) {
        return getElement(id);
    }

    public Epic getEpic(int id) {
        return getElement(id);
    }

    public SubTask getSubTask(int id) {
        SubTask subTask = null;

        if(!tasks.isEmpty())
            try {
                for (Epic epic : getEpics()) {
                    subTask = epic.getSubTaskForId(id);
                    if (subTask != null)
                        return subTask;
                }
            } catch (NullPointerException e) {
                // не будем обрабатывать
            }

        return subTask;
    }

    // обновление
    public <T extends Task> void modifyElement(T element) {
        T oldElement;

        if (element != null) {
            oldElement = getElement(element.getId());
            if (oldElement != null)
                oldElement.modify(element);
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
            oldSubTask = getSubTask(subTask.getId());
            if (oldSubTask != null)
                oldSubTask.modify(subTask);
      }
    }

}
