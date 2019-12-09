package com.cplier.platform.logging;

import com.cplier.platform.util.DateUtil;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

public class P6SpyLogger implements MessageFormattingStrategy {

  /** 过滤掉定时任务的 SQL */
  @Override
  public String formatMessage(
      int connectionId,
      String now,
      long elapsed,
      String category,
      String prepared,
      String sql,
      String url) {
    return DateUtil.formatFullTime(LocalDateTime.now(), DateUtil.FULL_TIME_SPLIT_PATTERN)
        + "  | cost "
        + elapsed
        + " ms | SQL query："
        + sql.replaceAll("[\\s]+", StringUtils.SPACE);
  }
}
