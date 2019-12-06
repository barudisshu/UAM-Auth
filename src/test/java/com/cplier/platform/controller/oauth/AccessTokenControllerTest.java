package com.cplier.platform.controller.oauth;

import com.cplier.platform.service.Oauth2AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.annotation.Resource;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccessTokenController.class)
class AccessTokenControllerTest {

  @MockBean
  Oauth2AuthService oauth2AuthService;
  @Resource
  MockMvc mockMvc;

  private static final String CACHE_CODE = "code-cache";
  private static final String clientId = "client_id";
  private static final String clientSecret = "client_secret";
  private static final String authCode = "12345";
  private static final String accessToken = "fkaleivvla;l2333iajmlKvvll;";
  private static final String username = "peter";
  private static final String password = "passw0rd";
  private static final String salt = "efa9298b080c4068a170a0f7fa52d194";

  private MockHttpServletRequestBuilder mockBuild;

  @BeforeEach
  void setUp() {
    mockBuild =
        post("/access")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .param("grant_type", "authorization_code")
            .param("redirect_uri", "http://localhost")
            .param("code", authCode)
            .param("client_id", clientId)
            .param("client_secret", clientSecret);

    given(oauth2AuthService.getUsernameByAccessToken(accessToken)).willReturn(username);
  }

  @DisplayName("获取accessToken[client_id不存在]")
  @Test
  void token1() throws Exception {
    given(oauth2AuthService.checkClientId(clientId)).willReturn(false);
    this.mockMvc.perform(mockBuild).andExpect(status().isUnauthorized()).andReturn();
  }

  @DisplayName("获取accessToken[client_secret不存在]")
  @Test
  void token2() throws Exception {
    given(oauth2AuthService.checkClientId(clientId)).willReturn(true);
    given(oauth2AuthService.checkClientSecret(clientSecret)).willReturn(false);
    this.mockMvc.perform(mockBuild).andExpect(status().isUnauthorized()).andReturn();
  }

  @DisplayName("获取accessToken[检查authorization_code]")
  @Test
  void token3() throws Exception {
    given(oauth2AuthService.checkClientId(clientId)).willReturn(true);
    given(oauth2AuthService.checkClientSecret(clientSecret)).willReturn(true);
    given(oauth2AuthService.checkAuthCode(authCode)).willReturn(false);
    this.mockMvc.perform(mockBuild).andExpect(status().isUnauthorized()).andReturn();
  }

  @DisplayName("获取accessToken[成功]")
  @Test
  void token4() throws Exception {
    given(oauth2AuthService.checkClientId(clientId)).willReturn(true);
    given(oauth2AuthService.checkClientSecret(clientSecret)).willReturn(true);
    given(oauth2AuthService.checkAuthCode(authCode)).willReturn(true);
    given(oauth2AuthService.getUsernameByAuthCode(authCode)).willReturn(username);
    this.mockMvc.perform(mockBuild).andExpect(status().isOk()).andReturn();
  }

  @DisplayName("获取accessToken[参数出错]")
  @Test
  void token5() throws Exception {
    given(oauth2AuthService.checkClientId(clientId)).willReturn(true);
    this.mockMvc
        .perform(post("/access").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andExpect(status().isUnauthorized())
        .andReturn();
  }

  @DisplayName("验证accessToken[通过]")
  @Test
  void checkAccessTokenPass() throws Exception {
    given(oauth2AuthService.checkAccessToken(accessToken)).willReturn(true);
    this.mockMvc
        .perform(post("/check_accessToken").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();
  }

  @DisplayName("验证accessToken[失败]")
  @Test
  void checkAccessTokenFail() throws Exception {
    given(oauth2AuthService.checkAccessToken(accessToken)).willReturn(false);
    this.mockMvc
        .perform(post("/check_accessToken").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();
  }
}
