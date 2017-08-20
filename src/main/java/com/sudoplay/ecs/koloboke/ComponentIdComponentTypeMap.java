package com.sudoplay.ecs.koloboke;

import com.koloboke.compile.KolobokeMap;
import com.sudoplay.ecs.core.ComponentType;
import com.sudoplay.ecs.integration.spi.Component;

import java.util.Map;

@KolobokeMap
public interface ComponentIdComponentTypeMap extends Map<Integer, ComponentType> {

  static Map<Integer, ComponentType> withExpectedSize(int expectedSize) {

    return new KolobokeComponentIdComponentTypeMap(expectedSize);
  }

  ComponentType put(
      int key, ComponentType value
  );

  ComponentType get(int key);

  ComponentType remove(int key);

}
