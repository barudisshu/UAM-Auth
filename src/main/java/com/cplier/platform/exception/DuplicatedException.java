package com.cplier.platform.exception;

public class DuplicatedException extends RuntimeException {

  private String desc;

  public DuplicatedException(String desc) {
    this.desc = desc;
  }

  public DuplicatedException(String message, String desc) {
    super(message);
    this.desc = desc;
  }

  public String getDesc() {
    return desc;
  }
}
