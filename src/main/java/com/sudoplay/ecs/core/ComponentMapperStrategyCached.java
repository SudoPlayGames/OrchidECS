package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.ComponentMapper;
import com.sudoplay.ecs.integration.spi.Component;
import com.sudoplay.ecs.integration.spi.ComponentMapperStrategy;
import com.sudoplay.ecs.integration.spi.ComponentRegistry;
import com.sudoplay.ecs.util.IntMap;
import com.sudoplay.ecs.util.LongMap;

import java.util.Map;

public class ComponentMapperStrategyCached
    implements
    ComponentMapperStrategy {

  private IntMap<LongMap<Component>> componentsByTypeIndexMap;
  private Map<Class<? extends Component>, ComponentMapper<? extends Component>> componentMapperMap;
  private ComponentRegistry componentRegistry;

  /* package */ ComponentMapperStrategyCached(
      IntMap<LongMap<Component>> componentsByTypeIndexMap,
      Map<Class<? extends Component>, ComponentMapper<? extends Component>> componentMapperMap,
      ComponentRegistry componentRegistry
  ) {

    this.componentsByTypeIndexMap = componentsByTypeIndexMap;
    this.componentMapperMap = componentMapperMap;
    this.componentRegistry = componentRegistry;
  }

  /**
   * @param componentClass the component class
   * @param <C>            type
   * @return a component mapper for the given component class
   */
  @Override
  public <C extends Component> ComponentMapper<C> getComponentMapper(
      Class<C> componentClass
  ) {

    ComponentMapper<C> componentMapper;
    int componentIndex;
    LongMap<Component> entityIdComponentMap;

    componentIndex = this.componentRegistry.componentTypeIndexGet(componentClass);

    entityIdComponentMap = this.componentsByTypeIndexMap.get(componentIndex);

    if (entityIdComponentMap == null) {
      entityIdComponentMap = new LongMap<Component>(this.componentRegistry.componentCountGet());
      this.componentsByTypeIndexMap.put(componentIndex, entityIdComponentMap);
    }

    //noinspection unchecked
    componentMapper = (ComponentMapper<C>) this.componentMapperMap.get(componentClass);

    if (componentMapper == null) {
      //noinspection unchecked
      componentMapper = new ComponentMapperDefault<C>(componentClass, (LongMap<C>) entityIdComponentMap);
      this.componentMapperMap.put(componentClass, componentMapper);
    }

    //noinspection unchecked
    return componentMapper;
  }

}
