package com.cplier.platform.controller.oauth;

import com.cplier.platform.Constants;
import com.cplier.platform.common.Result;
import com.cplier.platform.common.ResultEnum;
import com.cplier.platform.dto.request.Oauth2AuthorizeRequest;
import com.cplier.platform.entity.Oauth2ClientEntity;
import com.cplier.platform.service.Oauth2AuthService;
import com.cplier.platform.service.Oauth2ClientService;
import com.cplier.platform.servlet.ParameterRequestWrapper;
import io.swagger.annotations.*;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static com.cplier.platform.Constants.RESOURCE_ENTRY_ENDPOINT;

@Slf4j
@RestController
@RequestMapping("authorize")
@Api(tags = "授权相关")
public class AuthorizeController {

  @Resource private Oauth2AuthService oauth2AuthService;
  @Resource private Oauth2ClientService oauth2ClientService;

  @ApiOperation(value = "authorize请求", notes = "进行授权", produces = "application/json")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "client_id",
        value = "client_id",
        required = true,
        dataType = "String",
        paramType = "query"),
    @ApiImplicitParam(
        name = "response_type",
        value = "response_type",
        required = true,
        dataType = "String",
        paramType = "query",
        defaultValue = "code"),
    @ApiImplicitParam(
        name = "redirect_uri",
        value = "redirect_uri",
        dataType = "String",
        paramType = "query")
  })
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "成功"),
        @ApiResponse(code = 401, message = "授权失败"),
        @ApiResponse(code = 400, message = "请求失败")
      })
  @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
  public Object authorize(@RequestBody Oauth2AuthorizeRequest json, HttpServletRequest req)
      throws URISyntaxException, OAuthSystemException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 由于使用json传输，需要回写request处理
    ParameterRequestWrapper wrapper = new ParameterRequestWrapper(req);
    wrapper.addAllParameters(json.wrap());

    try {
      // 构建OAuth 授权请求
      OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(wrapper);

      // 检查传入的客户端id是否正确
      if (!oauth2AuthService.checkClientId(oauthRequest.getClientId())) {
        Result result = Result.unauthorized();
        result.setDesc(OAuthError.TokenResponse.INVALID_CLIENT);
        return new ResponseEntity<>(result, headers, HttpStatus.UNAUTHORIZED);
      }

      // 如果用户没有登录，跳转到登陆页面
      if (!oauth2AuthService.login(wrapper)) { // 登录失败时跳转到登陆页面
        Oauth2ClientEntity client = oauth2ClientService.findByClientId(oauthRequest.getClientId());
        Result result = Result.of(ResultEnum.UNAUTHORIZED);
        result.setDesc(OAuthError.ResourceResponse.INVALID_REQUEST);
        return new ResponseEntity<>(result, headers, HttpStatus.UNAUTHORIZED);
      }

      String username = wrapper.getParameter("username"); // 获取用户名
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
          OAuthASResponse.authorizationResponse(wrapper, HttpServletResponse.SC_FOUND);
      // 设置授权码
      builder.setCode(authorizationCode);
      // 得到到客户端重定向地址
      String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);

      // 根据OAuthResponse返回ResponseEntity响应
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      Result result = Result.success(authorizationCode);
      return new ResponseEntity<>(result, headers, HttpStatus.OK);
    } catch (OAuthProblemException e) {

      // 出错处理
      String redirectUri = e.getRedirectUri();
      if (OAuthUtils.isEmpty(redirectUri)) {
        // 告诉客户端没有传入redirectUri直接报错
        Result result = Result.of(ResultEnum.BAD_REQUEST);
        result.setDesc(Constants.INVALID_REDIRECT_URI);
        return new ResponseEntity<>(result, headers, HttpStatus.BAD_REQUEST);
      }
      headers.setLocation(new URI(redirectUri));
      return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
  }
}
