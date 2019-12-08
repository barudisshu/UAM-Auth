package com.cplier.platform.service.impl;

import com.cplier.platform.entity.Oauth2UserEntity;
import com.cplier.platform.service.Oauth2AuthService;
import com.cplier.platform.service.Oauth2ClientService;
import com.cplier.platform.service.Oauth2UserService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/** * 认证授权服务 */
@Service
@Transactional
public class Oauth2AuthServiceImpl implements Oauth2AuthService {

  private static final String ERROR = "error";

  private static final String ERROR_MSG_ILLEGAL = "非法的请求";
  private static final String ERROR_MSG_ILLEGAL_CREDENTIAL = "登录失败:用户名或密码不能为空";
  private static final String ERROR_MSG_ILLEGAL_PASS = "登录失败:密码不正确";
  private static final String ERROR_MSG_ILLEGAL_USERNAME = "登录失败:用户名不正确";
  private static final String ERROR_MSG_ILLEGAL_LOGIN = "登录失败:";

  private Cache cache;

  @Resource Oauth2UserService oauth2UserService;
  @Resource Oauth2ClientService oauth2ClientService;

  /** 获取Spring Cache */
  public Oauth2AuthServiceImpl(CacheManager cacheManager) {
    this.cache = cacheManager.getCache("code-cache");
  }

  @Override
  public void addAuthCode(String authCode, String username) {
    cache.put(authCode, username);
  }

  @Override
  public void addAccessToken(String accessToken, String username) {
    cache.put(accessToken, username);
  }

  @Override
  public boolean checkAuthCode(String authCode) {
    return cache.get(authCode) != null;
  }

  @Override
  public boolean checkAccessToken(String accessToken) {
    return cache.get(accessToken) != null;
  }

  @Override
  public String getUsernameByAuthCode(String authCode) {
    Cache.ValueWrapper valueWrapper = cache.get(authCode);
    if (valueWrapper != null) {
      return (String) valueWrapper.get();
    }
    return null;
  }

  @Override
  public String getUsernameByAccessToken(String accessToken) {
    Cache.ValueWrapper valueWrapper = cache.get(accessToken);
    if (valueWrapper != null) {
      return (String) valueWrapper.get();
    }
    return null;
  }

  @Override
  public long getExpireIn() {
    return 3600L;
  }

  @Override
  public boolean checkClientId(String clientId) {
    return oauth2ClientService.findByClientId(clientId) != null;
  }

  @Override
  public boolean checkClientSecret(String clientSecret) {
    return oauth2ClientService.findByClientSecret(clientSecret) != null;
  }

  @Override
  public boolean login(@NotNull HttpServletRequest request) {
    if ("get".equalsIgnoreCase(request.getMethod())) {
      return false;
    }
    String username = request.getParameter("username");
    String password = request.getParameter("password");

    if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
      return false;
    }
    try {
      Oauth2UserEntity oauth2UserEntity = oauth2UserService.findByUsername(username);
      if (oauth2UserEntity != null) {
        if (!oauth2UserService.checkUser(
            username, password, oauth2UserEntity.getSalt(), oauth2UserEntity.getPassword())) {
          request.setAttribute(ERROR, ERROR_MSG_ILLEGAL_PASS);
          return false;
        } else {
          return true;
        }
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }
}
