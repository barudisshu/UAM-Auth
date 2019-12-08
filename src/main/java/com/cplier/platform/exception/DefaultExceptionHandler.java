package com.cplier.platform.exception;

import com.cplier.platform.common.Result;
import com.cplier.platform.common.ResultEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.Set;

@Slf4j
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class DefaultExceptionHandler {

  @ExceptionHandler(value = Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Result handleException(Exception e) {
    log.error("internal exception, error msg: {}", e.getMessage());
    return Result.of(ResultEnum.SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(value = UAMException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Result handleParamsInvalidException(UAMException e) {
    log.error("system error: {}", e.getMessage());
    return Result.of(ResultEnum.SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(value = EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result handleEntityNotFoundException(EntityNotFoundException e) {
    log.error("entity error: {}", e.getMessage());
    return Result.of(ResultEnum.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(value = JsonProcessingException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result handleJsonException(JsonProcessingException e) {
    log.error("json error: {}", e.getMessage());
    return Result.of(ResultEnum.BAD_REQUEST, "json parser error");
  }

  @ExceptionHandler(value = DuplicatedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result handleDuplicatedException(DuplicatedException e) {
    log.error("duplicated error: {}", e.getDesc());
    return Result.of(ResultEnum.BAD_REQUEST, e.getDesc());
  }

  @ExceptionHandler(value = EntityNotExistsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result handleNotFoundException(EntityNotExistsException e) {
    log.error("duplicated error: {}", e.getDesc());
    return Result.of(ResultEnum.BAD_REQUEST, e.getDesc());
  }

  /**
   * 统一处理请求参数校验（实体对象传参）
   *
   * @param e {@link BindException}
   * @return {@link Result}
   */
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result validExceptionHandler(BindException e) {
    StringBuilder sb = new StringBuilder();
    List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
    for (FieldError error : fieldErrors) {
      sb.append(error.getField()).append(error.getDefaultMessage()).append(",");
    }
    sb = new StringBuilder(sb.substring(0, sb.length() - 1));
    return Result.of(ResultEnum.SERVER_ERROR, sb.toString());
  }

  /**
   * 统一处理请求参数校验（普通传参）
   *
   * @param e {@link ConstraintViolationException}
   * @return {@link Result}
   */
  @ExceptionHandler(value = ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result handleConstraintViolationException(ConstraintViolationException e) {
    StringBuilder sb = new StringBuilder();
    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
    for (ConstraintViolation<?> violation : violations) {
      Path path = violation.getPropertyPath();
      String[] pathArr = StringUtils.splitByWholeSeparatorPreserveAllTokens(path.toString(), ".");
      sb.append(pathArr[1]).append(violation.getMessage()).append(",");
    }
    sb = new StringBuilder(sb.substring(0, sb.length() - 1));
    return Result.of(ResultEnum.SERVER_ERROR, sb.toString());
  }

  @ExceptionHandler(value = OAuthProblemException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Result handleOauthProblemException(OAuthProblemException e) {
    log.error("授权过程出错: {}", e.getMessage());
    return Result.unauthorized();
  }

  @ExceptionHandler(value = LimitAccessException.class)
  @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
  public Result handleLimitAccessException(LimitAccessException e) {
    log.warn(e.getMessage());
    return Result.of(ResultEnum.SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(value = UnauthorizedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Result handleUnauthorizedException(Exception e) {
    log.error("权限不足哦, {}", e.getMessage());
    return Result.of(ResultEnum.SERVER_ERROR, "权限不足！");
  }
}
