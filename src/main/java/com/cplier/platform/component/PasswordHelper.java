package com.cplier.platform.component;

import com.cplier.platform.entity.Oauth2UserEntity;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Component
public class PasswordHelper {

  private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

  @Value("${password.algorithmName:md5}")
  private String algorithmName = "md5";

  @Value("${password.hashIterations:2}")
  private int hashIterations = 2;

  public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
    this.randomNumberGenerator = randomNumberGenerator;
  }

  public void setAlgorithmName(String algorithmName) {
    this.algorithmName = algorithmName;
  }

  public void setHashIterations(int hashIterations) {
    this.hashIterations = hashIterations;
  }

  public void encryptPassword(@NotNull Oauth2UserEntity user) {
    user.setSalt(randomNumberGenerator.nextBytes().toHex());
    String newPassword =
        new SimpleHash(
                algorithmName,
                user.getPassword(),
                ByteSource.Util.bytes(user.getSalt()),
                hashIterations)
            .toHex();
    user.setPassword(newPassword);
  }

  /**
   * 根据用户名和盐值加密
   *
   * @param username 用户名
   * @param password 密码
   * @param salt 盐
   */
  public String encryptPassword(
      @NotBlank String username, @NotBlank String password, @NotBlank String salt) {
    return new SimpleHash(
            algorithmName, password, ByteSource.Util.bytes(salt), hashIterations)
        .toHex();
  }
}
