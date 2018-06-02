package com.sudoplay.ecs.integration.spi;

import com.sudoplay.ecs.integration.api.Entity;
import com.sudoplay.ecs.integration.api.Poolable;

public abstract class EntityEventBase
    implements Poolable {

  private Entity entity;
  private boolean cancelled;

  public void setEntity(Entity entity) {

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
  public void reset() {

    this.entity = null;
    this.cancelled = false;
  }

  @Override
  public String toString() {

    return "EntityEvent{" +
        "entity=" + entity +
        ", cancelled=" + cancelled +
        '}';
  }
}
