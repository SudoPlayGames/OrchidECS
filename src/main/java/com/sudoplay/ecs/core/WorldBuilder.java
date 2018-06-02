package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.ComponentMapper;
import com.sudoplay.ecs.integration.api.ComponentRegistrationException;
import com.sudoplay.ecs.integration.spi.Component;
import com.sudoplay.ecs.integration.spi.ComponentMapperStrategy;
import com.sudoplay.ecs.integration.spi.ComponentRegistry;
import com.sudoplay.ecs.integration.spi.WorldSerializer;
import com.sudoplay.ecs.util.ClassFieldIterator;
import com.sudoplay.ecs.util.IntMap;
import com.sudoplay.ecs.util.LongMap;

import java.util.*;

public class WorldBuilder {

  private WorldSerializer worldSerializer;
  private ComponentRegistry componentRegistry;
  private Map<Class<? extends Component>, ObjectPool> componentPoolMap;
  private List<Object> systemList;

  public WorldBuilder() {

    this.componentRegistry = new ComponentRegistryDefault();
    this.componentPoolMap = new HashMap<Class<? extends Component>, ObjectPool>();
    this.systemList = new LinkedList<Object>();
  }

  public <C extends Component> WorldBuilder registerComponents(final Class<C>... componentClasses) {

    for (Class<C> componentClass : componentClasses) {
      this.registerComponent(componentClass);
    }

    return this;
  }

  public <C extends Component> WorldBuilder registerComponent(final Class<C> componentClass) {

    return this.registerComponent(componentClass, new ObjectPool.Factory<C>() {

      @Override
      public C create() {

        try {
          return componentClass.newInstance();

        } catch (InstantiationException e) {
          throw new RuntimeException("", e);

        } catch (IllegalAccessException e) {
          throw new RuntimeException("", e);
        }
      }
    });
  }

  /**
   * Register a component and its associated factory.
   *
   * @param componentClass the component class
   * @param factory        the component factory
   * @return this builder
   */
  public <C extends Component> WorldBuilder registerComponent(Class<C> componentClass, ObjectPool.Factory<C> factory) {

    if (Component.class.isAssignableFrom(componentClass)) {

      // if a world serializer is registered, we need to ensure that it
      // is capable of processing the new component class

      if (this.worldSerializer != null) {

        if (!this.worldSerializer.canSerializeComponent(componentClass)) {
          throw new ComponentRegistrationException(String.format(
              "Can't serialize component class [%s]",
              componentClass.getName()
          ));
        }

      }

      this.componentRegistry.componentRegister(componentClass);

      //noinspection unchecked
      this.componentPoolMap.put(componentClass, new ObjectPool(factory, new ArrayList<Component>()));

    } else {
      throw new ComponentRegistrationException(String.format(
          "Component class [%s] must extend [%s]",
          componentClass.getName(),
          Component.class.getName()
      ));
    }

    return this;
  }

  /**
   * Register all systems for this world here.
   *
   * @param systems the systems
   * @return this builder
   */
  public WorldBuilder registerSystems(Object... systems) {

    Collections.addAll(this.systemList, systems);
    return this;
  }

  /**
   * Sets the world serializer that will be used for persistence.
   *
   * @param worldSerializer world serializer
   * @return this builder
   */
  public WorldBuilder setWorldSerializer(WorldSerializer worldSerializer) {

    // first, check that the new world serializer can serialize all
    // components contained in the component registry's collection

    Collection<ComponentType> componentTypes = this.componentRegistry
        .componentTypeCollectionGet(new ArrayList<ComponentType>());

    for (ComponentType componentType : componentTypes) {

      Class<? extends Component> componentClass = componentType.getType();

      //noinspection unchecked
      if (!worldSerializer.canSerializeComponent(componentClass)) {

        // if the serializer can't process a component class, fail fast, fail hard
        throw new ComponentRegistrationException(String.format(
            "Can't serialize component class [%s]",
            componentClass.getName()
        ));
      }

    }

    this.worldSerializer = worldSerializer;

    return this;
  }

