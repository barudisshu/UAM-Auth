package com.cplier.platform.controller.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cplier.platform.Constants;
import com.cplier.platform.common.Result;
import com.cplier.platform.common.ResultEnum;
import com.cplier.platform.entity.Oauth2ClientEntity;
import com.cplier.platform.service.Oauth2AuthService;
import com.cplier.platform.service.Oauth2ClientService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthorizeController.class)
class AuthorizeControllerTest {

  @MockBean
  Oauth2AuthService oauth2AuthService;
  @MockBean
  Oauth2ClientService oauth2ClientService;
  @Resource ObjectMapper objectMapper;
  @Resource
  MockMvc mockMvc;

  private static Oauth2ClientEntity oauth2ClientEntity;

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
    oauth2ClientEntity = new Oauth2ClientEntity();
    oauth2ClientEntity.setClientId(clientId);
    oauth2ClientEntity.setClientSecret(clientSecret);

    mockBuild =
        post("/authorize")
            .param("client_id", clientId)
            .param("response_type", "code")
            .param("redirect_uri", "http://localhost/auth/login");

    given(oauth2AuthService.checkClientId(clientId)).willReturn(true);
    given(oauth2AuthService.getUsernameByAccessToken(accessToken)).willReturn(username);
    given(oauth2AuthService.checkAccessToken(accessToken)).willReturn(true);
    given(oauth2ClientService.findByClientId(clientId)).willReturn(oauth2ClientEntity);
  }

  @DisplayName("认证token信息")
  @Test
  void authorize() throws Exception {
    given(oauth2AuthService.login(any())).willReturn(true);
    this.mockMvc
        .perform(mockBuild)
        .andExpect(status().isFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();
  }

  @DisplayName("认证token信息[client_id不存在]")
  @Test
  void authorize1() throws Exception {
    given(oauth2AuthService.login(any())).willReturn(true);
    given(oauth2AuthService.checkClientId(clientId)).willReturn(false);
    this.mockMvc
        .perform(mockBuild)
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();
  }

  @DisplayName("认证token信息[没有登录时，重定向]")
  @Test
  void authorize2() throws Exception {
    this.mockMvc
        .perform(mockBuild)
        .andExpect(status().isTemporaryRedirect())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            redirectedUrl(
                "http://localhost/auth/login?client_id=client_id&client_secret=client_secret"))
        .andReturn();
  }

  @DisplayName("认证token信息[参数不对]")
  @Test
  void authorize3() throws Exception {
    Result result = Result.of(ResultEnum.NOT_FOUND, null);
    result.setDesc(Constants.INVALID_REDIRECT_URI);

    this.mockMvc
        .perform(post("/authorize"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(result)))
        .andReturn();
  }

  @DisplayName("认证token信息[返回错误信息]")
  @Test
  void authorize4() throws Exception {
    this.mockMvc
        .perform(post("/authorize").param("redirect_uri", "http://localhost/auth/login"))
        .andExpect(status().isFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(redirectedUrl("http://localhost/auth/login"))
        .andReturn();
  }
}
