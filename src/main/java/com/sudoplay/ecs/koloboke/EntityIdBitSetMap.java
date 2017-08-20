package com.sudoplay.ecs.koloboke;

import com.koloboke.compile.KolobokeMap;

import java.util.BitSet;
import java.util.Map;

@KolobokeMap
public interface EntityIdBitSetMap extends Map<Long, BitSet> {

  static Map<Long, BitSet> withExpectedSize(int expectedSize) {

    return new KolobokeEntityIdBitSetMap(expectedSize);
  }

  BitSet put(long key, BitSet value);

  BitSet get(long key);

  BitSet remove(long key);

}
