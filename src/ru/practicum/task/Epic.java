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
        updateStatus();
    }

    @Override
    // вручную нельзя изменять
    public void setStatus(Status status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Status getStatus() {
        updateStatus();
        return status;
    }

    /*
    недоступно извне, вызывает подзадача
    */
    void addSubTask(SubTask subTask) {
        if (subTask != null) {
            subTasks.put(subTask.getId(), subTask);
            // обновим статус
            updateStatus();
        }
    }

    public void deleteSubTask(SubTask subTask) {
        boolean result;

        if (subTask != null) {
            result = subTasks.remove(subTask.getId()) != null;
            // обновим статус
            if (result)
                updateStatus();
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
        // обновим статус
        updateStatus();
    }

    public void update(Epic epic) {
        if (epic != null && this.getId() == epic.getId()) {
            super.update(epic);
            subTasks.clear();
            try {
                for (SubTask subTask : epic.getSubTasks())
                    subTasks.put(subTask.getId(), subTask);
            } catch (NullPointerException e) {
            }

            // обновим статус
            updateStatus();
        }
    }

    public SubTask getSubTaskForId(int id) {
        return subTasks.get(id);
    }

    void updateStatus() {
        if (subTasks.isEmpty()) {
            status = Status.NEW;
            return;
        }

        HashMap<Status, Integer> countStatus = new HashMap<>();
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            Status status = entry.getValue().getStatus();
            countStatus.put(status, countStatus.get(status) != null ? countStatus.get(status) + 1 : 1);
        }

        for (Map.Entry<Status, Integer> entry : countStatus.entrySet())
            if (entry.getValue() == subTasks.size()) {
                status = entry.getKey();
                return;
            }

        status = Status.IN_PROGRESS;
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
