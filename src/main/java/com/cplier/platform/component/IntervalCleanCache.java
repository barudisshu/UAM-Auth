package com.cplier.platform.component;

import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/** 项目启动，以及每各30分钟清理缓存内容 */
@Component
public class IntervalCleanCache {
  @Resource private CacheManager cacheManager;

  @Scheduled(cron = "0 0/30 * * * ?")
  public void clearCacheSchedule() {
    for (String name : cacheManager.getCacheNames()) {
      Objects.requireNonNull(cacheManager.getCache(name)).clear();
    }
  }
}
