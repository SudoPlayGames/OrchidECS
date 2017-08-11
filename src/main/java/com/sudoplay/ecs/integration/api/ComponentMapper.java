package com.sudoplay.ecs.integration.api;

import com.sudoplay.ecs.integration.spi.Component;

import java.util.function.Consumer;

public interface ComponentMapper<C extends Component> {

  C get(Entity entity);

  boolean has(Entity entity);

  void forEach(
      Consumer<Component> consumer
  );
}
