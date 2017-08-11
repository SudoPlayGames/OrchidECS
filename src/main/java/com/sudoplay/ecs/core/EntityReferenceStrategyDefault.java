package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.Entity;
import com.sudoplay.ecs.integration.spi.ComponentRegistry;
import com.sudoplay.ecs.integration.spi.EntityReferenceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.BitSet;
import java.util.Map;

public class EntityReferenceStrategyDefault implements
    EntityReferenceStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      EntityReferenceStrategyDefault.class);

  private long[] nextEntityId;
  private ComponentRegistry componentRegistry;
  private Map<Long, Reference<EntityInternal>> entityReferenceMap;
  private Map<Long, BitSet> entityComponentBitSetMap;

  public EntityReferenceStrategyDefault(
      long[] nextEntityId,
      ComponentRegistry componentRegistry,
      Map<Long, Reference<EntityInternal>> entityReferenceMap,
      Map<Long, BitSet> entityComponentBitSetMap
  ) {

    this.nextEntityId = nextEntityId;
    this.componentRegistry = componentRegistry;
    this.entityReferenceMap = entityReferenceMap;
    this.entityComponentBitSetMap = entityComponentBitSetMap;
  }

  @Override
  public Entity entityCreate() {

    return this.entityGet(this.nextEntityId[0]);
  }

  /**
   * Retrieve or create an entity.
   *
   * @param entityId the entity id
   * @return the created entity
   */
  @Override
  public Entity entityGet(long entityId) {

    if (entityId == this.nextEntityId[0]) {

      // entity hasn't been created yet, create entity

      EntityInternal entity = new EntityInternal(this.nextEntityId[0]);
      int componentCount = this.componentRegistry.componentCountGet();

      // new entity bitset entry
      this.entityComponentBitSetMap.put(
          this.nextEntityId[0],
          new BitSet(componentCount)
      );

      // cache the entity reference
      this.entityReferenceMap.put(
          this.nextEntityId[0],
          new WeakReference<>(entity)
      );

      // use up an id
      this.nextEntityId[0] += 1;

      LOGGER.debug("Created entity [{}]", entity);

      return entity;

    } else if (entityId < this.nextEntityId[0]) {

      // entity id has already been assigned, check for
      // existing entity reference

      Reference<EntityInternal> ref = this.entityReferenceMap.get(entityId);

      if (ref == null
          || ref.get() == null) {

        // reference is missing or expired, create a new one

        EntityInternal entity = new EntityInternal(entityId);
        this.entityReferenceMap.put(entityId, new WeakReference<>(entity));
        int componentCount = this.componentRegistry.componentCountGet();

        this.entityComponentBitSetMap.put(
            entityId,
            new BitSet(componentCount)
        );

        return entity;

        // else, entity doesn't exist and Entity.NULL will be returned

      } else {

        // return active entity reference

        return ref.get();

      }

    } else if (entityId > this.nextEntityId[0]) {

      throw new IndexOutOfBoundsException(String.format(
          "Requested entity id [%d] is larger than the next entity id [%d]",
          entityId,
          this.nextEntityId[0]
      ));

    }

    return Entity.NULL;

  }

}
