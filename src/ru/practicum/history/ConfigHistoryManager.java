package ru.practicum.history;

public class ConfigHistoryManager {
    private int maxSize;
    private boolean isNotDublicate;

    public ConfigHistoryManager(int maxSize){
        this.maxSize = maxSize;
    }

    public ConfigHistoryManager(int maxSize, boolean isNotDublicate){
        this(maxSize);
        this.isNotDublicate = isNotDublicate;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isNotDublicate() {
        return isNotDublicate;
    }
}
