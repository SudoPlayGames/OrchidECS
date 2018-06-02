package com.sudoplay.test.ecs;

import com.sudoplay.ecs.core.World;
import com.sudoplay.ecs.core.WorldBuilder;
import com.sudoplay.ecs.integration.api.*;
import com.sudoplay.ecs.util.MathUtils;

public class GarbageTest {

  public static void main(String[] args) throws InterruptedException {

    SystemA system = new SystemA();

    World world = new WorldBuilder()
        .registerSystems(system)
        .registerComponent(LifeComponent.class)
        .create();

    Entity entity = world.entityCreate();
    LifeComponent component = world.componentCreate(LifeComponent.class);
    component.life = MathUtils.random(9) + 1;
    entity.componentAdd(component);
    entity.worldAdd();

    while (true) {
      world.update();
      system.update();

      Thread.sleep(1);
    }
  }

  public static class SystemA {

    @InjectWorld
    private World world;

    @InjectComponentMapper(LifeComponent.class)
    private ComponentMapper<LifeComponent> lifeComponentMapper;

    @InjectEntitySet(all = LifeComponent.class)
    private EntitySet entitySet;

    public void update() {

      for (Entity entity : this.entitySet.entitiesGet()) {
        LifeComponent lifeComponent = this.lifeComponentMapper.get(entity);
        lifeComponent.life -= 1;

        if (lifeComponent.life <= 0) {
          entity.worldRemove();

          Entity newEntity = this.world.entityCreate();
          LifeComponent component = this.world.componentCreate(LifeComponent.class);
          component.life = MathUtils.random(9) + 1;
          newEntity.componentAdd(component);
          newEntity.worldAdd();
        }
      }

    }
  }

}
