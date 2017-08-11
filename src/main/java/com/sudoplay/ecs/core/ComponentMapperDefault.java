package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.ComponentMapper;
import com.sudoplay.ecs.integration.api.Entity;
import com.sudoplay.ecs.integration.spi.Component;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Used for quick access to components of a given type from the entities that own them.
 */
public class ComponentMapperDefault<C extends Component> implements
    ComponentMapper<C> {

  private Class<C> componentClass;
  private Map<Long, Component> entityIdComponentMap;

  /* package */ ComponentMapperDefault(
      Class<C> componentClass,
      Map<Long, Component> entityIdComponentMap
  ) {

    this.componentClass = componentClass;
    this.entityIdComponentMap = entityIdComponentMap;
  }

  @Override
  public C get(Entity entity) {

    long id = ((EntityInternal) entity).getId();
    Component component = this.entityIdComponentMap.get(id);
    return this.componentClass.cast(component);
  }

  @Override
  public boolean has(Entity entity) {

    long id = ((EntityInternal) entity).getId();
    return this.entityIdComponentMap.containsKey(id);
  }

  @Override
  public void forEach(
      Consumer<Component> consumer
  ) {

    this.entityIdComponentMap.values()
        .forEach(consumer);
  }

}
