package com.cplier.platform.repository;

import com.cplier.platform.entity.Oauth2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Oauth2UserRepository extends JpaRepository<Oauth2UserEntity, String> {
  Optional<Oauth2UserEntity> findByUsername(String username);
}
