package com.sudoplay.ecs.core;

import java.util.BitSet;

public class AspectDefault implements
    Aspect {

  private BitSet allSet;
  private BitSet excludeSet;
  private BitSet oneSet;

  /* package */ AspectDefault(
      BitSet allSet,
      BitSet excludeSet,
      BitSet oneSet
  ) {

    this.allSet = (BitSet) allSet.clone();
    this.excludeSet = (BitSet) excludeSet.clone();
    this.oneSet = (BitSet) oneSet.clone();
  }

  @Override
  public boolean matches(BitSet componentBitSet) {

    if (!this.allSet.isEmpty()) {

      for (int i = this.allSet.nextSetBit(0); i >= 0; i = this.allSet.nextSetBit(
          i + 1)) {

        if (!componentBitSet.get(i)) {
          return false;
        }

      }

    }

    //noinspection SimplifiableIfStatement
    if (!this.excludeSet.isEmpty()
        && this.excludeSet.intersects(componentBitSet)) {
      return false;
    }

    return this.oneSet.isEmpty() || this.oneSet.intersects(componentBitSet);

  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AspectDefault aspect = (AspectDefault) o;

    return this.allSet.equals(aspect.allSet)
        && excludeSet.equals(aspect.excludeSet)
        && oneSet.equals(aspect.oneSet);
  }

  @Override
  public int hashCode() {

    int result = this.allSet.hashCode();
    result = 31 * result + this.excludeSet.hashCode();
    result = 31 * result + this.oneSet.hashCode();
    return result;
  }

}
