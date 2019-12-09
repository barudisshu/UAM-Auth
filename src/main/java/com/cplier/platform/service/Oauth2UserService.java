package com.cplier.platform.service;

import com.cplier.platform.entity.Oauth2UserEntity;

import java.util.List;

public interface Oauth2UserService {

  List<Oauth2UserEntity> findAll();

  Oauth2UserEntity findById(String uid);

  Oauth2UserEntity saveOrUpdate(Oauth2UserEntity user);

  void deleteById(String uid);

  Oauth2UserEntity changePwd(String uid, String newPwd);

  Oauth2UserEntity findByUsername(String username);

  Oauth2UserEntity findByIdentified(String identified);

  Oauth2UserEntity findByEmail(String email);

  /**
   * 验证登录
   *
   * @param password 密码
   * @param salt 盐
   * @param encryptpwd 加密后的密码
   * @return boolean
   */
  boolean checkUser(String password, String salt, String encryptpwd);

  /**
   * 验证用户名是否存在
   *
   * @param username 用户名
   */
  boolean checkUsername(String username);

  /**
   * 验证邮箱是否存在
   *
   * @param email 邮箱
   */
  boolean checkEmail(String email);
}
