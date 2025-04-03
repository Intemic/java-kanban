package ru.practicum.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.practicum.exception.DeserilizationException;
import ru.practicum.manager.FileBackedTaskManager;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;
    private String name = "Тестовая задача";
    private String description = "Что то сделать";

    @BeforeEach
    public void initial() {
        task = new Task(name, description);
    }

    @DisplayName("Проверяем корректное создание объекта")
    @Test
    public void checkCorrectCreated() {
        assertNotNull(task);
        assertEquals(name, task.getName(), "Не корректное значение наименования");
        assertEquals(description, task.getDescription(), "Не корректное значение описания");
        assertEquals(Status.NEW, task.getStatus(), "Не коректный статус");

        task = null;
        try {
            task = new Task("", "");
        } catch (NullPointerException e) {
        }
        assertNull(task);
    }

    @DisplayName("Проверка корректной операции изменения аттрибутов объекта")
    @Test
    public void checkChangeAttributes() {
        String nameNew = "Новая задача";
        String decriptionNew = "Изменяем таску";

        task.setName(nameNew);
        assertEquals(nameNew, task.getName(), "Не изменяется наименование");
        task.setName(new String());
        assertEquals(nameNew, task.getName(), "Установлено пустое наименование");

        task.setDescription(decriptionNew);
        assertEquals(decriptionNew, task.getDescription(), "Не изменяется описание");
        task.setDescription(new String());
        assertEquals(decriptionNew, task.getDescription(), "Установлено пустое описание");

        task.setStatus(Status.DONE);
        assertEquals(Status.DONE, task.getStatus(), "Не работает установка статуса");

    }

    @DisplayName("Проверка корректности клонирования объекта")
    @Test
    public void checkCorrectClone() {
        Task taskClone = task.clone();

        assertEquals(task.getId(), taskClone.getId(), "Ошибка клонирования id");
        assertEquals(task.getName(), taskClone.getName(), "Ошибка клонирования name");
        assertEquals(task.getDescription(), taskClone.getDescription(), "Ошибка клонирования description");
    }


    @DisplayName("Проверка обновления объекта")
    @Test
    public void checkCorrectUpdateTask() {
        String nameNew = "Новая задача";
        String decriptionNew = "Изменяем таску";

        Task task2 = new Task(nameNew, decriptionNew);
        task.update(task2);
        assertNotEquals(task.getId(), task2.getId(), "Ошибка обновления id");
        assertNotEquals(task.getName(), task2.getName(), "Ошибка обновления name");
        assertNotEquals(task.getDescription(), task2.getDescription(), "Ошибка обновления description");


        Task taskClone = task.clone();
        taskClone.setName(nameNew);
        taskClone.setDescription(decriptionNew);
        task.update(taskClone);

        assertEquals(task.getId(), taskClone.getId(), "Ошибка обновления id");
        assertEquals(task.getName(), taskClone.getName(), "Ошибка обновления name");
        assertEquals(task.getDescription(), taskClone.getDescription(), "Ошибка обновления description");

    }

    @DisplayName("Проверка метода equals")
    @Test
    public void checkCorrectMethodEquals() {
        Task taskClone = task.clone();
        Task taskOthers = new Task(name, description);

        assertEquals(task, taskClone, "Ошибка метода equals");
        assertNotEquals(task, taskOthers, "Ошибка метода equals");

    }

    @DisplayName("Проверка метода hashCode")
    @Test
    public void checkCorrectMethodHashCode() {
        Task taskClone = task.clone();
        Task taskOthers = new Task(name, description);

        assertEquals(task.hashCode(), taskClone.hashCode(), "Ошибка метода hashCode");
        assertNotEquals(task.hashCode(), taskOthers.hashCode(), "Ошибка метода hashCode");

    }

    @DisplayName("Проверка корректности нумерации")
    @Test
    public void checkCorrectIncrementId() {
        int id = task.getId();

        Task newTask = new Task(name, description);
        assertEquals(id + 1, newTask.getId(), "Ошибка присвоения id");
        newTask = new Task(name, description);
        assertEquals(id + 2, newTask.getId(), "Ошибка присвоения id");

    }

    @DisplayName("Проверка сериализации/десериализации")
    @Test
    public void checkSerializationDeserilizationMethod() {
        Task task = new Task("Обычная задача", "Выполнить задачу обязательно");
        task.setStatus(Status.DONE);
        String serialized = task.serialization();

        Task restoreTask = Task.deserilization(serialized);

        assertEquals(task.getId(), restoreTask.getId(), "Ошибка в востановлении Id");
        assertEquals(task.getName(), restoreTask.getName(), "Ошибка в востановлении name");
        assertEquals(task.getDescription(), restoreTask.getDescription(), "Ошибка в востановлении description");
        assertEquals(task.getStatus(), restoreTask.getStatus(), "Ошибка в востановлении description");

        serialized = serialized.replaceFirst(Task.class.getName(), Epic.class.getName());
        restoreTask = Task.deserilization(serialized);
        assertEquals(Epic.class, restoreTask.getClass());

        DeserilizationException deserExcept = assertThrows(DeserilizationException.class,
                    new Executable() {
                        @Override
                        public void execute() {
                            Task.deserilization(new String());
                        }
                    });

        assertEquals("Некорректный входной параметр", deserExcept.getMessage(),
                "Ошибка обработки исключения при пустом параметре");

        LocalDateTime localDateTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(45);
        task = new Task("Обычная задача", "Выполнить задачу обязательно", localDateTime, duration);
        task.setStatus(Status.DONE);
        serialized = task.serialization();

        restoreTask = Task.deserilization(serialized);
        assertEquals(localDateTime, restoreTask.getStartTime(), "Ошибка восстановления даты начала");
        assertEquals(duration, restoreTask.getDuration(), "Ошибка восстановления длительности");
    }

    @DisplayName("Проверка корректности временных значений")
    @Test
    public void checkCorrectTimeValues() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(60);

        assertNotNull(task.getStartTime(), "Ошибка определения даты начала");
        assertNull(task.getDuration(), "Ошибка определения длительности");
        assertNull(task.getEndTime(), "Ошибка расчета времени окончания");

        task = new Task(task.getName(), task.getDescription(), localDateTime, null);
        assertEquals(localDateTime, task.getStartTime(), "Ошибка определения даты начала");
        assertNull(task.getDuration(), "Ошибка определения длительности");
        assertNull(task.getEndTime(), "Ошибка расчета времени окончания");

        task = new Task(task.getName(), task.getDescription(), localDateTime, duration);
        assertEquals(duration, task.getDuration(), "Ошибка определения длительности");
        assertEquals(localDateTime.plus(duration), task.getEndTime(), "Ошибка расчета времени окончания");
    }
}