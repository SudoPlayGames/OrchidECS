package com.sudoplay.ecs.integration.api;

import java.util.Deque;
import java.util.function.Consumer;

public interface EntitySet {

  boolean contains(Entity entity);

  void forEach(Consumer<Entity> consumer);

  Deque<Entity> newDequeEventEntityAdd();

  Deque<Entity> newDequeEventEntityRemove();

}
