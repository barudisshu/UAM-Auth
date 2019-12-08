package com.cplier.platform.exception;

public class EntityNotExistsException extends RuntimeException {

  private String desc;

  public EntityNotExistsException(String desc) {
    this.desc = desc;
  }

  public EntityNotExistsException(String message, String desc) {
    super(message);
    this.desc = desc;
  }

  public String getDesc() {
    return desc;
  }
}
