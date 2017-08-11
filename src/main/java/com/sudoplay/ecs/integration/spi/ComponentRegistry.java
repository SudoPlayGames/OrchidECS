package com.sudoplay.ecs.integration.spi;

import com.sudoplay.ecs.core.ComponentType;

import java.util.Collection;

public interface ComponentRegistry {

  /**
   * @return the given result with the registry's component type collection added
   */
  Collection<ComponentType> componentTypeCollectionGet(Collection<ComponentType> result);

  void componentRegister(Class<? extends Component> componentClass);

  ComponentType componentTypeGet(Class<? extends Component> componentClass);

  ComponentType componentTypeGet(int componentTypeIndex);

  int componentTypeIndexGet(Class<? extends Component> componentClass);

  int componentCountGet();

}
