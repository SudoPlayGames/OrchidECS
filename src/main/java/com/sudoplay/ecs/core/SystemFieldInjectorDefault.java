package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.*;
import com.sudoplay.ecs.integration.spi.Component;
import com.sudoplay.ecs.integration.spi.ComponentMapperStrategy;
import com.sudoplay.ecs.integration.spi.ComponentRegistry;
import com.sudoplay.ecs.util.ClassFieldIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class SystemFieldInjectorDefault implements
    SystemFieldInjector {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      SystemFieldInjectorDefault.class);

  private ComponentRegistry componentRegistry;
  private EntitySetStrategy entitySetStrategy;
  private ComponentMapperStrategy componentMapperStrategy;
  private ClassFieldIterator classFieldIterator;
  private World world;

  /* package */ SystemFieldInjectorDefault(
      ComponentRegistry componentRegistry,
      EntitySetStrategy entitySetStrategy,
      ComponentMapperStrategy componentMapperStrategy,
      ClassFieldIterator classFieldIterator
  ) {

    this.componentRegistry = componentRegistry;
    this.entitySetStrategy = entitySetStrategy;
    this.componentMapperStrategy = componentMapperStrategy;
    this.classFieldIterator = classFieldIterator;
  }

  /* package */ void setWorld(World world) {

    this.world = world;
  }

  @Override
  public void inject(Object objectToInject) {

    this.classFieldIterator.doWithFields(objectToInject.getClass(), field -> {

      if (field.getType() == EntitySet.class) {

        this.injectEntitySets(objectToInject, field);

      } else if (field.getType() == ComponentMapper.class) {

        this.injectComponentMappers(objectToInject, field);

      } else if (field.getType() == World.class) {

        this.injectWorld(objectToInject, field);

      }

    });

  }

  private void injectWorld(Object objectToInject, Field field) {

    InjectWorld annotation = field.getAnnotation(InjectWorld.class);

    if (annotation != null) {

      boolean accessible = field.isAccessible();
      field.setAccessible(true);

      try {
        field.set(
            objectToInject,
            this.world
        );

      } catch (IllegalAccessException e) {
        LOGGER.error(
            "Unable to inject field [{}] in class [{}]",
            field,
            objectToInject.getClass(),
            e
        );
      }

      field.setAccessible(accessible);

    }

  }

  private void injectComponentMappers(Object objectToInject, Field field) {

    InjectComponentMapper annotation = field.getAnnotation(
        InjectComponentMapper.class);

    if (annotation != null) {

      Class<? extends Component> value = annotation.value();

      boolean accessible = field.isAccessible();
      field.setAccessible(true);

      try {
        field.set(
            objectToInject,
            this.componentMapperStrategy.getComponentMapper(value)
        );

      } catch (IllegalAccessException e) {
        LOGGER.error(
            "Unable to inject field [{}] in class [{}]",
            field,
            objectToInject.getClass(),
            e
        );
      }

      field.setAccessible(accessible);

    }
  }

  private void injectEntitySets(Object objectToInject, Field field) {

    InjectEntitySet annotation = field.getAnnotation(InjectEntitySet.class);

    if (annotation != null) {

      Class<? extends Component>[] all = annotation.all();
      Class<? extends Component>[] one = annotation.one();
      Class<? extends Component>[] exclude = annotation.exclude();

      Aspect aspect = new AspectBuilder(this.componentRegistry)
          .requireAll(all)
          .requireOne(one)
          .exclude(exclude)
          .create();

      boolean accessible = field.isAccessible();
      field.setAccessible(true);

      try {
        field.set(
            objectToInject,
            this.entitySetStrategy.getEntitySet(aspect)
        );

      } catch (IllegalAccessException e) {
        LOGGER.error(
            "Unable to inject field [{}] in class [{}]",
            field,
            objectToInject.getClass(),
            e
        );
      }

      field.setAccessible(accessible);

    }
  }

}
