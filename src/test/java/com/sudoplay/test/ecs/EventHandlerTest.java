package com.sudoplay.test.ecs;

import com.sudoplay.ecs.core.World;
import com.sudoplay.ecs.core.WorldBuilder;
import com.sudoplay.ecs.integration.api.ComponentMapper;
import com.sudoplay.ecs.integration.api.Entity;
import com.sudoplay.ecs.integration.api.InjectComponentMapper;
import com.sudoplay.ecs.integration.api.Subscribe;
import com.sudoplay.ecs.integration.spi.Component;
import com.sudoplay.ecs.integration.spi.EntityEventBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EventHandlerTest {

  // --------------------------------------------------------------------------
  // -- Event Handler Priority

  @Test
  public void testSystemEventHandlerPriority() {

    SystemEventHandlerPriorityTest system = new SystemEventHandlerPriorityTest();

    World world = new WorldBuilder()
        .registerSystems(system)
        .create();

    Entity entity = world.entityCreate();
    entity.worldAdd();

    world.update();

    world.publish(new PriorityTestEvent(entity));

    Assert.assertEquals("Priority 1", system.resultList.get(0));
    Assert.assertEquals("Priority 0", system.resultList.get(1));
    Assert.assertEquals("Priority -1", system.resultList.get(2));

  }

  public static class SystemEventHandlerPriorityTest {

    List<String> resultList;

    SystemEventHandlerPriorityTest() {

      this.resultList = new ArrayList<String>();
    }

    @Subscribe
    public void onTestEvent(PriorityTestEvent event) {

      this.resultList.add("Priority 0");
    }

    @Subscribe(priority = -1)
    public void onTestEventLow(PriorityTestEvent event) {

      this.resultList.add("Priority -1");
    }

    @Subscribe(priority = 1)
    public void onTestEventHigh(PriorityTestEvent event) {

      this.resultList.add("Priority 1");
    }

  }

  static class PriorityTestEvent
      extends
      EntityEventBase {

    PriorityTestEvent(Entity entity) {

      super(entity);
    }

  }

  // --------------------------------------------------------------------------
  // -- Event Handler Component Filter

  @Test
  public void testEventHandlerComponentFilter() {

    SystemEventHandlerComponentFilterTest system = new SystemEventHandlerComponentFilterTest();

    World world = new WorldBuilder()
        .registerSystems(system)
        .registerComponent(ComponentA.class)
        .registerComponent(ComponentB.class)
        .create();

    Entity entityA;
    {
      ComponentA componentA;

      entityA = world.entityCreate();
      componentA = world.componentCreate(ComponentA.class);
      componentA.name = "A";
      entityA.componentAdd(componentA);
      entityA.worldAdd();
    }

    Entity entityB;
    {
      ComponentB componentB;

      entityB = world.entityCreate();
      componentB = world.componentCreate(ComponentB.class);
      componentB.name = "B";
      entityB.componentAdd(componentB);
      entityB.worldAdd();
    }

    Entity entityAB;
    {
      ComponentA componentA;
      ComponentB componentB;

      entityAB = world.entityCreate();
      componentA = world.componentCreate(ComponentA.class);
      componentA.name = "AB";
      entityAB.componentAdd(componentA);
      componentB = world.componentCreate(ComponentB.class);
      componentB.name = "AB";
      entityAB.componentAdd(componentB);
      entityAB.worldAdd();
    }

    world.update();

    world.publish(new ComponentFilterTestEvent(entityA));
    world.publish(new ComponentFilterTestEvent(entityB));
    world.publish(new ComponentFilterTestEvent(entityAB));

  }

  static class SystemEventHandlerComponentFilterTest {

    @InjectComponentMapper(ComponentA.class)
    ComponentMapper<ComponentA> componentAMapper;

    @InjectComponentMapper(ComponentB.class)
    ComponentMapper<ComponentB> componentBMapper;

    @Subscribe(all = ComponentA.class)
    public void onTestEventA(ComponentFilterTestEvent event) {

      String name = this.componentAMapper.get(event.getEntity()).name;
      Assert.assertTrue("A".equals(name) || "AB".equals(name));
    }

    @Subscribe(all = ComponentB.class)
    public void onTestEventB(ComponentFilterTestEvent event) {

      String name = this.componentBMapper.get(event.getEntity()).name;
      Assert.assertTrue("B".equals(name) || "AB".equals(name));
    }

    @Subscribe(all = ComponentA.class, exclude = ComponentB.class)
    public void onTestEventOnlyA(ComponentFilterTestEvent event) {

      String name = this.componentAMapper.get(event.getEntity()).name;
      Assert.assertTrue("A".equals(name));
    }

  }

  static class ComponentFilterTestEvent
      extends
      EntityEventBase {

    ComponentFilterTestEvent(Entity entity) {

      super(entity);
    }

  }

  static class ComponentA
      implements Component {

    String name;

    @Override
    public void reset() {

      this.name = null;
    }
  }

  static class ComponentB
      implements Component {

    String name;

    public ComponentB(String name) {

      this.name = name;
    }

    @Override
    public void reset() {

      this.name = null;
    }
  }

}
