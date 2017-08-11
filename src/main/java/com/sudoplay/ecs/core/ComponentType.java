package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.spi.Component;

public class ComponentType {

  private final int index;
  private final Class<? extends Component> type;

  /* package */ ComponentType(int index, Class<? extends Component> type) {

    this.index = index;
    this.type = type;
  }

  public int getIndex() {

    return this.index;
  }

  public Class<? extends Component> getType() {

    return this.type;
  }

  @Override
  public String toString() {

    return "ComponentType{" +
        "index=" + index +
        ", type=" + type +
        '}';
  }

}
