package com.sudoplay.ecs.util;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class ClassFieldIterator {

  public void doWithFields(Class<?> aClass, Consumer<Field> consumer) {

    do {

      Field[] declaredFields = aClass.getDeclaredFields();

      for (Field field : declaredFields) {

        consumer.accept(field);

      }

    } while ((aClass = aClass.getSuperclass()) != null);

  }

}
