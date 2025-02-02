package ru.practucum.task;

import java.util.ArrayList;
import java.util.Objects;

public class Task {
    private static int uid;
    private int id;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description) {
        this.id = ++uid;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    // наружу не показываем
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

    public void modify(Task task) {
        if (task != null) {
            setName(task.name);
            setDescription(task.description);
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
      if(status != null)
          this.status = status;
    }

    public int getId() {
        return id;
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

    public static void main(String[] args) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            tasks.add(new Task("Задача № " + i, "Что то сделать № " + i));
        }

        tasks.get(3).id = 1;
        if (tasks.get(0).equals(tasks.get(3)))
            System.out.println("Равны");
        System.out.println(tasks.get(0).hashCode());
        System.out.println(tasks.get(3).hashCode());
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
