package ru.practicum.history;

import ru.practicum.task.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedList<Task> history = new LinkedList<>();
    private ConfigHistoryManager config;

    public InMemoryHistoryManager(ConfigHistoryManager config) {
        this.config = config;
    }

    @Override
    public void add(Task task) {
        if (task == null)
            return;

        if (config.isExistLimit() && history.size() == config.getMaxSize()) {
            history.removeFirst();
        }

        if (config.isNotDublicate() && history.contains(task))
            return;

        history.addLast(task);
    }

    @Override
    public void remove(int id) {

    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }

    private class LinkedTaskList {
        private HashMap<Integer, Node<Task>> linkedList = new HashMap<>();
        private Node<Task> head;
        private Node<Task> tail;

        public void addLast(Task task) {
            Node<Task> node = new Node<>(tail, null, task);
            tail = node;
            if (head == null)
                head = node;

            linkedList.put(task.getId(), node);
        }

        public void remove(Task task) {
            if (linkedList.containsKey(task.getId())) {
                Node<Task> current = linkedList.remove(task.getId());

                if (current.equals(head)) {
                    head = current.getNext();
                    if (head != null)
                        head.setPrev(null);
                }

                if (current.equals(tail)) {
                    tail = current.getPrev();
                    if (tail != null)
                        tail.setNext(null);
                }

                if (current.getPrev() != null)
                    current.getPrev().setNext(current.getNext());

                if (current.getNext() != null)
                    current.getNext().setPrev(current.getPrev());
                }
        }

        public int size() {
            return linkedList.size();
        }
    }
}
