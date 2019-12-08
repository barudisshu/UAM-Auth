package com.cplier.platform.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Oauth2AuthorizeRequest implements Serializable, Cloneable {

  private String clientId;
  private String responseType;
  private String redirectURI;
  private String username;
  private String password;

  public Map<String, Object> wrap() {
    Map<String, Object> params = new HashMap<>();

    params.put("client_id", clientId);
    params.put("response_type", responseType);
    params.put("redirect_uri", redirectURI);
    params.put("username", username);
    params.put("password", password);

    return params;
  }
}
