package com.cplier.platform.dto.request;

import lombok.Data;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Oauth2AccessRequest implements Serializable, Cloneable {

  private String clientId;
  private String clientSecret;
  private String code;
  private String grantType;
  private String redirectUri;

  public Map<String, Object> wrap() {
    Map<String, Object> params = new HashMap<>();
    params.put("client_id", clientId);
    params.put("client_secret", clientSecret);
    params.put("grant_type", grantType);
    params.put("code", code);
    params.put("redirect_uri", redirectUri);
    return params;
  }

  public void validate() throws OAuthProblemException {
    // todo: 校验参数数据
  }
}
