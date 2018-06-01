package com.sudoplay.ecs.integration.spi;

import com.sudoplay.ecs.core.World;
import com.sudoplay.ecs.util.IntMap;
import com.sudoplay.ecs.util.LongMap;

public interface WorldSerializer {

  void write(
      IntMap<LongMap<Component>> componentsByTypeIndexMap,
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
