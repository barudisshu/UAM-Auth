package com.cplier.platform.exception;

/** 限流异常 */
public class LimitAccessException extends Exception {

  public LimitAccessException(String message) {
    super(message);
  }
}
