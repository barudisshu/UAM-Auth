package com.cplier.platform.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Result<T> {
  private int code;
  private String desc;
  private T data;

  private Result(ResultEnum resultEnum) {
    this.code = resultEnum.getCode();
    this.desc = resultEnum.getDesc();
  }

  private Result(ResultEnum resultEnum, T data) {
    this.code = resultEnum.getCode();
    this.desc = resultEnum.getDesc();
    this.data = data;
  }

  public static Result of(ResultEnum resultEnum) {
    return new Result<>(resultEnum);
  }

  public static <T> Result of(ResultEnum resultEnum, T data) {
    return new Result<>(resultEnum, data);
  }

  public static Result success() {
    return new Result<>(ResultEnum.SUCCESS);
  }

  public static <T> Result success(T data) {
    return new Result<>(ResultEnum.SUCCESS, data);
  }

  public static Result fail() {
    return new Result<>(ResultEnum.SERVER_ERROR);
  }

  public static <T> Result fail(T data) {
    return new Result<>(ResultEnum.SERVER_ERROR, data);
  }

  public static Result unauthorized() {
    return new Result<>(ResultEnum.UNAUTHORIZED);
  }

  public static <T> Result unauthorized(T data) {
    return new Result<>(ResultEnum.UNAUTHORIZED, data);
  }

  public static Result badRequest() {
    return new Result<>(ResultEnum.BAD_REQUEST);
  }

  public static <T> Result badRequest(T data) {
    return new Result<>(ResultEnum.BAD_REQUEST, data);
  }
}
