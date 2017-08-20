package com.sudoplay.ecs.koloboke;

import com.koloboke.compile.KolobokeMap;
import com.sudoplay.ecs.integration.spi.Component;

import java.util.Map;

@KolobokeMap
public interface EntityIdComponentMap extends Map<Long, Component> {

  static Map<Long, Component> withExpectedSize(int expectedSize) {

    return new KolobokeEntityIdComponentMap(expectedSize);
  }

  Component put(long key, Component value);

  Component get(long key);

  Component remove(long key);

}
