package com.sudoplay.ecs.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EntitySetStrategyCached
    implements EntitySetStrategy {

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

    this.aspectEntitySetByAspectMap = new HashMap<Aspect, EntitySetInternal>();

    this.entitySetList = entitySetList;
  }

  @Override
  public EntitySetInternal getEntitySet(Aspect aspect) {

    EntitySetInternal entitySetInternal = this.aspectEntitySetByAspectMap.get(aspect);

    if (entitySetInternal == null) {
      // create a new aspect entity set and add it to the local list
      entitySetInternal = this.aspectEntitySetFactory
          .createAspectEntitySet(aspect);
      this.entitySetList.add(entitySetInternal);
      this.aspectEntitySetByAspectMap.put(aspect, entitySetInternal);
    }

    return entitySetInternal;
  }

}
