package ru.practicum.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.practicum.exception.ManagerLoadException;
import ru.practicum.task.Epic;
import ru.practicum.task.SubTask;
import ru.practicum.task.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends InTaskManagerBaseTest {
    String prefix = "test";
    String sufix = ".tsk";
    File file = null;

    @Override
    protected TaskManager getInstaceManager() {
        try {
            file = File.createTempFile(prefix, sufix);
            return new FileBackedTaskManager(file);
        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }

        return null;
    }

    @DisplayName("Проверка корректной работы сохранения при создании объектов")
    @Test
    public void checkSaveDataForOperaionCreate() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        FileBackedTaskManager managerLoaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(taskManager.getTasks().equals(managerLoaded.getTasks()), "Ошибка сохранения задач");
        assertTrue(taskManager.getEpics().equals(managerLoaded.getEpics()), "Ошибка сохранения эпиков");
        assertTrue(taskManager.getSubTasks().equals(managerLoaded.getSubTasks()), "Ошибка сохранения подзадач");
    }

    @DisplayName("Проверка корректной работы сохранения при удалении объектов")
    @Test
    public void checkSaveDataForOperaionDelete() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        taskManager.deleteAllSubTasks();
        FileBackedTaskManager managerLoaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(taskManager.getSubTasks().equals(managerLoaded.getSubTasks()), "Ошибка удаления подзадач");

        assertTrue(taskManager.getTasks().equals(managerLoaded.getTasks()), "Ошибка удаления подзадач");
        assertTrue(taskManager.getEpics().equals(managerLoaded.getEpics()), "Ошибка удаления эпиков");

        taskManager.deleteAllEpics();
        managerLoaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(taskManager.getEpics().equals(managerLoaded.getEpics()), "Ошибка удаления эпиков");

        assertTrue(taskManager.getTasks().equals(managerLoaded.getTasks()), "Ошибка удаления эпиков");
        assertTrue(taskManager.getSubTasks().equals(managerLoaded.getSubTasks()), "Ошибка удаления эпиков");

        taskManager.deleteAllTasks();
        managerLoaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(taskManager.getTasks().equals(managerLoaded.getTasks()), "Ошибка удаления задач");

        assertTrue(taskManager.getEpics().equals(managerLoaded.getEpics()), "Ошибка удаления задач");
        assertTrue(taskManager.getSubTasks().equals(managerLoaded.getSubTasks()), "Ошибка удаления задач");
    }

    @DisplayName("Проверка корректной работы сохранения при изменении объектов")
    @Test
    public void checkSaveDataForOperaionModify() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        Task taskClone = task.clone();
        taskClone.setDescription(task.getDescription() + " изменен");
        taskManager.modifyTask(taskClone);

        Epic epicClone = epic.clone();
        epicClone.setDescription(epic.getDescription() + " изменен");
        taskManager.modifyEpic(epicClone);

        SubTask subTaskClone = subTask.clone();
        subTaskClone.setDescription(subTaskClone.getDescription() + " изменен");
        taskManager.modifySubTask(subTaskClone);

        FileBackedTaskManager managerLoaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(taskManager.getTasks().equals(managerLoaded.getTasks()), "Ошибка операции изменения задач");
        assertTrue(taskManager.getEpics().equals(managerLoaded.getEpics()), "Ошибка операции изменения эпиков");
        assertTrue(taskManager.getSubTasks().equals(managerLoaded.getSubTasks()),
                "Ошибка операции изменения подзадач");

        // проверим что UId корректный
        task = new Task("Проверка", "Проверка корректности Id");
        assertEquals(subTask.getId() + 1, task.getId(), "Ошибка востановления UId");
    }

    @DisplayName("Проверка корректной работы при загрузке из файла с пустым переданным именем")
    @Test
    public void checkLoadFromEmptyFile() {
        ManagerLoadException except = assertThrows(ManagerLoadException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        FileBackedTaskManager.loadFromFile("");
                    }
                });
        assertEquals("Ошибка загрузки: файл не найден", except.getMessage(),
                "Ошибка обработки исключения при загрузке");

        except = assertThrows(ManagerLoadException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        FileBackedTaskManager.loadFromFile(new File(""));
                    }
                });
        assertEquals("Ошибка загрузки: файл не найден", except.getMessage(),
                "Ошибка обработки исключения при загрузке");
    }

    @DisplayName("Проверка корректной работы при создание манеджера с пустым файлом")
    @Test
    public void checkCreateWithEmptyFile() {
        IllegalArgumentException except = assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new FileBackedTaskManager("");
                    }
                });
        assertEquals("Некорректное имя файла", except.getMessage(),
                "Ошибка обработки исключения при загрузке");

        except = assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new FileBackedTaskManager(new File(""));
                    }
                });
        assertEquals("Некорректное имя файла", except.getMessage(),
                "Ошибка обработки исключения при загрузке");

    }

}