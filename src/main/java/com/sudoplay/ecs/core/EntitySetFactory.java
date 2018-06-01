package com.sudoplay.ecs.core;

import com.sudoplay.ecs.util.LongMap;

import java.util.BitSet;

public class EntitySetFactory {

  private LongMap<PooledBitSet> entityComponentBitSetMap;

  public EntitySetFactory(LongMap<PooledBitSet> entityComponentBitSetMap) {

    this.entityComponentBitSetMap = entityComponentBitSetMap;
  }

  /* package */ EntitySetInternal createAspectEntitySet(Aspect aspect) {

    return new EntitySetInternal(
        this.entityComponentBitSetMap,
        aspect
    );

  }

}
