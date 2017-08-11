package com.sudoplay.ecs.integration.spi;

import com.sudoplay.ecs.core.World;

import java.util.Map;

public interface WorldSerializer {

  void write(
      Map<Integer, Map<Long, Component>> componentsByTypeIndexMap,
      WorldWriter out,
      long[] nextEntityId
  );

  void read(
      World world,
      WorldReader in,
      long[] nextEntityId,
      ComponentRegistry componentRegistry
  );

  boolean canSerializeComponent(Class<? extends Component> component);

}
