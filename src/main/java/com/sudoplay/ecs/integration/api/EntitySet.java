package com.sudoplay.ecs.integration.api;

import com.sudoplay.ecs.integration.spi.EntitySetEventHandler;

import java.util.function.Consumer;

public interface EntitySet {

  boolean contains(Entity entity);

  void forEach(Consumer<Entity> consumer);

  void subscribe(EntitySetEventHandler eventHandler);

}
