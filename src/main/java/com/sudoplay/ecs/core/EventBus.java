package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.EntitySet;
import com.sudoplay.ecs.integration.api.Subscribe;
import com.sudoplay.ecs.integration.spi.Component;
import com.sudoplay.ecs.integration.spi.ComponentRegistry;
import com.sudoplay.ecs.integration.spi.EntityEventBase;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/* package */ class EventBus {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);

  /* package */ static class Subscriber {

    /* package */ static final Comparator<Subscriber> PRIORITY_SORT = Comparator.comparingInt(
        Subscriber::getPriority)
        .reversed();

    private int priority;
    private Method method;
    private WeakReference<Object> subscriberReference;

    /* package */ Subscriber(
        int priority,
        Method method,
        Object subscriberReference
    ) {

      this.priority = priority;
      this.method = method;
      this.subscriberReference = new WeakReference<>(subscriberReference);
    }

    /* package */ int getPriority() {

      return this.priority;
    }

    /* package */ void invoke(EntityEventBase entityEvent) throws InvocationTargetException, IllegalAccessException {

      Object subscriber = this.subscriberReference.get();

      if (subscriber != null) {
        boolean accessible = this.method.isAccessible();
        this.method.setAccessible(true);
        this.method.invoke(subscriber, entityEvent);
        this.method.setAccessible(accessible);
      }

    }

    /* package */ boolean exists() {

      return this.subscriberReference.get() != null;
    }

    @Override
    public String toString() {

      return "Subscriber{" +
          "priority=" + priority +
          ", method=" + method +
          ", subscriberReference=" + subscriberReference +
          '}';
    }
  }

  /**
   * Stores aspect dispatchers for iteration, grouped by event type.
   */
  private Map<Class<? extends EntityEventBase>, List<AspectDispatch>> aspectDispatchListByEntityEventMap;

  /**
   * Reference to the component registry.
   */
  private ComponentRegistry componentRegistry;

  /**
   * Entity event bus for subscribers without an aspect filter.
   */
  private List<Subscriber> subscriberList;

  /**
   * Stores aspect dispatchers, indexed by their aspects.
   */
  private Map<Aspect, AspectDispatch> aspectDispatchByAspectMap;

  private EntitySetStrategy entitySetStrategy;

  /* package */ EventBus(
      ComponentRegistry componentRegistry,
      EntitySetStrategy entitySetStrategy
  ) {

    this.componentRegistry = componentRegistry;
    this.entitySetStrategy = entitySetStrategy;

    this.aspectDispatchByAspectMap = HashObjObjMaps.getDefaultFactory()
        .newUpdatableMap();

    this.aspectDispatchListByEntityEventMap = HashObjObjMaps.getDefaultFactory()
        .newUpdatableMap();

    this.subscriberList = new ArrayList<>();
  }

  /* package */ void subscribe(Object subscribingObject) {

    // this is the method used to subscribe an external object to entity events
    // using aspects to create filtered entity views

    // annotation processing

    Method[] declaredMethods = subscribingObject.getClass()
        .getDeclaredMethods();

    for (Method method : declaredMethods) {

      if (method.getParameterCount() != 1) {
        continue;
      }

      Subscribe annotation;

      annotation = method.getAnnotation(Subscribe.class);

      if (annotation == null) {
        continue;
      }

      Class<?> c = method.getParameterTypes()[0];

      if (!EntityEventBase.class.isAssignableFrom(c)) {
        continue;
      }

      //noinspection unchecked
      Class<? extends EntityEventBase> eventClass = (Class<? extends EntityEventBase>) c;

      Class<? extends Component>[] all = annotation.all();
      Class<? extends Component>[] one = annotation.one();
      Class<? extends Component>[] exclude = annotation.exclude();
      int priority = annotation.priority();

      Subscriber subscriber = new Subscriber(
          priority,
          method,
          subscribingObject
      );

      if (all.length == 0
          && one.length == 0
          && exclude.length == 0) {

        // subscribe to the 'all entity events' external bus
        this.subscriberList.add(subscriber);
        this.subscriberList.sort(Subscriber.PRIORITY_SORT);

        continue;

      }

      Aspect aspect = new AspectBuilder(this.componentRegistry)
          .requireAll(all)
          .requireOne(one)
          .exclude(exclude)
          .create();

      // does the aspect dispatch already exist?

      AspectDispatch dispatch = this.aspectDispatchByAspectMap.get(aspect);

      if (dispatch == null) {

        // get or create the entity set for the dispatch

        EntitySet entitySet;

        entitySet = this.entitySetStrategy.getEntitySet(aspect);

        dispatch = new AspectDispatch(
            entitySet
        );

        // store the dispatch
        this.aspectDispatchByAspectMap.put(aspect, dispatch);

        List<AspectDispatch> list;

        list = this.aspectDispatchListByEntityEventMap.computeIfAbsent(
            eventClass,
            k -> new ArrayList<>()
        );

        list.add(dispatch);

      }

      dispatch.subscribe(subscriber);

    }

  }

  /* package */ void publish(EntityEventBase event) {

    List<AspectDispatch> aspectDispatchList;

    // publish to all first
    this.publishToAll(event);

    aspectDispatchList = this.aspectDispatchListByEntityEventMap.get(event.getClass());

    if (aspectDispatchList != null
        && !aspectDispatchList.isEmpty()) {

      for (AspectDispatch dispatch : aspectDispatchList) {
        dispatch.publish(event);
      }

    }

  }

  private void publishToAll(EntityEventBase event) {

    for (Iterator<Subscriber> it = this.subscriberList.iterator(); it.hasNext(); ) {

      Subscriber subscriber = it.next();

      if (!subscriber.exists()) {
        // cleanup dead subscribers
        it.remove();
        continue;
      }

      try {
        subscriber.invoke(event);

      } catch (InvocationTargetException | IllegalAccessException e) {
        LOGGER.error(
            "Unable to publish event [{}] to subscriber [{}]",
            event,
            subscriber,
            e
        );
      }
    }
  }

}
