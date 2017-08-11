package com.sudoplay.ecs.core;

import java.util.BitSet;
import java.util.Map;

public class EntitySetFactory {

  private Map<Long, BitSet> entityComponentBitSetMap;

  public EntitySetFactory(Map<Long, BitSet> entityComponentBitSetMap) {

    this.entityComponentBitSetMap = entityComponentBitSetMap;
  }

  /* package */ EntitySetInternal createAspectEntitySet(Aspect aspect) {

    return new EntitySetInternal(
        this.entityComponentBitSetMap,
        aspect
    );

  }

}
