package com.cplier.platform.controller.oauth;

import com.cplier.platform.Constants;
import com.cplier.platform.common.Result;
import com.cplier.platform.common.ResultEnum;
import com.cplier.platform.entity.Oauth2ClientEntity;
import com.cplier.platform.service.Oauth2AuthService;
import com.cplier.platform.service.Oauth2ClientService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("authorize")
public class AuthorizeController {

  @Resource private Oauth2AuthService oauth2AuthService;
  @Resource private Oauth2ClientService oauth2ClientService;

  @ApiOperation("authorize请求")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "成功"),
        @ApiResponse(code = 401, message = "授权失败"),
        @ApiResponse(code = 400, message = "请求失败")
      })
  @PostMapping
  public Object authorize(HttpServletRequest req, HttpServletResponse res)
      throws URISyntaxException, OAuthSystemException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    try {
      // 构建OAuth 授权请求
      OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(req);

      // 检查传入的客户端id是否正确
      if (!oauth2AuthService.checkClientId(oauthRequest.getClientId())) {
        Result result = Result.unauthorized();
        result.setDesc(OAuthError.TokenResponse.INVALID_CLIENT);
        return new ResponseEntity<>(result, headers, HttpStatus.UNAUTHORIZED);
      }

      // 如果用户没有登录，跳转到登陆页面
      if (!oauth2AuthService.login(req)) { // 登录失败时跳转到登陆页面
        Oauth2ClientEntity client = oauth2ClientService.findByClientId(oauthRequest.getClientId());
        headers.setLocation(
            new URI(
                "http://icetai.cn/oauth2/login?client_id="
                    + client.getClientId()
                    + "&client_secret="
                    + client.getClientSecret()));
        return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
      }

      String username = req.getParameter("username"); // 获取用户名
      // 生成授权码
      String authorizationCode = null;
      // responseType目前仅支持CODE，另外还有TOKEN
      String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
      if (Objects.equals(responseType, ResponseType.CODE.toString())) {
        OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
        authorizationCode = oauthIssuerImpl.authorizationCode();
        oauth2AuthService.addAuthCode(authorizationCode, username);
      }

      // 进行OAuth响应构建
      OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
          OAuthASResponse.authorizationResponse(req, HttpServletResponse.SC_FOUND);
      // 设置授权码
      builder.setCode(authorizationCode);
      // 得到到客户端重定向地址
      String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);

      // 构建响应
      final OAuthResponse response = builder.location(redirectURI).buildQueryMessage();

      // 根据OAuthResponse返回ResponseEntity响应
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setLocation(new URI(response.getLocationUri()));
      return new ResponseEntity<>(headers, HttpStatus.valueOf(response.getResponseStatus()));
    } catch (OAuthProblemException e) {

      // 出错处理
      String redirectUri = e.getRedirectUri();
      if (OAuthUtils.isEmpty(redirectUri)) {
        // 告诉客户端没有传入redirectUri直接报错
        Result result = Result.of(ResultEnum.NOT_FOUND, null);
        result.setDesc(Constants.INVALID_REDIRECT_URI);
        return new ResponseEntity<>(result, headers, HttpStatus.NOT_FOUND);
      }
      headers.setLocation(new URI(redirectUri));
      return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
  }
}
