package ru.practicum.history;

public class ConfigHistoryManager {
    private boolean isExistLimit;
    private int maxSize;
    private boolean isNotDublicate;

    public ConfigHistoryManager() {
    }

    public ConfigHistoryManager(int maxSize) {
        this();
        setMaxSize(maxSize);
    }

    private void setMaxSize(int maxSize) {
        if (maxSize > 0) {
            this.maxSize = maxSize;
            isExistLimit = true;
        }
    }

    public ConfigHistoryManager(int maxSize, boolean isNotDublicate) {
        this(maxSize);
        this.isNotDublicate = isNotDublicate;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isNotDublicate() {
        return isNotDublicate;
    }

    public boolean isExistLimit() {
        return isExistLimit;
    }
}
