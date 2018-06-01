package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.Entity;
import com.sudoplay.ecs.integration.api.Poolable;
import com.sudoplay.ecs.integration.spi.Component;

public class ComponentSystemEvent
    implements Poolable {

  public enum EventType {
    ADD, REMOVE
  }

  private EventType eventType;
  private EntityInternal entityReference;
  private ComponentType componentType;
  private Component component;

  public ComponentSystemEvent init(
      EventType eventType,
      EntityInternal entityReference,
      ComponentType componentType,
      Component component
  ) {

    this.eventType = eventType;
    this.entityReference = entityReference;
    this.componentType = componentType;
    this.component = component;
    return this;
  }

  public EventType getEventType() {

    return this.eventType;
  }

  public ComponentType getComponentType() {

    return this.componentType;
  }

  public Entity getEntity() {

    return this.entityReference;
  }

  /* package */ EntityInternal getEntityReference() {

    return this.entityReference;
  }

  public Component getComponent() {

    return this.component;
  }

  @Override
  public void reset() {

    this.eventType = null;
    this.entityReference = null;
    this.componentType = null;
    this.component = null;
  }

  @Override
  public String toString() {

    return "ComponentEvent{" +
        "type=" + eventType +
        ", entityRef=" + entityReference +
        ", component=" + component +
        '}';
  }

}
