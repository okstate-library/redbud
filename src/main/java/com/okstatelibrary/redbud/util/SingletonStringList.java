package com.okstatelibrary.redbud.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingletonStringList {
    private static final SingletonStringList instance = new SingletonStringList();
    private final List<String> list;

    // Private constructor to prevent instantiation
    private SingletonStringList() {
        list = new ArrayList<>();
    }

    // Public method to provide access to the singleton instance
    public static SingletonStringList getInstance() {
        return instance;
    }

    // Method to add a value to the list only if it's not already present
    public synchronized boolean addValue(String value) {
        if (!list.contains(value)) {
            return list.add(value);
        }
        return false;
    }

    // Method to get the unmodifiable list
    public List<String> getList() {
        return Collections.unmodifiableList(list);
    }
}
