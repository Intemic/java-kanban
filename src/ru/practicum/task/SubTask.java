package ru.practicum.task;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int parentId;

    private SubTask(int uid, int id, String name, String description, Status status,
                    LocalDateTime startTime, Duration duration, int parentId) {
        super(uid, id, name, description, status, startTime, duration);
        this.parentId = parentId;
    }

    private SubTask(SubTask subTask) {
        super(subTask);
        this.parentId = subTask.parentId;
    }

    public SubTask(String name, String description, Epic epic) {
        this(name, description, epic, null, null);
    }

    public SubTask(String name, String description, Epic epic,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);

        // нет смысла создавать без связки, без связки это обычная задача
        if (epic == null)
            throw new NullPointerException();

        epic.addSubTask(this);
        this.parentId = epic.getId();
    }

    protected SubTask(String name, String description, Status status, LocalDateTime startTime, Duration duration,
                      int parentId) {
        super(name, description, status, startTime, duration);
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }

    @Override
    public void update(Task task) {
        // не будем позволять менять родителя
        if (task != null && task.getClass() == SubTask.class) {
            SubTask subTask = (SubTask) task;
            super.update(subTask);
        }
    }

    @Override
    public SubTask clone() {
        return new SubTask(this);
    }

    @Override
    public String toString() {
        String result = super.toString();
        String parentValues = "null";

        int positionStatus = result.indexOf("Task");
        if (positionStatus != -1)
            result = new StringBuffer(result).insert(0, "Sub").toString();

        positionStatus = result.indexOf("status");
        if (positionStatus != -1) {
            if (parentId != 0)
                parentValues = "Epic"
                        + "{id=" + getParentId() + "}";
            result = new StringBuffer(result).insert(positionStatus, "parent=" + parentValues + ", ").toString();
        }

        return result;
    }

}
