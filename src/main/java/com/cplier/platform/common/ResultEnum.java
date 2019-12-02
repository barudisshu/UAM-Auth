package com.cplier.platform.common;

public enum ResultEnum {
  SUCCESS(200, "success"),
  SERVER_ERROR(500, "server_error"),
  BAD_REQUEST(400, "bad_request"),
  UNAUTHORIZED(401, "unauthorized"),
  NOT_FOUND(404, "not_found");

  private int code;
  private String desc;

  ResultEnum(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public int getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }
}
