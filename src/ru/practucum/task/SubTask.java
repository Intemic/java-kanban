package ru.practucum.task;

public class SubTask extends Task {
    private final Epic parent;

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.parent = epic;
        if (epic != null)
            epic.addSubTask(this);
    }

    public Epic getParent() {
        return parent;
    }

    public void modify(SubTask subTask) {
        // не будем позволять менять родителя
        super.modify(subTask);
    }

    public void setStatus(Status status) {
        super.setStatus(status);
        // обновим статус у Эпика
        if(status != null && parent != null)
            parent.updateStatus();
    }

    @Override
    public String toString() {
        String result = super.toString();
        String parentValues = "null";

        int positionStatus = result.indexOf("Task");
        if (positionStatus != -1)
            result = new StringBuffer(result).insert(0, "Sub").toString();

        positionStatus = result.indexOf("status");
        if (positionStatus != -1){
            if (parent != null)
                parentValues = parent.getClass().getSimpleName()
                        + "{name=" + parent.getName() + ", id=" + parent.getId() + "}";
            result = new StringBuffer(result).insert(positionStatus, "parent=" + parentValues + ", ").toString();
        }

        return result;
    }
}
