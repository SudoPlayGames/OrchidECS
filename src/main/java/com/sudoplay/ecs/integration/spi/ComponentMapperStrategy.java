package com.sudoplay.ecs.integration.spi;

import com.sudoplay.ecs.integration.api.ComponentMapper;

public interface ComponentMapperStrategy {

  <C extends Component> ComponentMapper<C> getComponentMapper(
      Class<C> componentClass
  );

}
