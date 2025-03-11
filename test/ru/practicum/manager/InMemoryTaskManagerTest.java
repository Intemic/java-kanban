package ru.practicum.manager;

class InMemoryTaskManagerTest extends InTaskManagerBaseTest {
    @Override
    protected TaskManager getInstaceManager() {
        return new InMemoryTaskManager();
    }

}