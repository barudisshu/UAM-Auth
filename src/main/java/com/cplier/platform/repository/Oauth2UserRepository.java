package com.cplier.platform.repository;

import com.cplier.platform.entity.Oauth2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Oauth2UserRepository extends JpaRepository<Oauth2UserEntity, String> {

  Optional<Oauth2UserEntity> findByUsername(String username);

  Optional<Oauth2UserEntity> findByEmail(String email);

  @Query("select o from Oauth2UserEntity o where o.username = :identified or o.email = :identified")
  Optional<Oauth2UserEntity> findByIdentified(@Param("identified") String identified);
}
