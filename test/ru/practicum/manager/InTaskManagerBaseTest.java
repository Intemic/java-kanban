package ru.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.task.Epic;
import ru.practicum.task.SubTask;
import ru.practicum.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public abstract class InTaskManagerBaseTest {
    protected TaskManager taskManager;
    protected Task task;
    protected Epic epic;
    protected SubTask subTask;

    protected abstract TaskManager getInstaceManager();

    @BeforeEach
    public void initial() {
        taskManager = getInstaceManager();
        task = new Task("Новая задача", "что то делаем");
        epic = new Epic("Новый эпик", "что то делаем");
        subTask = new SubTask("Новая подзадача", "что то делаем", epic);
    }

    @DisplayName("Проверка корректного создания объектов")
    @Test
    public void testCreateAnyElements() {
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTask(task.getId()), "Не работает добавление Task");

        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpic(epic.getId()), "Не работает добавление Epic");

        taskManager.createSubTask(subTask);
        assertEquals(subTask, taskManager.getSubTask(subTask.getId()), "Не работает добавление subTask");
    }

    @DisplayName("Проверка что данные Task не изменяются в менеджере при создания объектов")
    @Test
    public void testNoChangeTaskInManagerOnCreate() {
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTask(task.getId()),
                "Не работает добавление Task");
        assertEquals(task.getName(), (taskManager.getTask(task.getId()).getName()),
                "Не работает добавление Task");
        assertEquals(task.getDescription(), (taskManager.getTask(task.getId()).getDescription()),
                "Не работает добавление Task");
    }

    @DisplayName("Проверка что данные Epic не изменяются в менеджере при создания объектов")
    @Test
    public void testNoChangeEpicInManagerOnCreate() {
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpic(epic.getId()),
                "Не работает добавление Epic");
        assertEquals(epic.getName(), (taskManager.getEpic(epic.getId()).getName()),
                "Не работает добавление Epic");
        assertEquals(epic.getDescription(), (taskManager.getEpic(epic.getId()).getDescription()),
                "Не работает добавление Epic");
        assertEquals(epic.getSubTasks().get(0), (taskManager.getEpic(epic.getId())).getSubTasks().get(0),
                "Не работает добавление Epic");

        assertEquals(subTask, taskManager.getSubTask(subTask.getId()), "Не работает добавление Epic");
    }

    @DisplayName("Проверка что данные SubTask не изменяются в менеджере при создания объектов")
    @Test
    public void testNoChangeSubTaskInManagerOnCreate() {
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        assertEquals(subTask, taskManager.getSubTask(subTask.getId()),
                "Не работает добавление subTask");
        assertEquals(subTask.getName(), (taskManager.getSubTask(subTask.getId()).getName()),
                "Не работает добавление subTask");
        assertEquals(subTask.getDescription(), (taskManager.getSubTask(subTask.getId()).getDescription()),
                "Не работает добавление subTask");
        assertEquals(subTask.getParentId(), (taskManager.getSubTask(subTask.getId())).getParentId(),
                "Не работает добавление subTask");
    }

    @DisplayName("Проверка изменение данных Task")
    @Test
    public void testChangeTask() {
        Task taskCopy = task.clone();
        taskManager.createTask(task);

        taskCopy.setName(task.getName() + " Изменено");
        taskCopy.setDescription(task.getDescription() + " Изменено");
        taskManager.modifyTask(taskCopy);
        assertEquals(taskCopy.getName(), (taskManager.getTask(task.getId()).getName()),
                "Не работает обновление Task");
        assertEquals(taskCopy.getDescription(), (taskManager.getTask(task.getId()).getDescription()),
                "Не работает обновление Task");
    }

    @DisplayName("Проверка изменение данных Epic")
    @Test
    public void testChangeEpic() {
        Epic epicCopy = epic.clone();

        taskManager.createEpic(epic);

        epicCopy.setName(epic.getName() + " Изменено");
        epicCopy.setDescription(epic.getDescription() + " Изменено");
        SubTask subTask2 = new SubTask("Новая подзадача 2", "что то делаем 2", epicCopy);
        taskManager.modifyEpic(epicCopy);
        assertEquals(epic, taskManager.getEpic(epic.getId()),
                "Не работает обновление Epic");
        assertEquals(epic.getName(), (taskManager.getEpic(epic.getId()).getName()),
                "Не работает обновление Epic");
        assertEquals(epic.getDescription(), (taskManager.getEpic(epic.getId()).getDescription()),
                "Не работает обновление Epic");
        assertEquals(epic.getSubTasks().get(0), (taskManager.getEpic(epic.getId())).getSubTasks().get(0),
                "Не работает обновление Epic");
        assertEquals(2, (taskManager.getEpic(epic.getId())).getSubTasks().size(),
                "Не работает обновление Epic");

    }

    @DisplayName("Проверка изменение данных SubTask")
    @Test
    public void testChangeSubTask() {
        SubTask subTaskCopy = subTask.clone();

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        subTaskCopy.setName(subTaskCopy.getName() + " Изменено");
        subTaskCopy.setDescription(subTaskCopy.getDescription() + " Изменено");
        taskManager.modifySubTask(subTaskCopy);
        assertEquals(subTaskCopy, taskManager.getSubTask(subTask.getId()),
                "Не работает добавление subTask");
        assertEquals(subTaskCopy.getName(), (taskManager.getSubTask(subTask.getId()).getName()),
                "Не работает добавление subTask");
        assertEquals(subTaskCopy.getDescription(), (taskManager.getSubTask(subTask.getId()).getDescription()),
                "Не работает добавление subTask");
        assertEquals(subTaskCopy.getParentId(), (taskManager.getSubTask(subTask.getId())).getParentId(),
                "Не работает добавление subTask");

    }

    @DisplayName("Проверка удаления данных из Epic")
    @Test
    public void testDeleteFromEpic() {
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        // Тоже должны удалиться
        assertTrue(taskManager.getSubTasks().isEmpty());
    }

    @DisplayName("Проверка истории изменений")
    @Test
    public void testHistory() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        taskManager.getTask(task.getId());
        assertEquals(1, taskManager.getHistory().size(), "Не корректная работа истории");
        assertEquals(task, taskManager.getHistory().get(0), "Не корректная работа истории");

        taskManager.getEpic(epic.getId());
        assertEquals(task, taskManager.getHistory().get(0), "Не корректная работа истории");
        assertEquals(2, taskManager.getHistory().size(), "Не корректная работа истории");
        assertEquals(epic, taskManager.getHistory().get(1), "Не корректная работа истории");

        taskManager.getSubTask(subTask.getId());
        assertEquals(epic, taskManager.getHistory().get(1), "Не корректная работа истории");
        assertEquals(3, taskManager.getHistory().size(), "Не корректная работа истории");
        assertEquals(subTask, taskManager.getHistory().get(2), "Не корректная работа истории");

        SubTask subTaskChanged = subTask.clone();
        subTaskChanged.setName(subTaskChanged.getName() + " Изменен");
        subTaskChanged.setDescription(subTaskChanged.getDescription() + " Изменен");
        taskManager.modifySubTask(subTaskChanged);

        assertNotEquals(taskManager.getHistory().get(2).getName(), subTaskChanged.getName());
        assertNotEquals(taskManager.getHistory().get(2).getDescription(), subTaskChanged.getDescription());

    }

    @DisplayName("Проверка удаления из истории")
    @Test
    public void testDeleteFromHistory() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubTask(subTask.getId());

        assertEquals(3, taskManager.getHistory().size(), "Ошибки в работе истории");

        taskManager.deleteAllTasks();
        assertEquals(2, taskManager.getHistory().size(), "Ошибки в работе истории");
        assertFalse(taskManager.getHistory().contains(task.getId()),
                "Ошибки в работе истории при удалении задач");

        taskManager.deleteAllSubTasks();
        assertEquals(1, taskManager.getHistory().size(), "Ошибки в работе истории");
        assertFalse(taskManager.getHistory().contains(subTask.getId()),
                "Ошибки в работе истории при удалении подзадач");

        taskManager.createSubTask(subTask);
        taskManager.getSubTask(subTask.getId());

        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getHistory().size(),
                "Ошибки в работе истории, при удалении эпиков");
    }

    @DisplayName("Проверка работы метода getPrioritizedTasks")
    @Test
    public void testMethodGetPrioritizedTasks() {
        LocalDateTime startDateTime = LocalDateTime.now();
        HashMap<LocalDateTime, Long> map = new LinkedHashMap<>();
        int count = 10;
        Random random = new Random();

        assertEquals(Collections.EMPTY_LIST, taskManager.getPrioritizedTasks());

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        do {
            map.put(startDateTime.plusMinutes(random.nextLong(300) + 1),
                    random.nextLong(60) + 1);
        } while (map.size() != count);

        int index = 1;
        for (Map.Entry<LocalDateTime, Long> entry : map.entrySet()) {
            index++;

            if (index <= 5) {
                subTask = new SubTask("Подзадача " + index,
                        "Описание подзадачи " + index, epic,
                        entry.getKey(), Duration.ofMinutes(entry.getValue()));

                taskManager.createSubTask(subTask);
            } else {
                task = new Task("Задача " + index,
                        "Описание задачи " + index, entry.getKey(), Duration.ofMinutes(entry.getValue()));

                taskManager.createTask(task);
            }
        }

        assertEquals(count + 3, taskManager.getPrioritizedTasks().size(),
                "Некорректное кол-во элементов");

        List<Task> allTasks = new ArrayList<>(taskManager.getTasks());
        allTasks.addAll(taskManager.getEpics());
        allTasks.addAll(taskManager.getSubTasks());
        Collections.sort(allTasks);

        List<Task> sorted = taskManager.getPrioritizedTasks();

        assertTrue(allTasks.equals(taskManager.getPrioritizedTasks()),
                "Ошибка формирования сортированного списка");


        task = taskManager.getTasks().get(random.nextInt(taskManager.getTasks().size() - 1) + 1);
        task.setStartTime((LocalDateTime.now()).minusDays(1));
        taskManager.modifyTask(task);

        allTasks.clear();
        allTasks.addAll(taskManager.getTasks());
        allTasks.addAll(taskManager.getEpics());
        allTasks.addAll(taskManager.getSubTasks());
        Collections.sort(allTasks);

        List<Task> sort = taskManager.getPrioritizedTasks();

        assertEquals(allTasks, taskManager.getPrioritizedTasks(),
                "Ошибка формирования сортированного списка");


        taskManager.deleteAllTasks();
        allTasks.clear();
        allTasks.addAll(taskManager.getEpics());
        allTasks.addAll(taskManager.getSubTasks());
        Collections.sort(allTasks);

        assertEquals(allTasks, taskManager.getPrioritizedTasks(),
                "Ошибка формирования сортированного списка");

    }

}
