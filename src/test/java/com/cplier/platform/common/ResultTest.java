package com.cplier.platform.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultTest {

  private Result result;

  @Test
  void of() {
    result = Result.of(ResultEnum.SUCCESS);
  }

  @Test
  void testOf() {
    result = Result.of(ResultEnum.UNAUTHORIZED);
    assertEquals(401, result.getCode());
  }

  @Test
  void success() {
    result = Result.success();
    assertEquals(200, result.getCode());
  }

  @Test
  void testSuccess() {
    result = Result.success("data");
    assertEquals("data", result.getData());
  }

  @Test
  void fail() {
    result = Result.fail();
    assertEquals(500, result.getCode());
  }

  @Test
  void testFail() {
    result = Result.fail("data");
    assertEquals("data", result.getData());
  }

  @Test
  void unauthorized() {
    result = Result.unauthorized();
    assertEquals(401, result.getCode());
  }

  @Test
  void testUnauthorized() {
    result = Result.unauthorized("data");
    assertEquals("data", result.getData());
  }

  @Test
  void bad_request() {
    result = Result.badRequest();
    assertEquals(400, result.getCode());
  }

  @Test
  void testBad_request() {
    result = Result.badRequest("data");
    assertEquals("data", result.getData());
  }
}
