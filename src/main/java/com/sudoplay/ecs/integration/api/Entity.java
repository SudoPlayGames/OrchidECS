package com.sudoplay.ecs.integration.api;

import com.sudoplay.ecs.integration.spi.Component;

public interface Entity {

  Entity NULL = new Entity() {

    @Override
    public void worldAdd() {
      //
    }

    @Override
    public void worldRemove() {
      //
    }

    @Override
    public boolean entityExists() {

      return false;
    }

    @Override
    public <C extends Component> void componentAdd(C component) {
      //
    }

    @Override
    public <C extends Component> void componentRemove(Class<C> componentClass) {
      //
    }

  };

  /**
   * Add this entity to the world.
   */
  void worldAdd();

  /**
   * Remove this entity from the world.
   */
  void worldRemove();

  /**
   * @return true if this entity exists in the world
   */
  boolean entityExists();

  <C extends Component> void componentAdd(C component);

  <C extends Component> void componentRemove(Class<C> componentClass);

}
