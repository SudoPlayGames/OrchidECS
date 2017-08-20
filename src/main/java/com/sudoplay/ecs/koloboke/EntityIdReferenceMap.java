package com.sudoplay.ecs.koloboke;

import com.koloboke.compile.KolobokeMap;
import com.sudoplay.ecs.core.EntityInternal;

import java.lang.ref.Reference;
import java.util.Map;

@KolobokeMap
public interface EntityIdReferenceMap extends Map<Long, Reference<EntityInternal>> {

  static Map<Long, Reference<EntityInternal>> withExpectedSize(int expectedSize) {

    return new KolobokeEntityIdReferenceMap(expectedSize);
  }

  Reference<EntityInternal> put(long key, Reference<EntityInternal> value);

  Reference<EntityInternal> get(long key);

  Reference<EntityInternal> remove(long key);

}
