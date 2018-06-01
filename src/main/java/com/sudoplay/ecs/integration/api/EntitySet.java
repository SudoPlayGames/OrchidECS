package com.sudoplay.ecs.integration.api;

import com.sudoplay.ecs.util.LongMap;

import java.util.Deque;

public interface EntitySet {

  boolean contains(Entity entity);

  LongMap.Values<Entity> entitiesGet();

  Deque<Entity> newDequeEventEntityAdd();

  Deque<Entity> newDequeEventEntityRemove();

}
