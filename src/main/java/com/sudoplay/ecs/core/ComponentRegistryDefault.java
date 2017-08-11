package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.ComponentRegistrationException;
import com.sudoplay.ecs.integration.spi.Component;
import com.sudoplay.ecs.integration.spi.ComponentRegistry;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ComponentRegistryDefault implements
    ComponentRegistry {

  private int nextAvailableId;
  private Map<Class<? extends Component>, ComponentType> classComponentTypeMap;
  private Map<Integer, ComponentType> indexComponentTypeMap;

  public ComponentRegistryDefault() {

    this.nextAvailableId = 0;
    this.classComponentTypeMap = HashObjObjMaps.getDefaultFactory()
        .newUpdatableMap();
    this.indexComponentTypeMap = HashIntObjMaps.getDefaultFactory()
        .newUpdatableMap();
  }

  @Override
  public Collection<ComponentType> componentTypeCollectionGet(Collection<ComponentType> result) {

    result.addAll(this.classComponentTypeMap.values());
    return result;
  }

  @Override
  public void componentRegister(Class<? extends Component> componentClass) {

    ComponentType type = this.classComponentTypeMap.get(componentClass);

    if (type != null) {
      throw new ComponentRegistrationException(
          "Duplicate component registration: " + componentClass.getName()
      );
    }

    ComponentType value = new ComponentType(
        this.nextAvailableId,
        componentClass
    );

    this.classComponentTypeMap.put(
        componentClass,
        value
    );

    this.indexComponentTypeMap.put(
        this.nextAvailableId,
        value
    );

    this.nextAvailableId += 1;

  }

  @Override
  public ComponentType componentTypeGet(Class<? extends Component> componentClass) {

    ComponentType type = this.classComponentTypeMap.get(componentClass);

    if (type == null) {
      throw new IllegalStateException("Unregistered component: " + componentClass
          .getName());
    }

    return type;
  }

  @Override
  public ComponentType componentTypeGet(int componentTypeIndex) {

    ComponentType type = this.indexComponentTypeMap.get(componentTypeIndex);

    if (type == null) {
      throw new IllegalStateException("Unregistered component index: " + componentTypeIndex);
    }

    return type;
  }

  @Override
  public int componentTypeIndexGet(Class<? extends Component> componentClass) {

    return this.componentTypeGet(componentClass)
        .getIndex();
  }

  @Override
  public int componentCountGet() {

    return this.nextAvailableId;
  }
}
