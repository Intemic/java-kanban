package ru.practicum.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @DisplayName("Проверка корректности возвращаемых объектов")
    @Test
    public void testNotNullObjects(){
       assertNotNull(Managers.getDefaultHistory());
       assertNotNull(Managers.getDefault());

        assertNotNull(Managers.getDefaultHistory());
        assertNotNull(Managers.getDefault());
    }
}