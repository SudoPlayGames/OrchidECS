package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.Entity;
import com.sudoplay.ecs.integration.api.EntitySet;
import com.sudoplay.ecs.util.LongMap;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;

public class EntitySetInternal
    implements EntitySet {

  /* package */ enum EventType {
    ADD, CHANGE, REMOVE
  }

  private LongMap<PooledBitSet> entityComponentBitSetMap;
  private Aspect aspect;
  private LongMap<Entity> entityMap;
  private List<Reference<Deque<Entity>>> eventHandlerAddList;
  private List<Reference<Deque<Entity>>> eventHandlerRemoveList;
  private List<Reference<Deque<Entity>>> toRemove;
  private EntityIterator entityIterator;

  /* package */ EntitySetInternal(
      LongMap<PooledBitSet> entityComponentBitSetMap,
      Aspect aspect
  ) {

    this.entityComponentBitSetMap = entityComponentBitSetMap;
    this.aspect = aspect;

    this.entityMap = new LongMap<Entity>(entityComponentBitSetMap.size());

    this.eventHandlerAddList = new ArrayList<Reference<Deque<Entity>>>();
    this.eventHandlerRemoveList = new ArrayList<Reference<Deque<Entity>>>();
    this.toRemove = new ArrayList<Reference<Deque<Entity>>>();
  }

  /* package */ void onSystemEvent(
      EntityInternal entity,
      EventType eventType
  ) {

    long entityId;
    BitSet componentBitSet;
    boolean interested;
    boolean contains;

    entityId = entity.getId();
    contains = this.entityMap.containsKey(entityId);

    if (eventType == EventType.ADD
        || eventType == EventType.CHANGE) {

      componentBitSet = this.entityComponentBitSetMap.get(entityId).getBitSet();
      interested = this.aspect.matches(componentBitSet);

      if (interested && !contains) {

        // we like this entity and we don't have it - we covets
        // this covers the case where an entity is added and
        // this view is interested in it

        this.entityMap.put(entityId, entity);

        // notify our subscribers and do some reference bookkeeping

        this.observerNotify(entity, this.eventHandlerAddList);

      } else if (!interested && contains) {

        // we don't care about this entity anymore - discard it
        // this covers the case where an entity has changed and
        // this view is no longer interested in the entity

        this.entityMap.remove(entityId);

        // notify our subscribers and do some reference bookkeeping

        this.observerNotify(entity, this.eventHandlerRemoveList);

      } else if (contains) {

        // this covers the case where this view is interested in
        // the entity, but it already contains the entity

        // currently do nothing
      }

      // if this view is not interested in and doesn't contain
      // the entity, don't do anything

    } else if (eventType == EventType.REMOVE) {

      if (contains) {

        // this view has the entity being removed and needs to
        // remove it

        this.entityMap.remove(entityId);

        // notify our subscribers and do some reference bookkeeping

        this.observerNotify(entity, this.eventHandlerRemoveList);

      }

    }

  }

  @Override
  public boolean contains(Entity entity) {

    return this.entityMap.containsKey(((EntityInternal) entity).getId());
  }

  @Override
  public Iterable<Entity> entitiesGet() {

    if (this.entityIterator == null) {
      this.entityIterator = new EntityIterator();
    }

    return this.entityIterator.setValues(this.entityMap.values());
  }

  @Override
  public ReusableIterator<Entity> entityIteratorCreate() {

    return new EntityIterator().setValues(new LongMap.Values<Entity>(this.entityMap));
  }

  @Override
  public Deque<Entity> newDequeEventEntityAdd() {

    Deque<Entity> observer = new ArrayDeque<Entity>();
    this.eventHandlerAddList.add(new WeakReference<Deque<Entity>>(observer));

    return observer;
  }

  @Override
  public Deque<Entity> newDequeEventEntityRemove() {

    Deque<Entity> observer = new ArrayDeque<Entity>();
    this.eventHandlerRemoveList.add(new WeakReference<Deque<Entity>>(observer));

    return observer;
  }

  private void observerNotify(
      EntityInternal entity,
      List<Reference<Deque<Entity>>> list
  ) {

    for (int i = 0; i < list.size(); i++) {
      Reference<Deque<Entity>> ref = list.get(i);
      Deque<Entity> eventHandler = ref.get();

      if (eventHandler == null) {
        this.toRemove.add(ref);
        ref.clear();
        continue;
      }

      eventHandler.offer(entity);
    }

    if (!this.toRemove.isEmpty()) {
      list.removeAll(this.toRemove);
      this.toRemove.clear();
    }
  }

  private static class EntityIterator
      implements ReusableIterator<Entity> {

    private LongMap.Values<Entity> values;

    /* package */ EntityIterator setValues(LongMap.Values<Entity> values) {

      this.values = values;
      return this;
    }

    @Override
    public boolean hasNext() {

      return this.values.hasNext();
    }

    @Override
    public Entity next() {

      return this.values.next();
    }

    @Override
    public void remove() {

      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Entity> iterator() {

      return this;
    }

    @Override
    public void reset() {

      this.values.reset();
    }
  }

}
