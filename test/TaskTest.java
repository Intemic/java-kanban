import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.task.Status;
import ru.practicum.task.Task;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;
    private String name = "Тестовая задача";
    private String description = "Что то сделать";


    @DisplayName("Проверяем корректное создание объекта")
    @Test
    public void checkCorrectCreated() {
        task = new Task(name, description);

        assertNotNull(task);
        assertEquals(name, task.getName(), "Не корректное значение наименования");
        assertEquals(description, task.getDescription(), "Не корректное значение описания");
        assertEquals(1, task.getId(), "Не корректное значение Id");
        assertEquals(Status.NEW, task.getStatus(), "Не коректный статус");
    }

    @DisplayName("Проверка корректной операции изменения аттрибутов объекта")
    @Test
    public void checkChangeAttributes() {
        task = new Task(name, description);

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

    @Test
    public void checkCorrectUpdateTask() {

    }
}