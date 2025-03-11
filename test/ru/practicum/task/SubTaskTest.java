package ru.practicum.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    private Epic epic = new Epic("Новый эпик", "Что то сделаьть");
    private SubTask subTask;

    @BeforeEach
    public void initial() {
    }


    @DisplayName("Проверяем создание объекта")
    @Test
    public void checkCorrectCreate() {
        subTask = null;

        try {
            subTask = new SubTask("Новый SubTask", "Запланировать что то важное", null);
        } catch (NullPointerException e) {
        }
        assertNull(subTask);

        subTask = new SubTask("Новый SubTask", "Запланировать что то важное", epic);
        assertNotNull(subTask);
        assertEquals(subTask.getParentId(), epic.getId(), "Ошибка создания, не корректный id");
    }


    @DisplayName("Проверяем корректное клонирование объекта")
    @Test
    public void checkCorrectCloned() {
        SubTask subTaskClone;

        subTask = new SubTask("Новый SubTask", "Запланировать что то важное", epic);
        subTaskClone = subTask.clone();

        // все остально проверяется в Task
        assertEquals(subTask.getId(), subTaskClone.getId(), "Ошибка клонирования id");
    }
}