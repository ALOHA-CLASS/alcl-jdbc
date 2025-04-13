package com.alohaclass.jdbc.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.alohaclass.jdbc.annotation.Pk;
import com.alohaclass.jdbc.annotation.Table;
import com.alohaclass.jdbc.dto.Entity;

public class EntityProcessor {
    public static void processEntity(Entity entity) {
        Class<?> clazz = entity.getClass();

        if (clazz.isAnnotationPresent((Class<? extends Annotation>) Table.class)) {
            if (clazz.isAnnotationPresent(Table.class)) {
                Table table = clazz.getAnnotation(Table.class);
                entity.tableName = table.value();
            }

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Pk.class)) {
                    entity.pk = field.getName();
                    break;
                }
            }
        }
    }
}
