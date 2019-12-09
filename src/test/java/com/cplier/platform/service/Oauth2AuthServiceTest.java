package com.cplier.platform.service;

import com.cplier.platform.entity.Oauth2ClientEntity;
import com.cplier.platform.entity.Oauth2UserEntity;
import com.cplier.platform.service.impl.Oauth2AuthServiceImpl;
import com.cplier.platform.testhelper.TestCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Oauth2AuthServiceTest {

  @Mock Oauth2UserService oauth2UserService;
  @Mock Oauth2ClientService oauth2ClientService;
  private static CacheManager cacheManager = new ConcurrentMapCacheManager();

  private static final Cache cache = new TestCache();
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

  @InjectMocks Oauth2AuthService oauth2AuthService = new Oauth2AuthServiceImpl(cacheManager);

  @BeforeEach
  void setUp() {

    oauth2UserEntity = new Oauth2UserEntity();
    oauth2UserEntity.setUsername(username);
    oauth2UserEntity.setPassword(password);
    oauth2UserEntity.setSalt(salt);

    oauth2ClientEntity = new Oauth2ClientEntity();
    oauth2ClientEntity.setClientId(clientId);
    oauth2ClientEntity.setClientSecret(clientSecret);

    cacheManager = Mockito.mock(ConcurrentMapCacheManager.class);
    when(cacheManager.getCache(CACHE_CODE)).thenReturn(cache);
    when(oauth2ClientService.findByClientId(any())).thenReturn(oauth2ClientEntity);
    when(oauth2ClientService.findByClientSecret(any())).thenReturn(oauth2ClientEntity);
  }

  @DisplayName("缓存中写入auth_code")
  @Test
  void addAuthCode() {
    oauth2AuthService.addAuthCode(authCode, username);
  }

  @DisplayName("缓存中写入accessToken")
  @Test
  void addAccessToken() {
    oauth2AuthService.addAccessToken(accessToken, username);
  }

  @DisplayName("校验缓存中是否存在auth_code")
  @Test
  void checkAuthCode() {
    oauth2AuthService.checkAuthCode(authCode);
  }

  @DisplayName("校验缓存中是否存在accessToken")
  @Test
  void checkAccessToken() {
    oauth2AuthService.checkAccessToken(accessToken);
  }

  @DisplayName("通过auth_code获取用户名")
  @Test
  void getUsernameByAuthCode() {
    oauth2AuthService.getUsernameByAuthCode(authCode);
  }

  @DisplayName("通过accessToken获取用户名")
  @Test
  void getUsernameByAccessToken() {
    oauth2AuthService.getUsernameByAccessToken(accessToken);
  }

  @DisplayName("获取过期时间")
  @Test
  void getExpireIn() {
    assertEquals(3600L, oauth2AuthService.getExpireIn());
  }

  @DisplayName("校验是否存在对应的client_id")
  @Test
  void checkClientId() {
    assertTrue(oauth2AuthService.checkClientId(clientId));
    verify(oauth2ClientService).findByClientId(any());
  }

  @DisplayName("校验是否存在对应的client_secret")
  @Test
  void checkClientSecret() {
    assertTrue(oauth2AuthService.checkClientSecret(clientSecret));
    verify(oauth2ClientService).findByClientSecret(any());
  }

  @DisplayName("登录请求不合法")
  @Test
  void checkLogin1() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("get");
    oauth2AuthService.login(request);
  }

  @DisplayName("参数没有传递")
  @Test
  void checkLogin2() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("username1", username);
    request.addParameter("password1", password);
    oauth2AuthService.login(request);
  }

  @DisplayName("登录请求信息正确，但找不到该用户")
  @Test
  void checkLogin3() {
    when(oauth2UserService.findByUsername(any())).thenReturn(null);
    when(oauth2UserService.checkUser(password, salt, password)).thenReturn(true);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("username", username);
    request.addParameter("password", password);
    oauth2AuthService.login(request);
  }

  @DisplayName("登录用户密码不对时")
  @Test
  void checkLogin4() {
    when(oauth2UserService.findByUsername(any())).thenReturn(oauth2UserEntity);
    when(oauth2UserService.checkUser(password, salt, password)).thenReturn(false);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("username", username);
    request.addParameter("password", password);
    oauth2AuthService.login(request);
  }

  @DisplayName("登录信息正确")
  @Test
  void checkLogin5() {
    when(oauth2UserService.findByUsername(any())).thenReturn(oauth2UserEntity);
    when(oauth2UserService.checkUser(password, salt, password)).thenReturn(true);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("username", username);
    request.addParameter("password", password);
    oauth2AuthService.login(request);
  }

  @DisplayName("触发了异常")
  @Test
  void checkLogin6() {
    given(oauth2UserService.findByUsername(any()))
        .willAnswer(
            invocation -> {
              throw new Exception("放个异常");
            });
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("username", username);
    request.addParameter("password", password);
    oauth2AuthService.login(request);
  }
}
