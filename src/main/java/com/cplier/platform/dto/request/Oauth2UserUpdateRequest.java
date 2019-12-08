package com.cplier.platform.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class Oauth2UserUpdateRequest implements Serializable, Cloneable {

  private String username;
}
