package ru.practicum.history;

public class Node<T> {
    private Node<T> prev;
    private Node<T> next;
    private final T data;

    public Node(Node<T> prev, Node<T> next, T data) {
        this.prev = prev;
        this.next = next;
        this.data = data;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public Node<T> getNext() {
        return next;
    }

    public T getData() {
        return data;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }
}
