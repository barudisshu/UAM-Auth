package com.cplier.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cplier.platform.Constants;
import com.cplier.platform.common.Result;
import com.cplier.platform.entity.Oauth2ClientEntity;
import com.cplier.platform.entity.Oauth2UserEntity;
import com.cplier.platform.service.Oauth2AuthService;
import com.cplier.platform.service.Oauth2UserService;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static com.cplier.platform.common.Result.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(Oauth2UserInfoController.class)
class Oauth2UserEntityInfoControllerTest {

  @MockBean
  Oauth2AuthService oauth2AuthService;
  @MockBean
  Oauth2UserService oauth2UserService;
  @Resource ObjectMapper objectMapper;
  @Resource
  MockMvc mockMvc;

  private static Oauth2ClientEntity oauth2ClientEntity;
  private static Oauth2UserEntity oauth2UserEntity;

  private static final String CACHE_CODE = "code-cache";
  private static final String clientId = "client_id";
  private static final String clientSecret = "client_secret";
  private static final String authCode = "12345";
  private static final String accessToken = "fkaleivvla;l2333iajmlKvvll;";
  private static final String username = "peter";
  private static final String password = "passw0rd";
  private static final String salt = "efa9298b080c4068a170a0f7fa52d194";

  @BeforeEach
  void setUp() {

    oauth2UserEntity = new Oauth2UserEntity();
    oauth2UserEntity.setUsername(username);
    oauth2UserEntity.setPassword(password);
    oauth2UserEntity.setSalt(salt);

    oauth2ClientEntity = new Oauth2ClientEntity();
    oauth2ClientEntity.setClientId(clientId);
    oauth2ClientEntity.setClientSecret(clientSecret);

    given(oauth2AuthService.getUsernameByAccessToken(accessToken)).willReturn(username);
    given(oauth2AuthService.checkAccessToken(accessToken)).willReturn(true);
    given(oauth2UserService.findByUsername(username)).willReturn(oauth2UserEntity);
  }

  @DisplayName("校验access_token")
  @Test
  void userInfo() throws Exception {
    given(oauth2UserService.findByUsername(username)).willReturn(oauth2UserEntity);

    this.mockMvc
        .perform(get("/v1/openapi/user_info").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(success(oauth2UserEntity))));
  }

  @DisplayName("校验access_token, token不能存在/过期")
  @Test
  void userInfoTokenExpire() throws Exception {
    given(oauth2UserService.findByUsername(username)).willReturn(null);
    given(oauth2AuthService.checkAccessToken(accessToken)).willReturn(false);

    Result actual = unauthorized();
    actual.setDesc(Constants.INVALID_ACCESS_TOKEN);

    this.mockMvc
        .perform(get("/v1/openapi/user_info").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(actual)))
        .andReturn();
  }

  @DisplayName("校验access_token, 出现验证错误[有错误码]")
  @Test
  void userInfoTokenOauthProblemException1() throws Exception {
    given(oauth2AuthService.checkAccessToken(accessToken)).willReturn(true);
    given(oauth2UserService.findByUsername(username))
        .willAnswer(
            invocation -> {
              throw OAuthProblemException.error("problem occur while oauth process");
            });

    this.mockMvc
        .perform(get("/v1/openapi/user_info").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(badRequest())))
        .andReturn();
  }

  @DisplayName("校验access_token, 出现验证错误[无错误码]")
  @Test
  void userInfoTokenOauthProblemException2() throws Exception {
    given(oauth2AuthService.checkAccessToken(accessToken)).willReturn(true);
    given(oauth2UserService.findByUsername(username))
        .willAnswer(
            invocation -> {
              throw OAuthProblemException.error("");
            });

    this.mockMvc
        .perform(get("/v1/openapi/user_info").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(unauthorized())))
        .andReturn();
  }

  @DisplayName("不校验access_token")
  @Test
  void nocheckUserInfo() throws Exception {
    given(oauth2UserService.findByUsername(username)).willReturn(oauth2UserEntity);

    this.mockMvc
        .perform(
            get("/v1/openapi/nocheck_user_info").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(success(oauth2UserEntity))));
  }

  @DisplayName("不校验access_token[失败]")
  @Test
  void nocheckUserInfoFail() throws Exception {
    given(oauth2UserService.findByUsername(username)).willReturn(null);

    this.mockMvc
        .perform(
            get("/v1/openapi/nocheck_user_info").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(fail())));
  }
}
