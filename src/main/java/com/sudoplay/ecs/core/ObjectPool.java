package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.Poolable;

import java.util.ArrayList;
import java.util.List;

public class ObjectPool<P extends Poolable> {

  private final List<P> pool;
  private final Factory<P> factory;

  public ObjectPool(Class<P> poolableClass) {

    this(poolableClass, new ArrayList<P>());
  }

  public ObjectPool(Class<P> poolableClass, List<P> pool) {

    this(ObjectPool.factory(poolableClass), pool);
  }

  public ObjectPool(Factory<P> factory, List<P> pool) {

    this.factory = factory;
    this.pool = pool;
  }

  public P get() {

    if (this.pool.isEmpty()) {

      return this.factory.create();

    } else {

      return this.pool.remove(this.pool.size() - 1);
    }
  }

  public void reclaim(P poolable) {

    poolable.reset();
    this.pool.add(poolable);
  }

  public static <P extends Poolable> Factory<P> factory(final Class<P> poolableClass) {

    return new Factory<P>() {

      @Override
      public P create() {

        try {
          return poolableClass.newInstance();

        } catch (InstantiationException e) {
          throw new RuntimeException("", e);

        } catch (IllegalAccessException e) {
          throw new RuntimeException("", e);
        }
      }
    };
  }

  public interface Factory<T> {

    T create();
  }
}
