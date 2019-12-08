package com.cplier.platform.dto.request;

import com.cplier.platform.entity.Oauth2ClientEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class Oauth2ClientCreateRequest implements Serializable, Cloneable {

  private String clientName;

  public Oauth2ClientEntity mapping() {
    Oauth2ClientEntity entity = new Oauth2ClientEntity();
    entity.setClientSecret(UUID.randomUUID().toString());
    entity.setClientName(clientName);
    return entity;
  }
}
