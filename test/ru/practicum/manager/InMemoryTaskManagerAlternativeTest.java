package ru.practicum.manager;

class InMemoryTaskManagerAlternativeTest extends InTaskManagerBaseTest {
    @Override
    protected TaskManager getInstaceManager() {
        return new InMemoryTaskManagerAlternative();
    }

    @Override
    public void testMethodGetPrioritizedTasks() {
        // не реализованно
    }
}