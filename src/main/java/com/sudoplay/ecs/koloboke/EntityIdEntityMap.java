package com.sudoplay.ecs.koloboke;

import com.koloboke.compile.KolobokeMap;
import com.sudoplay.ecs.integration.api.Entity;

import java.util.Map;

@KolobokeMap
public interface EntityIdEntityMap extends Map<Long, Entity> {

  static Map<Long, Entity> withExpectedSize(int expectedSize) {

    return new KolobokeEntityIdEntityMap(expectedSize);
  }

  Entity put(long key, Entity value);

  Entity get(long key);

  Entity remove(long key);

  boolean containsKey(long key);

}
