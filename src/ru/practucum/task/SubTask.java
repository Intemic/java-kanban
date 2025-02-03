package ru.practucum.task;

public class SubTask extends Task {
    private Epic parent;

    private SubTask(SubTask subTask) {
        super(subTask);
        this.parent = subTask.parent;
    }

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.parent = epic;
        if (epic != null)
            epic.addSubTask(this);
    }

    public Epic getParent() {
        return parent;
    }

    public void update(SubTask subTask) {
        // не будем позволять менять родителя
        super.update(subTask);
    }

    public void setStatus(Status status) {
        super.setStatus(status);
        // обновим статус у Эпика
        if (status != null && parent != null)
            parent.updateStatus();
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
            if (parent != null)
                parentValues = parent.getClass().getSimpleName()
                        + "{id=" + parent.getId() + ", name=" + parent.getName() + "}";
            result = new StringBuffer(result).insert(positionStatus, "parent=" + parentValues + ", ").toString();
        }

        return result;
    }
}
