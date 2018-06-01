package com.sudoplay.ecs.core;

import com.sudoplay.ecs.integration.api.Poolable;

import java.util.BitSet;

public class PooledBitSet
    implements Poolable {

  private BitSet bitSet;

  public PooledBitSet(BitSet bitSet) {

    this.bitSet = bitSet;
  }

  public BitSet getBitSet() {

    return this.bitSet;
  }

  @Override
  public void reset() {

    this.bitSet.clear();
  }
}
