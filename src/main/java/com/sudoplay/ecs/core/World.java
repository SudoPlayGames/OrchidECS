package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.ComponentMapper;
import com.sudoplay.ecs.integration.api.Entity;
import com.sudoplay.ecs.integration.spi.*;
import com.sudoplay.ecs.util.IntMap;
import com.sudoplay.ecs.util.LongMap;

import java.util.*;

public class World {

  /**
   * Components are referenced by component type index and entity id.
   * <p>
   * Used to provide component mappers with a backing map.
   */
  private IntMap<LongMap<Component>> componentsByTypeIndexMap;

  /**
   * Stores a BitSet that is used as a component mask. If an entity has a
   * component, its BitSet will be true for the bit corresponding to the
   * component's type index.
   * <p>
   * Used for quick comparison between an entity's components and an aspect.
   */
  private LongMap<PooledBitSet> entityComponentBitSetMap;

  /**
   * Stores reference to the entity objects.
   * <p>
   * This allows returning the same entity object for subsequent requests.
   */
  private LongMap<EntityInternal> entityReferenceMap;

  /**
   * Stores the meta information about components registered for use with
   * this world.
   */
  private ComponentRegistry componentRegistry;

  /**
   * Provides component mappers.
   */
  private ComponentMapperStrategy componentMapperStrategy;

  /**
   * The entity set list.
   */
  private List<EntitySetInternal> entitySetList;

  /**
   * Component event queue.
   */
  private Deque<ComponentSystemEvent> componentSystemEventQueue;

  /**
   * Queue to store entities that have been added to the world.
   */
  private Deque<EntityInternal> entityQueueAdded;

  /**
   * Queue to store entities that have had a component added or removed.
   */
  private Deque<EntityInternal> entityQueueChanged;

  /**
   * Queue to store entities that have been removed from the world.
   */
  private Deque<EntityInternal> entityQueueRemoved;

  /**
   * The event bus.
   */
  private EventBus eventBus;

  /**
   * The world serializer.
   */
  private WorldSerializer worldSerializer;

  /**
   * Responsible for injecting annotated fields in Objects passed to it.
   */
  private SystemFieldInjector systemFieldInjector;

  /**
   * Provides entity reference objects when requested.
   */
  private EntityReferenceStrategy entityReferenceStrategy;

  private ObjectPool<ComponentSystemEvent> componentSystemEventObjectPool;

  private ObjectPool<PooledBitSet> pooledBitSetObjectPool;

  private Map<Class<? extends Component>, ObjectPool> componentPoolMap;
  /**
   * Contains the next entity id at position 0; currently only used by this
   * class during serialization
   */
  private long[] nextEntityId;

  // --------------------------------------------------------------------------
  // -- Initialization
  // --------------------------------------------------------------------------

  /**
   * World constructor.
   *
   * @param componentRegistry              stores component class / index relations
   * @param componentMapperStrategy        provides component mappers
   * @param entitySetList                  the entity set list
   * @param entityReferenceMap
   * @param entityComponentBitSetMap       maps entity id's to component id flags as bitsets
   * @param eventBus                       the event bus
   * @param worldSerializer                serializes the world
   * @param componentsByTypeIndexMap       the entity store
   * @param systemFieldInjector            injects system fields, entity sets and component mappers
   * @param entityReferenceStrategy        provides entity references
   * @param componentSystemEventObjectPool
   * @param pooledBitSetObjectPool
   * @param componentPoolMap
   * @param nextEntityId                   the next entity id
   */
  /* package */ World(
      ComponentRegistry componentRegistry,
      ComponentMapperStrategy componentMapperStrategy,
      List<EntitySetInternal> entitySetList,
      LongMap<EntityInternal> entityReferenceMap,
      LongMap<PooledBitSet> entityComponentBitSetMap,
      EventBus eventBus,
      WorldSerializer worldSerializer,
      IntMap<LongMap<Component>> componentsByTypeIndexMap,
      SystemFieldInjector systemFieldInjector,
      EntityReferenceStrategy entityReferenceStrategy,
      ObjectPool<ComponentSystemEvent> componentSystemEventObjectPool,
      ObjectPool<PooledBitSet> pooledBitSetObjectPool,
      Map<Class<? extends Component>, ObjectPool> componentPoolMap,
      long[] nextEntityId
  ) {

    this.entitySetList = entitySetList;
    this.eventBus = eventBus;
    this.worldSerializer = worldSerializer;

    this.componentsByTypeIndexMap = componentsByTypeIndexMap;
    this.systemFieldInjector = systemFieldInjector;
    this.entityReferenceStrategy = entityReferenceStrategy;
    this.componentSystemEventObjectPool = componentSystemEventObjectPool;
    this.pooledBitSetObjectPool = pooledBitSetObjectPool;
    this.componentPoolMap = componentPoolMap;
    this.nextEntityId = nextEntityId;

    this.entityReferenceMap = entityReferenceMap;

    this.entityComponentBitSetMap = entityComponentBitSetMap;

    this.componentMapperStrategy = componentMapperStrategy;
    this.componentRegistry = componentRegistry;

    this.componentSystemEventQueue = new ArrayDeque<ComponentSystemEvent>();

    this.entityQueueAdded = new ArrayDeque<EntityInternal>();
    this.entityQueueChanged = new ArrayDeque<EntityInternal>();
    this.entityQueueRemoved = new ArrayDeque<EntityInternal>();
  }

