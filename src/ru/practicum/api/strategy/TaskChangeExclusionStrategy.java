package ru.practicum.api.strategy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class TaskChangeExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        String value = fieldAttributes.getName();
        return fieldAttributes.getName().equals("uid") ||
                fieldAttributes.getName().equals("id");
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
