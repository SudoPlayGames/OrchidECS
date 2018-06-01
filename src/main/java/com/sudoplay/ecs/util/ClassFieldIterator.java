package com.sudoplay.ecs.util;

import java.lang.reflect.Field;

public class ClassFieldIterator {

  public interface IFieldConsumer {

    void accept(Field field);
  }

  public void doWithFields(Class<?> aClass, IFieldConsumer consumer) {

    do {

      Field[] declaredFields = aClass.getDeclaredFields();

      for (Field field : declaredFields) {
        consumer.accept(field);
      }

    } while ((aClass = aClass.getSuperclass()) != null);

  }

}
