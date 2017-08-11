package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.spi.Component;
import com.sudoplay.ecs.integration.spi.ComponentRegistry;

import java.util.BitSet;

/* package */ class AspectBuilder {

  private BitSet allSet;
  private BitSet excludeSet;
  private BitSet oneSet;

  private ComponentRegistry componentRegistry;

  /* package */ AspectBuilder(
      ComponentRegistry componentRegistry
  ) {

    this.allSet = new BitSet();
    this.excludeSet = new BitSet();
    this.oneSet = new BitSet();

    this.componentRegistry = componentRegistry;
  }

  /**
   * Returns an aspect where an entity must possess all of the specified
   * component types.
   *
   * @param types a required component type
   * @return an aspect that can be matched against entities
   */
  /* package */ AspectBuilder requireAll(Class<? extends Component>[] types) {

    for (Class<? extends Component> i : types) {
      this.allSet.set(this.componentRegistry.componentTypeIndexGet(i));
    }

    return this;
  }

  /**
   * Returns an aspect where an entity must possess one of the specified
   * component types.
   *
   * @param types one of the types the entity must possess
   * @return an aspect that can be matched against entities
   */
  /* package */ AspectBuilder requireOne(
      Class<? extends Component>[] types
  ) {

    for (Class<? extends Component> i : types) {
      this.oneSet.set(this.componentRegistry.componentTypeIndexGet(i));
    }

    return this;
  }

  /**
   * Excludes all of the specified component types from the aspect. A system
   * will not be interested in an entity that possesses one of the specified
   * exclusion component types.
   *
   * @param types component type to exclude
   * @return an aspect that can be matched against entities
   */
  /* package */ AspectBuilder exclude(
      Class<? extends Component>[] types
  ) {

    for (Class<? extends Component> i : types) {
      this.excludeSet.set(this.componentRegistry.componentTypeIndexGet(i));
    }

    return this;
  }

  /* package */ Aspect create() {

    return new AspectDefault(
        (BitSet) this.allSet.clone(),
        (BitSet) this.excludeSet.clone(),
        (BitSet) this.oneSet.clone()
    );
  }

}
