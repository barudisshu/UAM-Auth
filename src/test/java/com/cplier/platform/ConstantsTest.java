package com.cplier.platform;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConstantsTest {

  @Test
  void testConstructor() {
    InvocationTargetException thrown =
        assertThrows(
            InvocationTargetException.class,
            () -> {
              Constructor<Constants> c = Constants.class.getDeclaredConstructor();
              c.setAccessible(true);
              c.newInstance();
            });
    assertNull(thrown.getMessage());
  }
}
