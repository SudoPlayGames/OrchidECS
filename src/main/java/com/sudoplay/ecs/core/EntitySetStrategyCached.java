package com.sudoplay.ecs.core;

import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EntitySetStrategyCached implements
    EntitySetStrategy {

  /**
   * Provides aspect entity sets when requested.
   */
  private EntitySetFactory aspectEntitySetFactory;

  /**
   * Stores aspect entity sets by aspect.
   */
  private Map<Aspect, EntitySetInternal> aspectEntitySetByAspectMap;

  /**
   * Stores the aspect entity sets for iteration.
   */
  private List<EntitySetInternal> entitySetList;

  /* package */ EntitySetStrategyCached(
      EntitySetFactory aspectEntitySetFactory,
      LinkedList<EntitySetInternal> entitySetList
  ) {

    this.aspectEntitySetFactory = aspectEntitySetFactory;

    this.aspectEntitySetByAspectMap = HashObjObjMaps.getDefaultFactory()
        .newUpdatableMap();

    this.entitySetList = entitySetList;
  }

  @Override
  public EntitySetInternal getEntitySet(Aspect aspect) {

    return this.aspectEntitySetByAspectMap.computeIfAbsent(
        aspect,
        a -> {
          // create a new aspect entity set and add it to the local list
          EntitySetInternal newEntitySet = this.aspectEntitySetFactory
              .createAspectEntitySet(a);
          this.entitySetList.add(newEntitySet);
          return newEntitySet;
        }
    );

  }

}
