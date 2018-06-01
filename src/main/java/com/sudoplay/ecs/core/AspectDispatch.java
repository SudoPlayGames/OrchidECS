package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.EntitySet;
import com.sudoplay.ecs.integration.spi.EntityEventBase;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* package */ class AspectDispatch {

  private EntitySet entitySet;
  private List<EventBus.Subscriber> subscriberList;

  /* package */ AspectDispatch(
      EntitySet entitySet
  ) {

    this.entitySet = entitySet;
    this.subscriberList = new ArrayList<EventBus.Subscriber>();
  }

  /* package */ void subscribe(
      EventBus.Subscriber subscriber
  ) {

    this.subscriberList.add(subscriber);
    this.sortSubscriberList();
  }

  private void sortSubscriberList() {

    Collections.sort(this.subscriberList, EventBus.Subscriber.PRIORITY_SORT);
  }

  /* package */ void publish(
      EntityEventBase event
  ) {

    EntityInternal entity = (EntityInternal) event.getEntity();

    for (Iterator<EventBus.Subscriber> it = this.subscriberList.iterator(); it.hasNext(); ) {

      EventBus.Subscriber subscriber = it.next();

      if (!subscriber.exists()) {
        it.remove();
        continue;
      }

      if (this.entitySet.contains(entity)) {

        try {
          subscriber.invoke(event);

          if (event.isCancelled()) {
            break;
          }

        } catch (InvocationTargetException e) {
          throw new RuntimeException(String.format("Unable to invoke subscriber [%s]", subscriber), e);

        } catch (IllegalAccessException e) {
          throw new RuntimeException(String.format("Unable to invoke subscriber [%s]", subscriber), e);
        }

      }

    }

  }

}
