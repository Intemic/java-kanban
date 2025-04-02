package ru.practicum.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Epic extends Task {
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    // для создания из строки
    private Epic(int uid, int id, String name, String description, Status status,
                 LocalDateTime startTime, Duration duration) {
        super(uid, id, name, description, status, startTime, duration);
    }

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
        else if (status != Status.DONE)
            status = Status.IN_PROGRESS;

        return status;
    }


    public void addSubTask(SubTask subTask) {
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

            Epic epic = (Epic) task;
            subTasks.clear();
            try {
                for (SubTask subTask : epic.getSubTasks())
                    subTasks.put(subTask.getId(), subTask);
            } catch (NullPointerException e) {
                System.out.println("Ошибка");
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

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime startTime = null;

        Optional<SubTask> minTask = subTasks.values().stream()
                .filter(subTask -> subTask.getStartTime() != null)
                .min((subTask1, subTask2) ->
                        subTask1.getStartTime().compareTo(subTask2.getStartTime()));

        if (minTask.isPresent())
            startTime = minTask.get().getStartTime();

        // будем заполнять поле для записи в файл
        setStartTime(startTime);

        return super.getStartTime();
    }

    @Override
    public Duration getDuration() {
        Duration duration = null;

        long minutes = subTasks.values().stream()
                .filter(subTask -> subTask.getDuration() != null)
                .mapToLong(subTask -> subTask.getDuration().toMinutes()).sum();

        if (minutes != 0)
            duration = Duration.ofMinutes(minutes);

        setDuration(duration);

        return duration;
    }

    public static void main(String[] args) {
        Epic epic = new Epic("Обычная задача", "Выполнить задачу обязательно");
        String serialized = epic.serialization();
        System.out.println(serialized);

        Epic test = (Epic) deserilization(serialized);
        System.out.println(test);

    }

}
