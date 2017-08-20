package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.ComponentRegistrationException;
import com.sudoplay.ecs.integration.spi.Component;
import com.sudoplay.ecs.integration.spi.ComponentMapperStrategy;
import com.sudoplay.ecs.integration.spi.ComponentRegistry;
import com.sudoplay.ecs.integration.spi.WorldSerializer;
import com.sudoplay.ecs.koloboke.ComponentId_EntityIdComponentMap_Map;
import com.sudoplay.ecs.koloboke.EntityIdBitSetMap;
import com.sudoplay.ecs.koloboke.EntityIdReferenceMap;
import com.sudoplay.ecs.util.ClassFieldIterator;

import java.lang.ref.Reference;
import java.util.*;

public class WorldBuilder {

  private WorldSerializer worldSerializer;
  private ComponentRegistry componentRegistry;
  private List<Object> systemList;

  public WorldBuilder() {

    this.componentRegistry = new ComponentRegistryDefault();
    this.systemList = new LinkedList<>();
  }

  /**
   * Register all components for this world here.
   *
   * @param classes the component classes
   * @return this builder
   */
  public WorldBuilder registerComponents(Class... classes) {

    for (Class componentClass : classes) {

      if (Component.class.isAssignableFrom(componentClass)) {

        // if a world serializer is registered, we need to ensure that it
        // is capable of processing the new component class

        if (this.worldSerializer != null) {

          //noinspection unchecked
          if (!this.worldSerializer.canSerializeComponent(componentClass)) {
            throw new ComponentRegistrationException(String.format(
                "Can't serialize component class [%s]",
                componentClass.getName()
            ));
          }

        }

        //noinspection unchecked
        this.componentRegistry.componentRegister(componentClass);

      } else {
        throw new ComponentRegistrationException(String.format(
            "Component class [{}] must extend [{}]",
            componentClass.getName(),
            Component.class.getName()
        ));
      }

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

    Collection<ComponentType> componentTypes = this.componentRegistry.componentTypeCollectionGet(
        new ArrayList<>());

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

    // used for quick comparison of an entity's component composition against
    // an aspect for entity sets
    Map<Long, BitSet> entityComponentBitSetMap = EntityIdBitSetMap
        .withExpectedSize(1024);

    // entity set list for iteration
    LinkedList<EntitySetInternal> entitySetList = new LinkedList<>();

    // creates and retains entity sets
    EntitySetStrategy entitySetStrategy = new EntitySetStrategyCached(
        new EntitySetFactory(
            entityComponentBitSetMap
        ),
        entitySetList
    );

    // used as on observable long
    long[] nextEntityId = new long[1];

    Map<Integer, Map<Long, Component>> componentsByTypeIndexMap = ComponentId_EntityIdComponentMap_Map
        .withExpectedSize(256);

    // creates and retains component mappers
    ComponentMapperStrategy componentMapperStrategy = new ComponentMapperStrategyCached(
        componentsByTypeIndexMap,
        new HashMap<>(),
        this.componentRegistry
    );

    // stores references to entity references
    Map<Long, Reference<EntityInternal>> entityReferenceMap = EntityIdReferenceMap
        .withExpectedSize(1024);

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

    EntityReferenceStrategyDefault entityReferenceStrategy = new EntityReferenceStrategyDefault(
        nextEntityId,
        this.componentRegistry,
        entityReferenceMap,
        entityComponentBitSetMap
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
        nextEntityId
    );

    systemFieldInjector.setWorld(world);

    for (Object system : this.systemList) {
      world.registerSystem(system);
    }

    return world;

  }

}
