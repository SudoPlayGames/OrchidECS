package com.sudoplay.ecs.integration.spi;

import com.sudoplay.ecs.integration.api.Entity;

public abstract class EntityEventBase {

  private Entity entity;
  private boolean cancelled;

  protected EntityEventBase(Entity entity) {

    this.entity = entity;
  }

  public Entity getEntity() {

    return this.entity;
  }

  public void cancel() {

    this.cancelled = true;
  }

  public boolean isCancelled() {

    return this.cancelled;
  }

  @Override
  public String toString() {

    return "EntityEvent{" +
        "entity=" + entity +
        ", cancelled=" + cancelled +
        '}';
  }
}
