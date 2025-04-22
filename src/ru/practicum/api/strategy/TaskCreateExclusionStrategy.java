package ru.practicum.api.strategy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class TaskCreateExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        System.out.println(fieldAttributes.getName());
        return //fieldAttributes.getName().equals("uid") ||
                fieldAttributes.getName().equals("id");
                //|| fieldAttributes.getName().equals("status");
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
