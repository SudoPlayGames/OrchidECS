package com.sudoplay.ecs.koloboke;

import com.koloboke.compile.CustomKeyEquivalence;
import com.koloboke.compile.KolobokeMap;

import java.util.Map;

@KolobokeMap
@CustomKeyEquivalence
public abstract class ClassObjectMap<C, O> implements Map<Class<? extends C>, O> {

  public static <C, O> Map<Class<? extends C>, O> withExpectedSize(int expectedSize) {

    return new KolobokeClassObjectMap(expectedSize);
  }

  public final boolean keyEquals(Class<? extends C> aClass, Class<? extends C> bClass) {

    return aClass == bClass;
  }

  public final int keyHashCode(Class<? extends C> aClass) {

    return aClass.hashCode();
  }

}