  // --------------------------------------------------------------------------
  // -- Entity
  // --------------------------------------------------------------------------

  /**
   * @return a new entity
   */
  public Entity entityCreate() {

    EntityInternal entity = (EntityInternal) this.entityReferenceStrategy.entityCreate();

    if (entity != Entity.NULL) {
      entity.setWorld(this);
    }

    return entity;
  }

  /**
   * Retrieve or create an entity.
   *
   * @param entityId the entity id
   * @return the created entity
   */
  public Entity entityGet(long entityId) {

    Entity entity = this.entityReferenceStrategy.entityGet(entityId);

    if (entity instanceof EntityInternal) {
      ((EntityInternal) entity).setWorld(this);
    }

    return entity;
  }

  /* package */ void entityAdd(EntityInternal entityReference) {

    this.entityQueueAdded.add(entityReference);
  }

  /* package */  void entityChange(EntityInternal entityReference) {

    this.entityQueueChanged.add(entityReference);
  }

  /* package */  void entityRemove(EntityInternal entityReference) {

    this.entityQueueRemoved.add(entityReference);
  }

  // --------------------------------------------------------------------------
  // -- Component
  // --------------------------------------------------------------------------

  public <C extends Component> C componentCreate(Class<C> componentClass) {

    //noinspection unchecked
    return (C) this.componentPoolMap.get(componentClass).get();
  }

  /**
   * Add a component to an entity.
   * <p>
   * Note: Only add components created by calling {@link World#componentCreate(Class)}.
   *
   * @param entityReference the entity
   * @param component       the component
   */
  /* package */ void componentAdd(
      EntityInternal entityReference,
      Component component
  ) {

    Class<? extends Component> componentClass;
    ComponentType componentType;

    componentClass = component.getClass();
    componentType = this.componentRegistry.componentTypeGet(componentClass);

    this.componentSystemEventQueue.offer(this.componentSystemEventObjectPool.get().init(
        ComponentSystemEvent.EventType.ADD,
        entityReference,
        componentType,
        component
    ));
  }

  /**
   * Remove a component from an entity.
   *
   * @param entityReference the entity
   * @param componentClass  the component
   */
  /* package */ <C extends Component> void componentRemove(
      EntityInternal entityReference,
      Class<C> componentClass
  ) {

    ComponentType componentType;
    ComponentMapper<C> componentMapper;

    componentType = this.componentRegistry.componentTypeGet(componentClass);
    componentMapper = this.componentMapperStrategy.getComponentMapper(
        componentClass);

    if (componentMapper.has(entityReference)) {

      this.componentSystemEventQueue.offer(this.componentSystemEventObjectPool.get().init(
          ComponentSystemEvent.EventType.REMOVE,
          entityReference,
          componentType,
          componentMapper.get(entityReference)
      ));
    }
  }

  // --------------------------------------------------------------------------
  // -- Event
  // --------------------------------------------------------------------------

  public void eventSubscribe(Object subscriber) {

    this.eventBus.subscribe(subscriber);
  }

  public void eventPublish(EntityEventBase event) {

    this.eventBus.publish(event);
  }

  // --------------------------------------------------------------------------
  // -- Update
  // --------------------------------------------------------------------------

