package com.sudoplay.ecs.koloboke;

import com.koloboke.compile.KolobokeMap;
import com.sudoplay.ecs.integration.spi.Component;

import java.util.Map;

@KolobokeMap
public interface ComponentId_EntityIdComponentMap_Map extends Map<Integer, Map<Long, Component>> {

  static Map<Integer, Map<Long, Component>> withExpectedSize(int expectedSize) {

    return new KolobokeComponentId_EntityIdComponentMap_Map(expectedSize);
  }

  Map<Long, Component> put(
      int key, Map<Long, Component> value
  );

  Map<Long, Component> get(int key);

  Map<Long, Component> remove(int key);

}
