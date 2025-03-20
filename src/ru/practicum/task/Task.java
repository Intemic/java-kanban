package ru.practicum.task;

import java.io.Serializable;
import java.util.Objects;

public class Task implements Serializable {
    private static int uid;
    private int id;
    private String name;
    private String description;
    protected Status status;

    // нужен для создания объекта без изменения uid
    protected Task(Task task) {
        this.id = task.id;
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
    }

    public Task(String name, String description) {
        if (name == null || name.isEmpty())
            throw new NullPointerException();

        if (description == null || description.isEmpty())
            throw new NullPointerException();

        this.id = ++uid;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty())
            this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null && !description.isEmpty())
            this.description = description;
    }

    public void update(Task task) {
        if (task != null && this.id == task.getId()) {
            setName(task.name);
            setDescription(task.description);
            try {
                setStatus(task.status);
            } catch (UnsupportedOperationException e) {
                // не все можно обновить
            }
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status != null)
            this.status = status;
    }

    public int getId() {
        return id;
    }

    // будем возвращать копию
    public Task clone() {
        return new Task(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + (name != null ? name : "null") + '\'' +
                ", description='" + (description != null ? description : "null") + '\'' +
                ", status=" + status +
                '}';
    }
}
