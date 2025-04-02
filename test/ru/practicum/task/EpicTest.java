package ru.practicum.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    public void initial() {
        epic = new Epic("Новый эпис", "Что то сделать");
//        subTask = new SubTask("Новая подзадача", "Делаем что то важное", epic);
    }

    @DisplayName("Проверяем корректное создание объекта")
    @Test
    public void checkCorrectAddedSubTask() {
        assertTrue(epic.getSubTasks().isEmpty());

        SubTask subTask = new SubTask("Новая подзадача", "Делаем что то важное", epic);
        assertEquals(1, epic.getSubTasks().size(), "Не корректное добавление SubTasks");
        assertEquals(subTask, epic.getSubTasks().get(0), "Не корректное добавление SubTasks");

        subTask = new SubTask("Новая подзадача", "Делаем что то важное", epic);
        assertEquals(2, epic.getSubTasks().size(), "Не корректное добавление SubTasks");
    }

    @DisplayName("Проверяем невозможность изменить вручную статус объекта")
    @Test
    public void checkNotPossibleChangeStatusInManual() {
        try {
            epic.setStatus(Status.DONE);
        } catch (UnsupportedOperationException e) {
        }

        assertEquals(Status.NEW, epic.getStatus());
    }

    @DisplayName("Проверяем корректность статуса")
    @Test
    public void checkCorrectSetStatus() {
        subTask = new SubTask("Новая подзадача", "Делаем что то важное", epic);
        SubTask subTask2 = new SubTask("Новая подзадача", "Делаем что то важное", epic);

        assertEquals(Status.NEW, epic.getStatus(),
                "Не корректный статус, должен быть NEW, текущий - " + epic.getStatus());
        subTask2 = new SubTask("Новая подзадача", "Делаем что то важное", epic);
        assertEquals(Status.NEW, epic.getStatus());

        subTask.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(),
                "Не корректный статус, должен быть IN_PROGRESS, текущий - " + epic.getStatus());

        subTask2.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(),
                "Не корректный статус, должен быть IN_PROGRESS, текущий - " + epic.getStatus());

        subTask.setStatus(Status.DONE);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(),
                "Не корректный статус, должен быть IN_PROGRESS, текущий - " + epic.getStatus());

        subTask2.setStatus(Status.DONE);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(),
                "Не корректный статус, должен быть IN_PROGRESS, текущий - " + epic.getStatus());

        subTask.setStatus(Status.NEW);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(),
                "Не корректный статус, должен быть IN_PROGRESS, текущий - " + epic.getStatus());

        epic.deleteSubTasks();
        assertEquals(Status.NEW, epic.getStatus(),
                "Не корректный статус, должен быть NEW, текущий - " + epic.getStatus());

    }

    @DisplayName("Проверяем корректность удаления")
    @Test
    public void checkOperationDelete() {
        subTask = new SubTask("Новая подзадача", "Делаем что то важное", epic);
        SubTask subTask2 = new SubTask("Новая подзадача", "Делаем что то важное", epic);

        epic.deleteSubTask(subTask);
        assertFalse(epic.getSubTasks().contains(subTask));

        epic.deleteSubTasks();
        assertTrue(epic.getSubTasks().isEmpty());
    }

    @DisplayName("Проверяем корректность получения объекта по id")
    @Test
    public void checkCorrectGetSubTaskById() {
        subTask = new SubTask("Новая подзадача", "Делаем что то важное", epic);
        SubTask subTask2 = new SubTask("Новая подзадача", "Делаем что то важное", epic);

        assertEquals(subTask2, epic.getSubTaskForId(subTask2.getId()), "Не корректная работа getSubTaskForId");
    }

    @DisplayName("Проверяем корректность clone")
    @Test
    public void checkCorrectClone() {
        subTask = new SubTask("Новая подзадача", "Делаем что то важное", epic);
        SubTask subTask2 = new SubTask("Новая подзадача", "Делаем что то важное", epic);
        Epic epicClone = epic.clone();

        assertEquals(epic.getId(), epicClone.getId(), "Не корректное клонирование Id");
        assertTrue(epic.getSubTasks().equals(epicClone.getSubTasks()));

    }

    @DisplayName("Проверяем корректность обновления")
    @Test
    public void checkCorrectUpdate() {
        SubTask subTask2 = new SubTask("Новая подзадача", "Делаем что то важное", epic);
        Epic epicClone = epic.clone();

        epicClone.deleteSubTask(subTask);
        epic.update(epicClone);

        assertEquals(epicClone.getSubTasks().size(), epic.getSubTasks().size(),
                "Не корректная работа обновления");

        assertNotNull(epic.getSubTaskForId(subTask2.getId()));
        assertEquals(subTask2, epic.getSubTaskForId(subTask2.getId()), "Не корректная работа обновления");
    }

    @DisplayName("Проверка корректности временных значений")
    @Test
    public void checkCorrectTimeValues() {
        LocalDateTime startDateTime = LocalDateTime.now();
        HashMap<LocalDateTime, Long> map = new LinkedHashMap<>();
        int count = 4;
        Random random = new Random();
        Duration duration = null;

        do {
            map.put(startDateTime.plusMinutes(random.nextLong(300) + 1),
                    random.nextLong(60) + 1);
        } while (map.size() != count);

        int index = 1;
        // без даты
        subTask = new SubTask("Подзадача " + index, "Описание подзадачи " + index, epic);
        assertNull(epic.getStartTime(), "Ошибка определения даты начала");
        assertNull(epic.getDuration(), "Ошибка определения длительности");
        assertNull(epic.getEndTime(), "Ошибка расчета времени окончания");


        for (Map.Entry<LocalDateTime, Long> entry : map.entrySet()) {
            index++;
            subTask = new SubTask("Подзадача " + index,
                    "Описание подзадачи " + index, epic, entry.getKey(), Duration.ofMinutes(entry.getValue()));
            if (duration == null)
                duration = Duration.ofMinutes(entry.getValue());
            else
                duration = duration.plusMinutes(entry.getValue());
        }

        List<LocalDateTime> listDateTime = new ArrayList<>(map.keySet());
        Collections.sort(listDateTime);

        assertTrue(listDateTime.get(0).equals(epic.getStartTime()), "Ошибка определения даты начала");
        assertTrue(duration.equals(epic.getDuration()), "Ошибка определения длительности");
        LocalDateTime finishDateTime = epic.getStartTime().plusMinutes(epic.getDuration().toMinutes());
        assertTrue(finishDateTime.equals(epic.getEndTime()), "Ошибка расчета времени окончания");
    }
}