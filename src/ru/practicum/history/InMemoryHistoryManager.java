package ru.practicum.history;

import ru.practicum.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedTaskList history = new LinkedTaskList();
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
            remove(task.getId());

        history.addLast(task);
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return history.toList(); //new LinkedList<>(history);
    }

    private class LinkedTaskList {
        private HashMap<Integer, Node<Task>> linkedList = new HashMap<>();
        private Node<Task> head;
        private Node<Task> tail;

        public void addLast(Task task) {
//            remove(task.getId());

            Node<Task> node = new Node<>(tail, null, task);
            if (tail != null)
                tail.setNext(node);

            tail = node;

            if (head == null)
                head = node;

            linkedList.put(task.getId(), node);
        }

        public void removeFirst() {
            if (head != null)
                remove(head.getData().getId());
        }

        public void remove(int id) {
            if (linkedList.containsKey(id)) {
                Node<Task> current = linkedList.remove(id);

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

        public boolean contains(Task task) {
            return linkedList.containsKey(task.getId());
        }

        private Iterable<Task> iterable() {
            return new Iterable<Task>() {
                Node<Task> current = head;

                @Override
                public Iterator<Task> iterator() {

                    return new Iterator<Task>() {
                        @Override
                        public boolean hasNext() {
                            return current != null;
                        }

                        @Override
                        public Task next() {
                            Task task = current.getData();
                            current = current.getNext();

                            return task;
                        }
                    };
                }
            };
        }

        public List<Task> toList() {
            List<Task> list = new ArrayList<>(size());
            for (Task task : iterable())
                list.add(task);

            return list;
        }

    }
}
