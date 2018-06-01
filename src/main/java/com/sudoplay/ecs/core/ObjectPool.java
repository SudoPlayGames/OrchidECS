package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.Poolable;

import java.util.List;

public class ObjectPool<P extends Poolable> {

  private List<P> pool;
  private Factory<P> factory;

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

  public interface Factory<T> {

    T create();
  }
}
