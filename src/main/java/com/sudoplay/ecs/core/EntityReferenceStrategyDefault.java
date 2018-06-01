package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.Entity;
import com.sudoplay.ecs.integration.spi.ComponentRegistry;
import com.sudoplay.ecs.integration.spi.EntityReferenceStrategy;
import com.sudoplay.ecs.util.LongMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityReferenceStrategyDefault
    implements
    EntityReferenceStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      EntityReferenceStrategyDefault.class);

  private long[] nextEntityId;
  private ComponentRegistry componentRegistry;
  private LongMap<EntityInternal> entityReferenceMap;
  private LongMap<PooledBitSet> entityComponentBitSetMap;
  private ObjectPool<EntityInternal> entityInternalObjectPool;
  private ObjectPool<PooledBitSet> pooledBitSetObjectPool;

  public EntityReferenceStrategyDefault(
      long[] nextEntityId,
      ComponentRegistry componentRegistry,
      LongMap<EntityInternal> entityReferenceMap,
      LongMap<PooledBitSet> entityComponentBitSetMap,
      ObjectPool<EntityInternal> entityInternalObjectPool,
      ObjectPool<PooledBitSet> pooledBitSetObjectPool
  ) {

    this.nextEntityId = nextEntityId;
    this.componentRegistry = componentRegistry;
    this.entityReferenceMap = entityReferenceMap;
    this.entityComponentBitSetMap = entityComponentBitSetMap;
    this.entityInternalObjectPool = entityInternalObjectPool;
    this.pooledBitSetObjectPool = pooledBitSetObjectPool;
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

      EntityInternal entity = this.entityInternalObjectPool.get();
      entity.init(this.nextEntityId[0]);

      // new entity bitset entry
      this.entityComponentBitSetMap.put(
          this.nextEntityId[0],
          this.pooledBitSetObjectPool.get()
      );

      // cache the entity reference
      this.entityReferenceMap.put(this.nextEntityId[0], entity);

      // use up an id
      this.nextEntityId[0] += 1;

      LOGGER.debug("Created entity [{}]", entity);

      return entity;

    } else if (entityId < this.nextEntityId[0]) {

      // entity id has already been assigned, check for
      // existing entity reference

      EntityInternal entity = this.entityReferenceMap.get(entityId);

      if (entity == null) {

        // reference is missing or expired, create a new one

        entity = this.entityInternalObjectPool.get();
        entity.init(entityId);
        this.entityReferenceMap.put(entityId, entity);

        this.entityComponentBitSetMap.put(
            entityId,
            this.pooledBitSetObjectPool.get()
        );

        return entity;

        // else, entity doesn't exist and Entity.NULL will be returned

      } else {

        // return active entity reference

        return entity;
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

  @Override
  public void reclaim(Entity entity) {

    if (entity instanceof EntityInternal) {
      ((EntityInternal) entity).reset();
      this.entityInternalObjectPool.reclaim((EntityInternal) entity);
    }
  }
}
