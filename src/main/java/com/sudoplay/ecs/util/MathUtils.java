package com.sudoplay.ecs.util;

import java.util.Random;

public class MathUtils {

  public static final Random RANDOM = new Random();

  public static int random(int range) {

    return RANDOM.nextInt(range + 1);
  }

  public static int nextPowerOfTwo(int value) {

    if (value == 0) {
      return 1;
    }

    value--;
    value |= value >> 1;
    value |= value >> 2;
    value |= value >> 4;
    value |= value >> 8;
    value |= value >> 16;

    return value + 1;
  }
}
