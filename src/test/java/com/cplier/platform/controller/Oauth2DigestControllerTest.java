package com.cplier.platform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 全局异常测试 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(Oauth2DigestController.class)
class Oauth2DigestControllerTest {

  @Resource
  MockMvc mockMvc;

  @BeforeEach
  void setUp() {}

  @DisplayName("内部错误")
  @Test
  void internalServerError() throws Exception {
    mockMvc
        .perform(get("/digest/global_exception_handler"))
        .andExpect(status().isInternalServerError())
        .andReturn();
  }

  @DisplayName("业务错误")
  @Test
  void paramsInvalidError() throws Exception {
    mockMvc
        .perform(get("/digest/global_business_handler"))
        .andExpect(status().isInternalServerError())
        .andReturn();
  }

  @DisplayName("请求绑定参数错误")
  @Test
  void bindError() throws Exception {
    mockMvc
        .perform(get("/digest/global_bind_exception_handler"))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @DisplayName("参数校验错误")
  @Test
  void constraintViolationError() throws Exception {
    mockMvc
        .perform(get("/digest/global_constraint_violation_exception_handler"))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @DisplayName("授权错误")
  @Test
  void oauthProblemError() throws Exception {
    mockMvc
        .perform(get("/digest/global_oauth_problem_exception_handler"))
        .andExpect(status().isUnauthorized())
        .andReturn();
  }

  @DisplayName("连接限制")
  @Test
  void limitAccessError() throws Exception {
    mockMvc
        .perform(get("/digest/global_limit_access_exception_handler"))
        .andExpect(status().isTooManyRequests())
        .andReturn();
  }

  @DisplayName("禁止访问")
  @Test
  void unauthorizedError() throws Exception {
    mockMvc
        .perform(get("/digest/global_unauthorized_exception_handler"))
        .andExpect(status().isForbidden())
        .andReturn();
  }
}
