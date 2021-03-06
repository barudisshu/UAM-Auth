package com.cplier.platform.controller.oauth;

import com.cplier.platform.Constants;
import com.cplier.platform.common.Result;
import com.cplier.platform.dto.request.Oauth2AccessRequest;
import com.cplier.platform.service.Oauth2AuthService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Slf4j
@RestController
@Api(tags = "token相关")
public class AccessTokenController {

  @Resource private Oauth2AuthService oauth2AuthService;

  @ApiOperation(
      nickname = "token",
      value = "accessToken请求",
      notes = "用户获取授权令牌",
      produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "client_id",
        value = "client_id",
        required = true,
        dataType = "String",
        paramType = "form"),
    @ApiImplicitParam(
        name = "client_secret",
        value = "client_secret",
        required = true,
        dataType = "String",
        paramType = "form"),
    @ApiImplicitParam(
        name = "code",
        value = "代码",
        required = true,
        dataType = "String",
        paramType = "form"),
    @ApiImplicitParam(
        name = "grant_type",
        value = "授权类型",
        required = true,
        dataType = "String",
        paramType = "form",
        allowableValues = "authorization_code, password",
        allowMultiple = true),
    @ApiImplicitParam(
        name = "redirect_uri",
        value = "重定向地址",
        required = true,
        dataType = "String",
        paramType = "form")
  })
  @ApiResponses({
    @ApiResponse(code = 200, message = "token生成"),
    @ApiResponse(code = 401, message = "认证失败")
  })
  @PostMapping("access")
  public HttpEntity token(@RequestBody Oauth2AccessRequest request) throws OAuthSystemException, URISyntaxException {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    try {
        request.validate();

      // 检查提交的客户端id是否正确
      if (!oauth2AuthService.checkClientId(request.getClientId())) {
        OAuthResponse response =
            OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                .setErrorDescription(Constants.INVALID_CLIENT_ID)
                .buildJSONMessage();
        Result result = Result.unauthorized();
        result.setDesc(OAuthError.TokenResponse.INVALID_CLIENT);
        return new ResponseEntity<>(
            result, headers, HttpStatus.valueOf(response.getResponseStatus()));
      }

      // 检查客户端安全KEY是否正确
      if (!oauth2AuthService.checkClientSecret(request.getClientSecret())) {
        OAuthResponse response =
            OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                .setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
                .setErrorDescription(Constants.INVALID_CLIENT_ID)
                .buildJSONMessage();
        Result result = Result.unauthorized();
        result.setDesc(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT);
        return new ResponseEntity<>(
            result, headers, HttpStatus.valueOf(response.getResponseStatus()));
      }

      String authCode = request.getCode();
      // 检查验证类型，此处只检查AUTHORIZATION_CODE类型，其他的还有PASSWORD或REFRESH_TOKEN
      if (Objects.equals(
              request.getGrantType(),
              GrantType.AUTHORIZATION_CODE.toString())
          && !oauth2AuthService.checkAuthCode(authCode)) {
        OAuthResponse response =
            OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                .setError(OAuthError.TokenResponse.INVALID_GRANT)
                .setErrorDescription(Constants.INVALID_AUTH_CODE)
                .buildJSONMessage();
        Result result = Result.unauthorized();
        result.setDesc(OAuthError.TokenResponse.INVALID_GRANT);
        return new ResponseEntity<>(
            result, headers, HttpStatus.valueOf(response.getResponseStatus()));
      }

      // 生成Access Token
      OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
      final String accessToken = oauthIssuerImpl.accessToken();
      oauth2AuthService.addAccessToken(
          accessToken, oauth2AuthService.getUsernameByAuthCode(authCode));

      // 生成OAuth响应
      OAuthResponse response =
          OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
              .setAccessToken(accessToken)
              .setExpiresIn(String.valueOf(oauth2AuthService.getExpireIn()))
              .buildJSONMessage();
      // 根据OAuthResponse生成ResponseEntity
      return new ResponseEntity<>(
          response.getBody(), headers, HttpStatus.valueOf(response.getResponseStatus()));

    } catch (OAuthProblemException e) {
      log.error("授权方式出错：{}", e.getDescription());
      // 构建错误响应
      OAuthResponse res =
          OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
              .error(e)
              .buildJSONMessage();
      return new ResponseEntity<>(
          Result.badRequest(res.getBody()), headers, HttpStatus.valueOf(res.getResponseStatus()));
    }
  }

  /**
   * 验证accessToken
   *
   * @return 认证信息
   */
  @ApiOperation("验证accessToken")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "有效的token"),
        @ApiResponse(code = 401, message = "无效的token")
      })
  @PostMapping("checkAccessToken")
  public ResponseEntity checkAccessToken(HttpServletRequest request)
      throws OAuthSystemException, OAuthProblemException {
    OAuthAccessResourceRequest oauthRequest =
        new OAuthAccessResourceRequest(request, ParameterStyle.HEADER);
    String accessToken = oauthRequest.getAccessToken();
    boolean b = oauth2AuthService.checkAccessToken(accessToken);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return b
        ? new ResponseEntity<>(
            Result.success(), headers, HttpStatus.valueOf(HttpServletResponse.SC_OK))
        : new ResponseEntity<>(
            Result.unauthorized(),
            headers,
            HttpStatus.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
  }
}
