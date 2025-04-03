package ru.practicum.task;

import ru.practicum.exception.DeserilizationException;
import ru.practicum.exception.SerializationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Task implements Comparable<Task> {
    private static int uid;
    private int id;
    private String name;
    private String description;
    protected Status status;
    private LocalDateTime startTime;
    private Duration duration;

    // для создания из строки
    @SuppressWarnings("static-access")
    protected Task(int uid, int id, String name, String description, Status status,
                   LocalDateTime startTime, Duration duration) {
        this.uid = uid;
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    // нужен для создания объекта без изменения uid
    protected Task(Task task) {
        this(Task.uid, task.id, task.name, task.description, task.status, task.startTime, task.duration);
    }

    public Task(String name, String description) {
        this(name, description, null, null);
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("Отсутствует наименование");

        if (description == null || description.isEmpty())
            throw new NullPointerException("Отсутствует описание");

        // если не указали заполнить текущей
        if (startTime == null)
            this.startTime = LocalDateTime.now();
        else
            this.startTime = startTime;

        this.id = ++uid;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.duration = duration;
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
            setStartTime(task.startTime);
            setDuration(task.duration);
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

    /*
    функциональность сохранения и восстановления из csv можно было конечно реализовать
    намного проще, но данная реализация была сделана с целью применить полученные
    знания на практике
    */

    // получаем перечень полей для сохранения/восстановления
    private static Field[] getFields(Class<?> cls) {
        List<Field> result = new LinkedList<>();

        if (cls.getSuperclass() != null)
            Collections.addAll(result, getFields(cls.getSuperclass()));

        Collections.addAll(result, cls.getDeclaredFields());

        // HashMap уберем
        for (Field field : new LinkedList<>(result)) {
            field.setAccessible(true);
            if (field.getType() == HashMap.class)
                result.remove(field);
        }

        return result.toArray(new Field[]{});
    }

    // конвертируем значения в строку
    private String fieldsValueToString() throws IllegalAccessException {
        StringBuilder result = new StringBuilder();

        for (Field field : getFields(getClass())) {
            if (field.get(this) != null)
                result.append((field.get(this)).toString());
            else
                result.append(" ");
            result.append(",");
        }

        if (!result.isEmpty())
            result.deleteCharAt(result.length() - 1);

        return result.toString();
    }

    private static Object[] getConstructorParam(Field[] fields, String[] values) {
        Object[] objects = new Object[fields.length];

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getType() == int.class) {
                objects[i] = Integer.parseInt(values[i]);
            } else if (fields[i].getType() == Status.class) {
                objects[i] = Status.deserilization(values[i]);
            } else if (fields[i].getType() == LocalDateTime.class) {
                if (!values[i].isBlank())
                    objects[i] = LocalDateTime.parse(values[i]);
            } else if (fields[i].getType() == Duration.class) {
                if (!values[i].isBlank())
                    objects[i] = Duration.parse(values[i]);
            } else {
                objects[i] = values[i];
            }
        }

        return objects;
    }

    public String serialization() {
        StringBuilder result = new StringBuilder();
        result.append(getClass().getName());
        result.append(",");
        try {
            result.append(fieldsValueToString());
        } catch (IllegalAccessException e) {
            throw new SerializationException(e.getMessage());
        }

        return result.toString();
    }

    public static Task deserilization(String data) {
        String className;

        if (data.isBlank())
            throw new DeserilizationException("Некорректный входной параметр");

        String[] values = data.split(",");
        className = values[0];
        values = Arrays.copyOfRange(values, 1, values.length);

        try {
            Class<?> classTask = Class.forName(className);
            if (!(classTask == Task.class) && (classTask.getSuperclass() != Task.class))
                throw new DeserilizationException("Некорректный тип класса");

            Field[] fields = getFields(classTask);
            if (fields.length != values.length)
                throw new DeserilizationException("Некорректный входной параметр");

            Class<?>[] constructorAttributes = new Class[fields.length];
            for (int i = 0; i < fields.length; i++)
                constructorAttributes[i] = fields[i].getType();

            Constructor<?> classConstructor = classTask.getDeclaredConstructor(constructorAttributes);
            classConstructor.setAccessible(true);
            return (Task) classConstructor.newInstance(getConstructorParam(fields, values));
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException | ClassNotFoundException e) {
            throw new DeserilizationException(e.getMessage());
        }
    }

    protected void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    protected void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if (getStartTime() != null && getDuration() != null)
            return getStartTime().plus(getDuration());

        return null;
    }

    @Override
    // естественная сортировка будет по дате начала
    public int compareTo(Task o) {
        int result = -1;

        if (this.getStartTime() == null)
            return result;
        else if (o.getStartTime() == null)
            return 1;
        else
          result = this.getStartTime().compareTo(o.getStartTime());

        // одинаковые дата/время, по id
        if (result == 0)
            result = o.id - this.id;

        return result;
    }
}
