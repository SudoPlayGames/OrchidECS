package com.sudoplay.ecs.integration.api;

import java.util.Iterator;

public interface EntitySet {

  boolean contains(Entity entity);

  /**
   * Returns the same iterator each time this is called; not suitable for nested loops.
   * <p>
   * Remove method is not supported.
   *
   * @return value iterator
   */
  Iterable<Entity> entitiesGet();

  /**
   * Returns a new iterator each time this is called; suitable for nested loops.
   * <p>
   * The returned value should be cached and reused.
   * <p>
   * Remove method is not supported.
   *
   * @return value iterator
   */
  ReusableIterator<Entity> entityIteratorCreate();

  interface ReusableIterator<T>
      extends Iterator<T>,
      Iterable<T> {

    void reset();
  }

}
