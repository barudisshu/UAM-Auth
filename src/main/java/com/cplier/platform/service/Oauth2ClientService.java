package com.cplier.platform.service;

import com.cplier.platform.entity.Oauth2ClientEntity;

import java.util.List;
import java.util.Optional;

public interface Oauth2ClientService {

  List<Oauth2ClientEntity> findAll();

  Oauth2ClientEntity findByClientId(String clientId);

  Oauth2ClientEntity findByClientSecret(String clientSecret);

  Oauth2ClientEntity saveOrUpdate(Oauth2ClientEntity user);

  void deleteByClientId(String clientId);
}
