package com.sudoplay.ecs.integration.spi;

import com.sudoplay.ecs.integration.api.Entity;

public interface EntitySetEventHandler {

  void onEventEntityAdded(Entity entity);

  void onEventEntityRemoved(Entity entity);

}
