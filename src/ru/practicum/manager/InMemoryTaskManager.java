package ru.practicum.manager;

import ru.practicum.exception.NotFoundException;
import ru.practicum.history.HistoryManager;
import ru.practicum.task.Epic;
import ru.practicum.task.SubTask;
import ru.practicum.task.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager history = Managers.getDefaultHistory();
    protected ProxyTaskTreeSet proxyTaskTreeSet = new ProxyTaskTreeSet();

    protected class ProxyTaskTreeSet {
        private TreeSet<Task> sortedTasks = new TreeSet<>(Task::compareTo);

        public void add(Task task) {
            if (task.getStartTime() != null)
                sortedTasks.add(task.clone());
        }

        public void remove(Task task) {
            sortedTasks.remove(task);
        }

        public void modify(Task oldTask, Task newTask) {
            sortedTasks.remove(oldTask);
            sortedTasks.add(newTask);
        }

        public List<Task> getPrioritizedTasks() {
            return sortedTasks.stream().toList();
        }
    }

    @Override
    public void createTask(Task task) {
        if (task == null)
            throw new NullPointerException("Пустая задача");

        if (checkTaskIntervalOverlap(task))
            throw new IllegalArgumentException("Присутствует задача с пересекающимися датами");

        tasks.put(task.getId(), task);
        proxyTaskTreeSet.add(task);
    }

    @Override
    public void createEpic(Epic epic) {

        if (epic == null)
            throw new NullPointerException();

        epics.put(epic.getId(), epic);

        subTasks.putAll(epic.getSubTasks().stream()
                .peek(subTask -> proxyTaskTreeSet.add(subTask))
                .collect(Collectors.toMap(subTask -> subTask.getId(), subTask -> subTask)));
    }

    @Override
    public void createSubTask(SubTask subTask) {
        Epic epic = null;

        if (subTask == null)
            throw new NullPointerException("Пустая подзадача");

        // эпик для этой задачи
        epic = epics.get(subTask.getParentId());
        if (epic == null)
            throw new NotFoundException();

        if (checkTaskIntervalOverlap(subTask))
            throw new IllegalArgumentException("Присутствует задача с пересекающимися датами");

        subTasks.put(subTask.getId(), subTask);
        proxyTaskTreeSet.add(subTask);
    }

    // получаем все values конкретного HasMap
    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    private <T extends Task> ArrayList<T> getElements(Class<? extends Task> type, boolean isClone) {
        return (ArrayList<T>) getValuesForType(type).stream()
                .map(element -> {
                            if (isClone)
                                return element.clone();
                            else
                                return element;
                        }
                ).collect(Collectors.toList());
    }

    // поиск
    @SuppressWarnings("unchecked")
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

        if (result != null)
            history.add(result.clone());

        return result;
    }

    private <T extends Task> void modifyElement(Class<? extends Task> type, T element) {
        T oldElement;
        T cloneElement = null;

        if (element != null) {
            if ((type != Epic.class) && checkTaskIntervalOverlap(element))
                throw new IllegalArgumentException("Присутствует задача с пересекающимися датами");

            oldElement = getElement(type, element.getId(), false);
            if (oldElement != null) {
                if (type != Epic.class)
                    cloneElement = (T) oldElement.clone();
                oldElement.update(element);
                if (type != Epic.class)
                    proxyTaskTreeSet.modify(cloneElement, oldElement);
            }
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
        for (Task task : tasks.values()) {
            history.remove(task.getId());
            proxyTaskTreeSet.remove(task);
        }

        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubTasks();

        // если следовать принципу неизменяемости, здесь наверное нельзя применять Stream API
        for (Epic epic : new ArrayList<>(epics.values())) {
            history.remove(epic.getId());
            epics.remove(epic.getId());
        }
    }

    @Override
    public void deleteAllSubTasks() {
        Collection<SubTask> subTasksList = new ArrayList<>(subTasks.values());

        // если следовать принципу неизменяемости, здесь наверное нельзя применять Stream API
        for (SubTask subTask : subTasksList) {
            history.remove(subTask.getId());

            epics.get(subTask.getParentId()).deleteSubTask(subTask.getId());
            subTasks.remove(subTask.getId());
            proxyTaskTreeSet.remove(subTask);
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return proxyTaskTreeSet.getPrioritizedTasks();
    }

    @Override
    public boolean checkTaskIntervalOverlap(Task task) {
        if (task == null)
            return false;

        return getPrioritizedTasks().stream()
                .anyMatch(compareTask -> compareTask.isTaskIntervalOverlap(task));
    }
}
