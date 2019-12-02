package com.cplier.platform.repository;

import com.cplier.platform.entity.Oauth2ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Oauth2ClientRepository extends JpaRepository<Oauth2ClientEntity, String> {
  Optional<Oauth2ClientEntity> findByClientSecret(String clientSecret);
}
