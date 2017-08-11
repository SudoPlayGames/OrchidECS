package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.Entity;
import com.sudoplay.ecs.integration.spi.Component;

public class ComponentSystemEvent {

  public enum EventType {
    ADD, REMOVE
  }

  private final EventType eventType;
  private final EntityInternal entityReference;
  private final ComponentType componentType;
  private final Component component;

  public ComponentSystemEvent(
      EventType eventType,
      EntityInternal entityReference,
      ComponentType componentType,
      Component component
  ) {

    this.eventType = eventType;
    this.entityReference = entityReference;
    this.componentType = componentType;
    this.component = component;
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
  public String toString() {

    return "ComponentEvent{" +
        "type=" + eventType +
        ", entityRef=" + entityReference +
        ", component=" + component +
        '}';
  }

}
