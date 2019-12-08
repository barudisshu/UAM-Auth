package com.cplier.platform.service.impl;

import com.cplier.platform.component.PasswordHelper;
import com.cplier.platform.entity.Oauth2UserEntity;
import com.cplier.platform.exception.DuplicatedException;
import com.cplier.platform.exception.EntityNotExistsException;
import com.cplier.platform.repository.Oauth2UserRepository;
import com.cplier.platform.service.Oauth2UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.*;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@CacheConfig(cacheNames = {"users"})
public class Oauth2UserServiceImpl implements Oauth2UserService {

  @Resource MessageSource messageSource;
  @Resource private Oauth2UserRepository oauth2UserRepository;
  @Resource private PasswordHelper passwordHelper;

  @Cacheable
  @Override
  public List<Oauth2UserEntity> findAll() {
    return oauth2UserRepository.findAll();
  }

  @Cacheable(key = "#uid")
  @Override
  public Oauth2UserEntity findById(String uid) {
    return oauth2UserRepository
        .findById(uid)
        .orElseThrow(
            () -> new EntityNotFoundException(String.format("uid=%s does not exists", uid)));
  }

  @Caching(
      evict = {
        @CacheEvict(key = "#user.uid", allEntries = true),
        @CacheEvict(key = "#user.username", allEntries = true)
      })
  @CachePut(key = "#user.uid")
  @Override
  public Oauth2UserEntity saveOrUpdate(@NotNull Oauth2UserEntity user) {
    if (StringUtils.isBlank(user.getSalt())) {
      passwordHelper.encryptPassword(user);
    }
    Optional<Oauth2UserEntity> o2ue = oauth2UserRepository.findByUsername(user.getUsername());
    if (o2ue.isPresent()) throw new DuplicatedException("用户名重复");
    return oauth2UserRepository.saveAndFlush(user);
  }

  @CacheEvict(key = "#uid", allEntries = true)
  @Override
  public void deleteById(String uid) {
    try {
      oauth2UserRepository.deleteById(uid);
    } catch (EmptyResultDataAccessException e) {
      throw new EntityNotExistsException(e.getMessage(), "没有这个用户id");
    }
  }

  /**
   * 更新密码时，清空缓存数据
   *
   * @param uid 用户ID
   * @param newPwd 新的密码
   */
  @Caching(evict = {@CacheEvict(key = "#uid", allEntries = true)})
  @Override
  public Oauth2UserEntity changePwd(String uid, String newPwd) {
    Oauth2UserEntity oauth2UserEntity =
        oauth2UserRepository
            .findById(uid)
            .orElseThrow(
                () ->
                    new EntityNotExistsException(
                        String.format("change password failure, id=%s does not exists", uid)));
    oauth2UserEntity.setPassword(newPwd);
    passwordHelper.encryptPassword(oauth2UserEntity);
    return oauth2UserRepository.saveAndFlush(oauth2UserEntity);
  }

  @Cacheable(key = "#username")
  @Override
  public Oauth2UserEntity findByUsername(String username) {
    return oauth2UserRepository
        .findByUsername(username)
        .orElseThrow(
            () ->
                new EntityNotExistsException(
                    String.format("the username=%s does not exists", username)));
  }

  /**
   * 验证登录
   *
   * @param username 用户名
   * @param password 密码
   * @param salt 盐
   * @param encryptpwd 加密后的密码
   * @return boolean
   */
  @Override
  public boolean checkUser(
      @NotBlank String username,
      @NotBlank String password,
      @NotBlank String salt,
      @NotBlank String encryptpwd) {
    String pwd = passwordHelper.encryptPassword(username, password, salt);
    return pwd.equals(encryptpwd);
  }
}
