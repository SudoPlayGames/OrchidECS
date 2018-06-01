package com.sudoplay.test.ecs;

import com.sudoplay.ecs.core.World;
import com.sudoplay.ecs.core.WorldBuilder;
import com.sudoplay.ecs.integration.api.Entity;
import com.sudoplay.ecs.integration.api.EntitySet;
import com.sudoplay.ecs.integration.api.InjectEntitySet;
import com.sudoplay.ecs.integration.spi.Component;
import com.sudoplay.ecs.util.MathUtils;

public class Main {

  public static void main(String[] args) throws InterruptedException {

    World world = new WorldBuilder()
        .registerComponent(ComponentA.class)
        .create();

    SystemA system = new SystemA(world);
    world.registerSystem(system);

    while (true) {
      world.update();
      system.update();

      Thread.sleep(1);
    }
  }

  public static class SystemA {

    private World world;

    @InjectEntitySet(all = ComponentA.class)
    private EntitySet entitySet;

    private Entity lastEntity;

    public SystemA(World world) {

      this.world = world;
    }

    public void update() {

      if (this.lastEntity != null) {
        this.lastEntity.worldRemove();
      }

      Entity entity = this.world.entityCreate();
      ComponentA componentA = this.world.componentCreate(ComponentA.class);
      componentA.x = MathUtils.random(100);
      componentA.y = MathUtils.random(100);
      entity.componentAdd(componentA);
      entity.worldAdd();

      this.lastEntity = entity;
    }
  }

  public static class ComponentA
      implements Component {

    public int x;
    public int y;

    @Override
    public void reset() {

      this.x = 0;
      this.y = 0;
    }
  }

}