  public World create() {

    return this.create(Long.MIN_VALUE);
  }

  public World create(long initialEntityId) {

    final int componentCount = this.componentRegistry.componentCountGet();

    // used for quick comparison of an entity's component composition against
    // an aspect for entity sets
    LongMap<PooledBitSet> entityComponentBitSetMap = new LongMap<PooledBitSet>(1024);

    // entity set list for iteration
    LinkedList<EntitySetInternal> entitySetList = new LinkedList<EntitySetInternal>();

    // creates and retains entity sets
    EntitySetStrategy entitySetStrategy = new EntitySetStrategyCached(
        new EntitySetFactory(
            entityComponentBitSetMap
        ),
        entitySetList
    );

    // used as on observable long
    long[] nextEntityId = new long[1];
    nextEntityId[0] = initialEntityId;

    IntMap<LongMap<Component>> componentsByTypeIndexMap = new IntMap<LongMap<Component>>(256);

    // creates and retains component mappers
    ComponentMapperStrategy componentMapperStrategy = new ComponentMapperStrategyCached(
        componentsByTypeIndexMap,
        new HashMap<Class<? extends Component>, ComponentMapper<? extends Component>>(),
        this.componentRegistry
    );

    // stores references to entity references
    LongMap<EntityInternal> entityReferenceMap = new LongMap<EntityInternal>(1024);

    // aspect filtered priority entity event bus
    EventBus eventBus = new EventBus(
        this.componentRegistry,
        entitySetStrategy
    );

    // injects entity sets and component mappers into annotated class fields
    SystemFieldInjectorDefault systemFieldInjector = new SystemFieldInjectorDefault(
        this.componentRegistry,
        entitySetStrategy,
        componentMapperStrategy,
        new ClassFieldIterator()
    );

    ObjectPool<EntityInternal> entityInternalObjectPool = new ObjectPool<EntityInternal>(
        new ObjectPool.Factory<EntityInternal>() {

          @Override
          public EntityInternal create() {

            return new EntityInternal();
          }
        },
        new ArrayList<EntityInternal>()
    );

    ObjectPool<PooledBitSet> pooledBitSetObjectPool = new ObjectPool<PooledBitSet>(
        new ObjectPool.Factory<PooledBitSet>() {

          @Override
          public PooledBitSet create() {

            return new PooledBitSet(new BitSet(componentCount));
          }
        },
        new ArrayList<PooledBitSet>()
    );

    ObjectPool<ComponentSystemEvent> componentSystemEventObjectPool = new ObjectPool<ComponentSystemEvent>(
        new ObjectPool.Factory<ComponentSystemEvent>() {

          @Override
          public ComponentSystemEvent create() {

            return new ComponentSystemEvent();
          }
        },
        new ArrayList<ComponentSystemEvent>()
    );

    EntityReferenceStrategyDefault entityReferenceStrategy = new EntityReferenceStrategyDefault(
        nextEntityId,
        this.componentRegistry,
        entityReferenceMap,
        entityComponentBitSetMap,
        entityInternalObjectPool,
        pooledBitSetObjectPool
    );

    World world = new World(
        this.componentRegistry,
        componentMapperStrategy,
        entitySetList,
        entityReferenceMap,
        entityComponentBitSetMap,
        eventBus,
        this.worldSerializer,
        componentsByTypeIndexMap,
        systemFieldInjector,
        entityReferenceStrategy,
        componentSystemEventObjectPool,
        pooledBitSetObjectPool,
        this.componentPoolMap,
        nextEntityId
    );

    systemFieldInjector.setWorld(world);

    for (Object system : this.systemList) {
      world.eventSubscribe(system);
      systemFieldInjector.inject(system);
    }

    return world;

  }

}
