package com.example.inshort_assignment.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TypeConverters {
    @TypeConverter
    public static String fromIntegerList(List<Integer> list) {
        if (list == null) return null;
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Integer> fromStringToIntegerList(String value) {
        if (value == null) return null;
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Integer>>() {}.getType();
        return gson.fromJson(value, listType);
    }
}
