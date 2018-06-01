package com.sudoplay.test.ecs;

import com.sudoplay.ecs.integration.spi.Component;

public class LifeComponent
    implements Component {

  public int life;

  @Override
  public void reset() {

    this.life = 0;
  }
}
