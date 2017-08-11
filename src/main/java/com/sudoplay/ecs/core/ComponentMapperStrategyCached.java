package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.ComponentMapper;
import com.sudoplay.ecs.integration.spi.Component;
import com.sudoplay.ecs.integration.spi.ComponentMapperStrategy;
import com.sudoplay.ecs.integration.spi.ComponentRegistry;
import net.openhft.koloboke.collect.map.hash.HashLongObjMaps;
import net.openhft.koloboke.collect.map.hash.HashObjObjMap;

import java.util.Map;

public class ComponentMapperStrategyCached implements
    ComponentMapperStrategy {

  private Map<Integer, Map<Long, Component>> componentsByTypeIndexMap;
  private Map<Class<? extends Component>, ComponentMapper<? extends Component>> componentMapperMap;
  private ComponentRegistry componentRegistry;

  /* package */ ComponentMapperStrategyCached(
      Map<Integer, Map<Long, Component>> componentsByTypeIndexMap,
      HashObjObjMap<Class<? extends Component>, ComponentMapper<? extends Component>> componentMapperMap,
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

    ComponentMapper<? extends Component> componentMapper;
    int componentIndex;
    Map<Long, Component> entityIdComponentMap;

    componentIndex = this.componentRegistry.componentTypeIndexGet(componentClass);

    entityIdComponentMap = this.componentsByTypeIndexMap.computeIfAbsent(
        componentIndex,
        k -> HashLongObjMaps.getDefaultFactory()
            .newUpdatableMap()
    );

    componentMapper = this.componentMapperMap.computeIfAbsent(
        componentClass,
        k -> new ComponentMapperDefault<>(componentClass, entityIdComponentMap)
    );

    //noinspection unchecked
    return (ComponentMapper<C>) componentMapper;
  }

}
