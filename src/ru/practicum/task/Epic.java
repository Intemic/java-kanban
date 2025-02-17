package ru.practicum.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    private Epic(Epic epic) {
        super(epic);
        this.subTasks = new HashMap<>();
        for (Map.Entry<Integer, SubTask> entry : epic.subTasks.entrySet())
            this.subTasks.put(entry.getKey(), entry.getValue().clone());
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    // вручную нельзя изменять
    public void setStatus(Status status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Status getStatus() {
        int countNew = 0;

        if (subTasks.isEmpty()) {
            status = Status.NEW;
            return status;
        }

        status = Status.DONE;

        for (SubTask obj : subTasks.values()) {
            if (obj.getStatus().ordinal() < status.ordinal())
                status = obj.status;
            if (obj.getStatus() == Status.NEW)
                countNew++;
        }

        if (countNew == subTasks.size())
            status = Status.NEW;
        else
            status = Status.IN_PROGRESS;

        return status;
    }

    /*
    недоступно извне, вызывает подзадача
    */
    void addSubTask(SubTask subTask) {
        if (subTask != null) {
            subTasks.put(subTask.getId(), subTask);
        }
    }

    public void deleteSubTask(SubTask subTask) {
        boolean result;

        if (subTask != null) {
            result = subTasks.remove(subTask.getId()) != null;
        }
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteSubTask(int id) {
        subTasks.remove(id);
    }

    public void deleteSubTasks() {
        subTasks.clear();
    }

    @Override
    public void update(Task task) {

//    public void update(Epic epic) {
        if (task != null && task.getClass() == Epic.class && this.getId() == task.getId()) {
            super.update(task);

            Epic epic = (Epic)task;
            subTasks.clear();
            try {
                for (SubTask subTask : epic.getSubTasks())
                    subTasks.put(subTask.getId(), subTask);
            } catch (NullPointerException e) {
            }
        }
    }

    public SubTask getSubTaskForId(int id) {
        return subTasks.get(id);
    }

    @Override
    public Epic clone() {
        return new Epic(this);
    }

    @Override
    public String toString() {
        String result = super.toString();

        int positionStatus = result.indexOf("Task");
        if (positionStatus != -1)
            result = new StringBuffer(result).replace(positionStatus, "Task".length() - 1, "Epic").toString();

        positionStatus = result.indexOf("status");
        if (positionStatus != -1)
            result = new StringBuffer(result).insert(positionStatus,
                    "subTasks=" + (subTasks != null ? subTasks.toString() : "null") + ", ").toString();

        return result;
    }
}
