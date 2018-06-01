package com.sudoplay.ecs.integration.api;

import com.sudoplay.ecs.integration.spi.Component;
import com.sudoplay.ecs.util.LongMap;

public interface ComponentMapper<C extends Component> {

  C get(Entity entity);

  boolean has(Entity entity);

  LongMap.Values<C> componentsGet();

}
