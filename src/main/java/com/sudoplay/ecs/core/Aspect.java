package com.sudoplay.ecs.core;

import java.util.BitSet;

public interface Aspect {

  boolean matches(BitSet componentBitSet);

}
