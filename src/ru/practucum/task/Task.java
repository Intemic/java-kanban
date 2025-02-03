package ru.practucum.task;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Objects;

public class Task {
    private static int uid;
    private int id;
    private String name;
    private String description;
    private Status status;

    // нужен для создания объекта без изменения uid
    protected Task(Task task) {
        this.id = task.id;
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
    }

    public Task(String name, String description) {
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
        if (task != null) {
            setName(task.name);
            setDescription(task.description);
            setStatus(task.status);
        }
    }

    public Status getStatus() {
        return status;
    }

    protected void setStatus(Status status) {
      if(status != null)
          this.status = status;
    }

    public int getId() {
        return id;
    }

    // будем возвращать копию
    public Task clone() {
//        Task copyTask = new Task();
//
//        copyTask.id = this.id;
//        copyTask.name = this.name;
//        copyTask.description = this.description;
//        copyTask.status = this.status;
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
