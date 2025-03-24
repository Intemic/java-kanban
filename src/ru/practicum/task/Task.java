package ru.practicum.task;

import ru.practicum.exception.DeserilizationException;
import ru.practicum.exception.SerializationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Task {
    private static int uid;
    private int id;
    private String name;
    private String description;
    protected Status status;

    // для создания из строки
    protected Task(int uid, int id, String name, String description, Status status) {
        this.uid = uid;
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    // нужен для создания объекта без изменения uid
    protected Task(Task task) {
        this.id = task.id;
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
    }

    public Task(String name, String description) {
        if (name == null || name.isEmpty())
            throw new NullPointerException();

        if (description == null || description.isEmpty())
            throw new NullPointerException();

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
        if (task != null && this.id == task.getId()) {
            setName(task.name);
            setDescription(task.description);
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
    функциональность сохранения и востановления из csv можно было конечно реализовать
    намного проще, но данная реализация была сделана с целью применить полученные
    знания на практике
    */

    // получаем перечень полей для сохранения/востановления
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
            result.append(",");
        }

        if (!result.isEmpty())
            result.deleteCharAt(result.length() - 1);

        return result.toString();
    }

    private static Object[] getConstructorParam(Field[] fields, String[] values) {
        Object[] objects = new Object[fields.length];

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getType() == int.class)
                objects[i] = Integer.parseInt(values[i]);
            else if (fields[i].getType() == Status.class)
                objects[i] = Status.deserilization(values[i]);
            else
                objects[i] = values[i];
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
        String className = null;

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
}
