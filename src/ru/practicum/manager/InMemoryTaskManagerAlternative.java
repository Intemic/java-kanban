package ru.practicum.manager;

import ru.practicum.history.HistoryManager;
import ru.practicum.task.Epic;
import ru.practicum.task.SubTask;
import ru.practicum.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManagerAlternative implements TaskManager {
    // здесь будем хранить только Task и Epic
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();

    private void create(Task task) {
        // будем добавлять только Тask и Epic
        if (task != null && (task.getClass() == Task.class || task.getClass() == Epic.class))
            tasks.put(task.getId(), task);
    }

    // виртуальное создание
    @Override
    public void createTask(Task task) {
        create(task);
    }

    @Override
    public void createEpic(Epic epic) {
        create(epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        // здесь ничего не делаем так как храним только Тask и Epic
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

        return result;
    }

    public ArrayList<Task> getTasks() {
        return getElements(Task.class, true);
    }

    public ArrayList<Epic> getEpics() {
        return getElements(Epic.class, true);
    }

    public ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> subTasks = new ArrayList<>();

        for (Epic epic : getEpics())
            // так же добавляем копии
            for (SubTask subTask : epic.getSubTasks())
                subTasks.add(subTask.clone());

        return subTasks;
    }

    // удаление
    public void deleteAllTasks() {
        for (Task task : getTasks()) {
            history.remove(task.getId());
            tasks.remove(task.getId());
        }
    }

    public void deleteAllEpics() {
        for (Epic epic : getEpics()) {
            // вычищаем подзадачи
            deleteAllSubTaskForEpic(epic);

            history.remove(epic.getId());
            tasks.remove(epic.getId());
        }
    }

    public void deleteAllSubTasks() {
        for (Epic epic : getEpics())
            deleteAllSubTaskForEpic(epic);
    }

    private void deleteAllSubTaskForEpic(Epic epic) {
        for (SubTask subTask : epic.getSubTasks())
            history.remove(subTask.getId());
        epic.deleteSubTasks();
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

        history.add(result.clone());

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
            for (Task epic : getElements(Epic.class, false)) {
                subTask = ((Epic) epic).getSubTaskForId(id);
                if (subTask != null)
                    // для обычного поиска возвращаем клон
                    history.add(subTask.clone());

                if (isClone)
                    return subTask.clone();
                else
                    return subTask;
            }

        return subTask;
    }

    public SubTask getSubTask(int id) {
        return getSubTaskInner(id, true);
    }

    // обновление
    private <T extends Task> void modifyElement(T element) {
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

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        throw new UnsupportedOperationException();
    }
}
