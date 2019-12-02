package com.cplier.platform.testhelper;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/** 单元测试 */
public class TestCache implements Cache {

  private static final Map<Object, ValueWrapper> storage = new HashMap<>();

  @Override
  public String getName() {
    return "fake test cache";
  }

  @Override
  public Object getNativeCache() {
    return null;
  }

  @Override
  public ValueWrapper get(Object o) {
    return storage.get(o);
  }

  @Override
  public <T> T get(Object o, Class<T> aClass) {
    return null;
  }

  @Override
  public <T> T get(Object o, Callable<T> callable) {
    return null;
  }

  @Override
  public void put(Object o, Object o1) {
      SimpleValueWrapper svw = new SimpleValueWrapper(o1);
      storage.put(o, svw);
  }

  @Override
  public void evict(Object o) {}

  @Override
  public void clear() {}
}
