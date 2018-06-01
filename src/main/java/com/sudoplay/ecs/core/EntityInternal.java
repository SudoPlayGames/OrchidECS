package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.Entity;
import com.sudoplay.ecs.integration.api.Poolable;
import com.sudoplay.ecs.integration.spi.Component;

public class EntityInternal
    implements Entity,
    Poolable {

  private long id;
  private World world;

  /* package */ void init(
      long id
  ) {

    this.id = id;
  }

  @Override
  public void reset() {

    this.id = Long.MIN_VALUE;
    this.world = null;
  }

  /**
   * Called by the world post entity creation.
   *
   * @param world the world
   */
  /* package */ void setWorld(World world) {

    this.world = world;
  }

  /* package */ long getId() {

    return this.id;
  }

  @Override
  public void worldAdd() {

    if (this.entityExists()) {
      this.world.entityAdd(this);
    }
  }

  @Override
  public void worldRemove() {

    this.world.entityRemove(this);
    this.world = null;
  }

  @Override
  public boolean entityExists() {

    return this.world != null;
  }

  @Override
  public <C extends Component> void componentAdd(C component) {

    if (this.entityExists()) {
      this.world.componentAdd(this, component);
      this.world.entityChange(this);
    }
  }

  @Override
  public <C extends Component> void componentRemove(Class<C> componentClass) {

    if (this.entityExists()) {
      this.world.componentRemove(this, componentClass);
      this.world.entityChange(this);
    }
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    EntityInternal entityReference = (EntityInternal) o;

    return this.id == entityReference.id;
  }

  @Override
  public int hashCode() {

    return (int) (this.id ^ (this.id >>> 32));
  }

  @Override
  public String toString() {

    return "Entity{" +
        "id=" + this.id +
        ", world=" + this.world +
        '}';
  }
}
