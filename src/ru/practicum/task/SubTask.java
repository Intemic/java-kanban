package ru.practicum.task;

public class SubTask extends Task {
    private int parentId;

    private SubTask(SubTask subTask) {
        super(subTask);
        this.parentId = subTask.parentId;
    }

    public SubTask(String name, String description, Epic epic) {
        super(name, description);

        // нет смысла создавать без связки, без связки это обычная задача
        if (epic == null)
            throw new NullPointerException();

        epic.addSubTask(this);
        this.parentId = epic.getId();
    }

    public int getParentId() {
        return parentId;
    }

    public void update(SubTask subTask) {
        // не будем позволять менять родителя
        super.update(subTask);
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