  /**
   * Must to be called once and only once per update loop.
   */
  public void update() {

    ComponentSystemEvent componentSystemEvent;

    while ((componentSystemEvent = this.componentSystemEventQueue.poll()) != null) {
      this.processComponentEvent(componentSystemEvent);
      this.componentSystemEventObjectPool.reclaim(componentSystemEvent);
    }

    EntityInternal entity;

    // remove event
    while ((entity = this.entityQueueRemoved.pollFirst()) != null) {

      long id = entity.getId();

      this.entityReferenceMap.remove(id);

      PooledBitSet pooledBitSet = this.entityComponentBitSetMap.remove(id);
      BitSet bitSet = pooledBitSet.getBitSet();

      for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
        LongMap<Component> map = this.componentsByTypeIndexMap.get(i);
        Component component = map.remove(id);
        //noinspection unchecked
        this.componentPoolMap.get(component.getClass()).reclaim(component);
      }

      for (int i = 0; i < this.entitySetList.size(); i++) {
        EntitySetInternal entitySet = this.entitySetList.get(i);
        entitySet.onSystemEvent(entity, EntitySetInternal.EventType.REMOVE);
      }

      this.entityReferenceStrategy.reclaim(entity);
      this.pooledBitSetObjectPool.reclaim(pooledBitSet);
    }

    // added event
    while ((entity = this.entityQueueAdded.pollFirst()) != null) {

      for (int i = 0; i < this.entitySetList.size(); i++) {
        EntitySetInternal entitySet = this.entitySetList.get(i);
        entitySet.onSystemEvent(entity, EntitySetInternal.EventType.ADD);
      }
    }

    // changed event
    while ((entity = this.entityQueueChanged.pollFirst()) != null) {

      for (int i = 0; i < this.entitySetList.size(); i++) {
        EntitySetInternal entitySet = this.entitySetList.get(i);
        entitySet.onSystemEvent(entity, EntitySetInternal.EventType.CHANGE);
      }
    }

  }

  private void processComponentEvent(ComponentSystemEvent event) {

    ComponentSystemEvent.EventType eventType = event.getEventType();
    Component component = event.getComponent();
    ComponentType componentType = event.getComponentType();
    EntityInternal entityReference = event.getEntityReference();

    switch (eventType) {

      case ADD: {
        LongMap<Component> entityComponentMap;

        entityComponentMap = this.componentsByTypeIndexMap.get(componentType.getIndex());

        if (entityComponentMap == null) {
          entityComponentMap = new LongMap<Component>(32);
          this.componentsByTypeIndexMap.put(componentType.getIndex(), entityComponentMap);
        }

        entityComponentMap.put(entityReference.getId(), component);

        PooledBitSet pooledBitSet = this.entityComponentBitSetMap.get(entityReference.getId());

        if (pooledBitSet == null) {
          pooledBitSet = this.pooledBitSetObjectPool.get();
          this.entityComponentBitSetMap.put(entityReference.getId(), pooledBitSet);
        }

        pooledBitSet.getBitSet().set(componentType.getIndex());

        break;
      }

      case REMOVE: {
        int index;
        LongMap<Component> entityComponentMap;

        index = componentType.getIndex();
        entityComponentMap = this.componentsByTypeIndexMap.get(index);

        if (entityComponentMap != null) {
          entityComponentMap.remove(entityReference.getId());
        }

        PooledBitSet pooledBitSet = this.entityComponentBitSetMap.get(entityReference.getId());

        if (pooledBitSet == null) {
          pooledBitSet = this.pooledBitSetObjectPool.get();
          this.entityComponentBitSetMap.put(entityReference.getId(), pooledBitSet);
        }

        pooledBitSet.getBitSet().clear(componentType.getIndex());
        ObjectPool componentPool = this.componentPoolMap.get(component.getClass());

        //noinspection unchecked
        componentPool.reclaim(component);

        break;
      }

      default:
        //

    }

  }

  // --------------------------------------------------------------------------
  // -- Serialization
  // --------------------------------------------------------------------------

  public void serializerSet(WorldSerializer worldSerializer) {

    this.worldSerializer = worldSerializer;
  }

  public void serializerWrite(WorldWriter out) {

    if (this.worldSerializer != null) {
      this.worldSerializer.write(
          this.componentsByTypeIndexMap,
          out,
          this.nextEntityId
      );
    }
  }

  public void serializerRead(WorldReader in) {

    if (this.worldSerializer != null) {
      this.worldSerializer.read(
          this,
          in,
          this.nextEntityId,
          this.componentRegistry
      );
    }
  }

}
