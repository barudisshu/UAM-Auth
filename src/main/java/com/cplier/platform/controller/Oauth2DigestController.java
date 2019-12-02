package com.cplier.platform.controller;

import com.cplier.platform.common.Result;
import com.cplier.platform.entity.Oauth2UserEntity;
import com.cplier.platform.exception.UAMException;
import com.cplier.platform.exception.LimitAccessException;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.shiro.authz.UnauthorizedException;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/** 仅用于异常情况的排查，非业务逻辑接口 */
@RestController
@RequestMapping("digest")
public class Oauth2DigestController {

  @GetMapping("global_exception_handler")
  public Result internalServerError() throws IllegalAccessException {
    throw new IllegalAccessException("系统内部错误");
  }

  @GetMapping("global_business_handler")
  public Result paramsInvalidError() throws UAMException {
    throw new UAMException("业务逻辑错误");
  }

  @GetMapping("global_bind_exception_handler")
  public Result bindError() throws BindException {
    BindingResult bindingResult = new DirectFieldBindingResult(new Oauth2UserEntity(), "oauth2User");
    bindingResult.recordFieldValue("username", String.class, "peter");
    bindingResult.addError(new FieldError("peter", "username", ""));
    throw new BindException(bindingResult);
  }

  @GetMapping("global_constraint_violation_exception_handler")
  public Result constraintViolationError() {
    Set<ConstraintViolation<Oauth2UserEntity>> constraintViolations = new HashSet<>();
    ConstraintViolation<Oauth2UserEntity> con =
        ConstraintViolationImpl.forReturnValueValidation(
            "template",
            new HashMap<>(),
            new HashMap<>(),
            "interpolated",
            Oauth2UserEntity.class,
            new Oauth2UserEntity(),
            new Oauth2UserEntity(),
            new Oauth2UserEntity(),
            PathImpl.createPathFromString("a.b"),
            null,
            ElementType.ANNOTATION_TYPE,
            null,
            null);
    constraintViolations.add(con);
    throw new ConstraintViolationException(constraintViolations);
  }

  @GetMapping("global_oauth_problem_exception_handler")
  public Result oauthProblemError() throws OAuthProblemException {
    throw OAuthProblemException.error("授权错误");
  }

  @GetMapping("global_limit_access_exception_handler")
  public Result limitAccessError() throws LimitAccessException {
    throw new LimitAccessException("限制请求错误");
  }

  @GetMapping("global_unauthorized_exception_handler")
  public Result unauthorizedError() {
    throw new UnauthorizedException("权限不足错误");
  }
}
