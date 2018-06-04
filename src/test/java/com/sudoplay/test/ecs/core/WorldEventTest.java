package com.sudoplay.test.ecs.core;

import com.sudoplay.ecs.core.World;
import com.sudoplay.ecs.core.WorldBuilder;
import com.sudoplay.ecs.integration.api.*;
import com.sudoplay.test.ecs.LifeComponent;
import org.junit.Assert;
import org.junit.Test;

public class WorldEventTest {

  // --------------------------------------------------------------------------
  // - Event Add Pre
  // --------------------------------------------------------------------------

  @Test
  public void testAddPreEvent() {

    WorldEventEntityAddPreEventTestSystem system = new WorldEventEntityAddPreEventTestSystem();

    World world = new WorldBuilder()
        .registerSystems(system)
        .registerComponent(LifeComponent.class)
        .create();

    Entity entity = world.entityCreate();
    LifeComponent component = world.componentCreate(LifeComponent.class);
    component.life = 42;
    entity.componentAdd(component);
    entity.worldAdd();

    world.update();

    system.checkAssertions(entity);
  }

  public static class WorldEventEntityAddPreEventTestSystem {

    @InjectComponentMapper(LifeComponent.class)
    private ComponentMapper<LifeComponent> componentMapper;

    @InjectEntitySet(all = LifeComponent.class)
    private EntitySet entitySet;

    private boolean eventReceived;

    @Subscribe
    public void onEvent(WorldEvent.EntityAddPreEvent event) {

      // Assert that the component mapper has the entity's component at this time.
      Assert.assertTrue(this.componentMapper.has(event.getEntity()));

      // Assert that the entity's life component is set to 42.
      Assert.assertEquals(42, this.componentMapper.get(event.getEntity()).life);

      // Assert that the entity set does not contain this entity yet.
      Assert.assertFalse(this.entitySet.contains(event.getEntity()));

      event.cancel();

      this.eventReceived = true;
    }

    /* package */ void checkAssertions(Entity entity) {

      // Assert that the event was received.
      Assert.assertTrue(this.eventReceived);

      // Assert that the entity has been removed from the component mapper.
      Assert.assertFalse(this.componentMapper.has(entity));

      // Assert that the entity has been removed from the entity set.
      Assert.assertFalse(this.entitySet.contains(entity));
    }

  }

  // --------------------------------------------------------------------------
  // - Event Add Post
  // --------------------------------------------------------------------------

  @Test
  public void testAddPostEvent() {

    WorldEventEntityAddPostEventTestSystem system = new WorldEventEntityAddPostEventTestSystem();

    World world = new WorldBuilder()
        .registerSystems(system)
        .registerComponent(LifeComponent.class)
        .create();

    Entity entity = world.entityCreate();
    LifeComponent component = world.componentCreate(LifeComponent.class);
    component.life = 42;
    entity.componentAdd(component);
    entity.worldAdd();

    world.update();

    system.checkAssertions();
  }

  public static class WorldEventEntityAddPostEventTestSystem {

    @InjectComponentMapper(LifeComponent.class)
    private ComponentMapper<LifeComponent> componentMapper;

    @InjectEntitySet(all = LifeComponent.class)
    private EntitySet entitySet;

    private boolean eventReceived;

    @Subscribe
    public void onEvent(WorldEvent.EntityAddPostEvent event) {

      // Assert that the component mapper has the entity's component at this time.
      Assert.assertTrue(this.componentMapper.has(event.getEntity()));

      // Assert that the entity's life component is set to 42.
      Assert.assertEquals(42, this.componentMapper.get(event.getEntity()).life);

      // Assert that the entity set does contain this entity at this time.
      Assert.assertTrue(this.entitySet.contains(event.getEntity()));

      this.eventReceived = true;
    }

    /* package */ void checkAssertions() {

      // Assert that the event was received.
      Assert.assertTrue(this.eventReceived);
    }

  }

  // --------------------------------------------------------------------------
  // - Event Remove Pre
  // --------------------------------------------------------------------------

  @Test
  public void testRemovePreEvent() {

    WorldEventEntityRemovePreEventTestSystem system = new WorldEventEntityRemovePreEventTestSystem();

    World world = new WorldBuilder()
        .registerSystems(system)
        .registerComponent(LifeComponent.class)
        .create();

    Entity entity = world.entityCreate();
    LifeComponent component = world.componentCreate(LifeComponent.class);
    component.life = 42;
    entity.componentAdd(component);
    entity.worldAdd();

    world.update();

    entity.worldRemove();

    world.update();

    system.checkAssertions(entity);
  }

