package ru.practicum.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.manager.Managers;
import ru.practicum.task.Epic;
import ru.practicum.task.SubTask;
import ru.practicum.task.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager history;
    private ConfigHistoryManager configLimitIgnore;
    private ConfigHistoryManager configNoLimit;
    private Task task;
    private Epic epic;
    private SubTask subTask;


    @BeforeEach
    public void initial() {
        configLimitIgnore = new ConfigHistoryManager(3, true);
        configNoLimit = new ConfigHistoryManager(false);

        task = new Task("Новая задача", "что то делаем");
        epic = new Epic("Новый эпик", "что то делаем");
        subTask = new SubTask("Новая подзадача", "что то делаем", epic);
    }

    @DisplayName("Проверяем работу ограничения на количество")
    @Test
    public void checkLimitElements() {
        history = new InMemoryHistoryManager(configLimitIgnore);

        history.add(task);
        history.add(epic);
        history.add(subTask);

        Task task2 = new Task("Новая задача2", "что то делаем");
        history.add(task2);
        assertEquals(3, history.getHistory().size(), "Не корректно работает ограничение на кол-во");

        assertEquals(epic, history.getHistory().getFirst(), "Не корректно работает ограничение на кол-во");

        Task task3 = new Task("Новая задача3", "что то делаем");
        history.add(task3);
        assertEquals(3, history.getHistory().size(), "Не корректно работает ограничение на кол-во");

        assertEquals(subTask, history.getHistory().getFirst(), "Не корректно работает ограничение на кол-во");

        history = new InMemoryHistoryManager(configNoLimit);
        for (int i = 0; i < 20; i++) {
            history.add(new Task("Новая задача + " + i, "что то делаем"));
        }

        assertEquals(20, history.getHistory().size(), "Не корректно работает ограничение на кол-во");

    }

    @DisplayName("Проверяем добавление элементов")
    @Test
    public void checkCorrectAddElements() {
        history = new InMemoryHistoryManager(configNoLimit);

        history.add(task);
        assertTrue(history.getHistory().contains(task));

        history.add(null);
        assertEquals(1, history.getHistory().size(), "Ошибка добавления null значения");

        history.add(epic);
        assertEquals(2, history.getHistory().size(), "Ошибка добавления значения");
        assertEquals(epic, history.getHistory().get(1), "Ошибка порядка добавления значения");

        history.add(subTask);
        assertEquals(3, history.getHistory().size(), "Ошибка добавления значения");
        assertEquals(subTask, history.getHistory().get(2), "Ошибка порядка добавления значения");

    }

    @DisplayName("Проверяем на отсутствие дубликатов")
    @Test
    public void checkNotAddedDublicate() {
        history = new InMemoryHistoryManager(configLimitIgnore);

        history.add(task);
        assertEquals(1, history.getHistory().size());

        history.add(task);
        assertEquals(1, history.getHistory().size());

        history.add(task);
        assertEquals(1, history.getHistory().size());

        Task cloneTask = task.clone();
        String description = "Копия первоначальной задачи";
        cloneTask.setDescription(description);

        history.add(cloneTask);
        assertEquals(description, history.getHistory().get(0).getDescription(),
                "Ошибка сохранения актуальных данных");
    }

    @DisplayName("Проверяем корректность работу метода remove")
    @Test
    public void checkCorrectMethodRemove() {
        history = new InMemoryHistoryManager(configNoLimit);

        history.add(task);
        history.add(epic);
        history.add(subTask);

        history.remove(5);
        assertEquals(3, history.getHistory().size(), "Ошибка удаления элемента");

        history.remove(0);
        assertEquals(3, history.getHistory().size(), "Ошибка удаления элемента");

        history.remove(epic.getId());
        assertFalse(history.getHistory().contains(epic.getId()), "Ошибка удаления элемента");
        assertEquals(2, history.getHistory().size(), "Ошибка удаления элемента");

        history.remove(task.getId());
        assertFalse(history.getHistory().contains(task.getId()), "Ошибка удаления элемента");
        assertEquals(1, history.getHistory().size(), "Ошибка удаления элемента");

        history.remove(subTask.getId());
        assertFalse(history.getHistory().contains(subTask.getId()), "Ошибка удаления элемента");
        assertEquals(0, history.getHistory().size(), "Ошибка удаления элемента");

        history.remove(5);

    }

}