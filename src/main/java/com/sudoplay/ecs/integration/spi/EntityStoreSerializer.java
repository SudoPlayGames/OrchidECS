package com.sudoplay.ecs.integration.spi;

import com.sudoplay.ecs.core.World;

import java.util.Map;

public interface EntityStoreSerializer {

  void write(
      Map<Integer, Map<Long, Component>> entityStore,
      WorldWriter out
  );

  void read(
      World world,
      WorldReader in,
      ComponentRegistry componentRegistry
  );

}
