package com.cplier.platform.dto.request;

import com.cplier.platform.entity.Oauth2UserEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class Oauth2UserCreateRequest implements Serializable, Cloneable {

  private String name;
  private String username;
  private String password;
  private String email;

  public Oauth2UserEntity mapping() {
    Oauth2UserEntity entity = new Oauth2UserEntity();
    entity.setName(name);
    entity.setUsername(username);
    entity.setPassword(password);
    entity.setEmail(email);
    return entity;
  }
}
