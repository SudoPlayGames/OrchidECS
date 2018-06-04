package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.spi.EntityEventBase;

public abstract class WorldEvent
    extends EntityEventBase {

  /**
   * This event is fired immediately before an entity is removed from the
   * world. If cancelled, the entity will not be removed. At the time of
   * this event, the entity still has all of its components and has yet
   * to be removed from any interested entity set.
   */
  public static class EntityRemovePreEvent
      extends WorldEvent {

  }

  /**
   * This event is fired immediately after an entity is removed from the
   * world and prior to the entity being reclaimed by the object pool. At
   * this time, the entity will not have any components and will have been
   * removed from all interested entity sets. The entity's id is still valid.
   */
  public static class EntityRemovePostEvent
      extends WorldEvent {

  }

  /**
   * This event is fired immediately before an entity is added to the
   * world and before it is added to any entity sets. Cancelling this
   * event will prevent the entity from being added.
   */
  public static class EntityAddPreEvent
      extends WorldEvent {

  }

  /**
   * This event is fired immediately after an entity is added to the
   * world and any interested entity sets.
   */
  public static class EntityAddPostEvent
      extends WorldEvent {

  }

  /**
   * This event is fired when a component is added to or removed from an
   * entity in the world.
   */
  public static class EntityChangedEvent
      extends WorldEvent {

  }

}