  public static class WorldEventEntityRemovePreEventTestSystem {

    @InjectComponentMapper(LifeComponent.class)
    private ComponentMapper<LifeComponent> componentMapper;

    @InjectEntitySet(all = LifeComponent.class)
    private EntitySet entitySet;

    private boolean eventReceived;

    @Subscribe
    public void onEvent(WorldEvent.EntityRemovePreEvent event) {

      // Assert that the component mapper has the entity's component at this time.
      Assert.assertTrue(this.componentMapper.has(event.getEntity()));

      // Assert that the entity's life component is set to 42.
      Assert.assertEquals(42, this.componentMapper.get(event.getEntity()).life);

      // Assert that the entity set does contain this entity at this time.
      Assert.assertTrue(this.entitySet.contains(event.getEntity()));

      event.cancel();

      this.eventReceived = true;
    }

    /* package */ void checkAssertions(Entity entity) {

      // Assert that the event was received.
      Assert.assertTrue(this.eventReceived);

      // Assert that the entity has not been removed from the component mapper.
      Assert.assertTrue(this.componentMapper.has(entity));

      // Assert that the entity has not been removed from the entity set.
      Assert.assertTrue(this.entitySet.contains(entity));
    }

  }

  // --------------------------------------------------------------------------
  // - Event Remove Post
  // --------------------------------------------------------------------------

  @Test
  public void testRemovePostEvent() {

    WorldEventEntityRemovePostEventTestSystem system = new WorldEventEntityRemovePostEventTestSystem();

    World world = new WorldBuilder()
        .registerSystems(system)
        .registerComponent(LifeComponent.class)
        .create();

    Entity entity = world.entityCreate();
    LifeComponent component = world.componentCreate(LifeComponent.class);
    component.life = 42;
    entity.componentAdd(component);
    entity.worldAdd();

    world.update();

    entity.worldRemove();

    world.update();

    system.checkAssertions();
  }

  public static class WorldEventEntityRemovePostEventTestSystem {

    @InjectComponentMapper(LifeComponent.class)
    private ComponentMapper<LifeComponent> componentMapper;

    @InjectEntitySet(all = LifeComponent.class)
    private EntitySet entitySet;

    private boolean eventReceived;

    @Subscribe
    public void onEvent(WorldEvent.EntityRemovePostEvent event) {

      // Assert that the component mapper does not have the entity's component at this time.
      Assert.assertFalse(this.componentMapper.has(event.getEntity()));

      // Assert that the entity set does not contain this entity at this time.
      Assert.assertFalse(this.entitySet.contains(event.getEntity()));

      this.eventReceived = true;
    }

    /* package */ void checkAssertions() {

      // Assert that the event was received.
      Assert.assertTrue(this.eventReceived);
    }

  }

  // --------------------------------------------------------------------------
  // - Event Changed
  // --------------------------------------------------------------------------

  @Test
  public void testChangedEvent() {

    WorldEventEntityChangedEventTestSystem system = new WorldEventEntityChangedEventTestSystem();

    World world = new WorldBuilder()
        .registerSystems(system)
        .registerComponent(LifeComponent.class)
        .create();

    Entity entity = world.entityCreate();
    LifeComponent component = world.componentCreate(LifeComponent.class);
    component.life = 42;
    entity.componentAdd(component);
    entity.worldAdd();

    world.update();

    entity.componentRemove(LifeComponent.class);

    world.update();

    entity.worldRemove();

    world.update();

    system.checkAssertions();
  }

  public static class WorldEventEntityChangedEventTestSystem {

    @InjectComponentMapper(LifeComponent.class)
    private ComponentMapper<LifeComponent> componentMapper;

    @InjectEntitySet(all = LifeComponent.class)
    private EntitySet entitySet;

    private int eventsReceived;

    @Subscribe
    public void onEvent(WorldEvent.EntityChangedEvent event) {

      this.eventsReceived += 1;
    }

    /* package */ void checkAssertions() {

      // Assert that the event was received once for the component
      // addition and once for the component removal.
      Assert.assertEquals(2, this.eventsReceived);
    }

  }

}
