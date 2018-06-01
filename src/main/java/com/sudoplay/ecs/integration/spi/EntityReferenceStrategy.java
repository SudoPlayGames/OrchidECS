package com.sudoplay.ecs.integration.spi;

import com.sudoplay.ecs.integration.api.Entity;

public interface EntityReferenceStrategy {

  Entity entityCreate();

  Entity entityGet(long entityId);

  void reclaim(Entity entity);
}
