package com.cplier.platform.controller;

import com.cplier.platform.Constants;
import com.cplier.platform.common.Result;
import com.cplier.platform.entity.Oauth2UserEntity;
import com.cplier.platform.service.Oauth2AuthService;
import com.cplier.platform.service.Oauth2UserService;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/** 通过access_token获取用户信息 */
@RestController
@RequestMapping("v1/openapi")
public class Oauth2UserInfoController {

  @Resource Oauth2AuthService oauth2AuthService;

  @Resource Oauth2UserService oauth2UserService;

  @GetMapping("user_info")
  public HttpEntity userInfo(HttpServletRequest request) throws OAuthSystemException {
    return checkAccessToken(request);
  }

  @GetMapping("nocheck_user_info")
  public HttpEntity nocheckUserInfo(@NotNull HttpServletRequest request)
      throws OAuthSystemException, OAuthProblemException {
    return nocheckAccessToken(request);
  }

  /**
   * 不校验accessToken
   *
   * @param request {@link HttpServletRequest}
   * @return {@link HttpEntity}
   * @throws OAuthSystemException e
   * @throws OAuthProblemException e
   */
  private HttpEntity nocheckAccessToken(@NotNull HttpServletRequest request)
      throws OAuthSystemException, OAuthProblemException {

    // 构建OAuth资源请求
    OAuthAccessResourceRequest oauthRequest =
        new OAuthAccessResourceRequest(request, ParameterStyle.HEADER);

    // 获取Access Token
    String accessToken = oauthRequest.getAccessToken();

    // 获取用户名
    String username = oauth2AuthService.getUsernameByAccessToken(accessToken);
    Oauth2UserEntity oauth2UserEntity = oauth2UserService.findByUsername(username);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    if (oauth2UserEntity != null) {
      Result result = Result.success(oauth2UserEntity);
      return new ResponseEntity<>(result, headers, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(Result.fail(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 校验accessToken
   *
   * @param request {@link HttpServletRequest}
   * @return {@link HttpEntity}
   * @throws OAuthSystemException e
   */
  private HttpEntity checkAccessToken(HttpServletRequest request) throws OAuthSystemException {
    try {
      // 构建OAuth资源请求
      OAuthAccessResourceRequest oauthRequest =
          new OAuthAccessResourceRequest(request, ParameterStyle.HEADER);
      // 获取Access Token
      String accessToken = oauthRequest.getAccessToken();
      // 验证Access Token
      if (!oauth2AuthService.checkAccessToken(accessToken)) {
        // 如果不存在/过期了，返回未验证错误，需重新验证
        OAuthResponse oauthResponse =
            OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                .setRealm(Constants.RESOURCE_SERVER_NAME)
                .setError(OAuthError.ResourceResponse.INVALID_TOKEN)
                .buildHeaderMessage();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        Result result = Result.unauthorized(oauthResponse.getBody());
        result.setDesc(Constants.INVALID_ACCESS_TOKEN);

        return new ResponseEntity<>(result, responseHeaders, HttpStatus.UNAUTHORIZED);
      }
      // 获取用户名
      String username = oauth2AuthService.getUsernameByAccessToken(accessToken);
      Oauth2UserEntity oauth2UserEntity = oauth2UserService.findByUsername(username);

      return new ResponseEntity<>(Result.success(oauth2UserEntity), HttpStatus.OK);
    } catch (OAuthProblemException e) {
      // 检查是否设置了错误码
      String errorCode = e.getError();
      if (OAuthUtils.isEmpty(errorCode)) {
        OAuthResponse oauthResponse =
            OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                .setRealm(Constants.RESOURCE_SERVER_NAME)
                .buildHeaderMessage();
        HttpHeaders headers = new HttpHeaders();
        headers.add(
            OAuth.HeaderType.WWW_AUTHENTICATE,
            oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(Result.unauthorized(), headers, HttpStatus.UNAUTHORIZED);
      }
      OAuthResponse oauthResponse =
          OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
              .setRealm(Constants.RESOURCE_SERVER_NAME)
              .setError(e.getError())
              .setErrorDescription(e.getDescription())
              .setErrorUri(e.getUri())
              .buildHeaderMessage();
      HttpHeaders headers = new HttpHeaders();
      headers.add(
          OAuth.HeaderType.WWW_AUTHENTICATE,
          oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
      headers.setContentType(MediaType.APPLICATION_JSON);
      return new ResponseEntity<>(
          Result.badRequest(oauthResponse.getBody()), headers, HttpStatus.BAD_REQUEST);
    }
  }
}
