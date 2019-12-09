package com.cplier.platform.component;

import com.cplier.platform.entity.Oauth2UserEntity;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PasswordHelperTest {

  @InjectMocks PasswordHelper passwordHelper;
  private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
  private Oauth2UserEntity oauth2UserEntity;
  private static final String username = "peter";
  private static final String password = "Passw0rd";
  private static final String salt = "efa9298b080c4068a170a0f7fa52d194";
  private static final String actualEncryptPwd = "8470bb2001cafa3e914e090a29e81192";

  @BeforeEach
  void setUp() {
    passwordHelper.setRandomNumberGenerator(randomNumberGenerator);
    passwordHelper.setAlgorithmName("md5");
    passwordHelper.setHashIterations(2);

    oauth2UserEntity = new Oauth2UserEntity();
    oauth2UserEntity.setUsername(username);
    oauth2UserEntity.setPassword(password);
    oauth2UserEntity.setSalt(salt);
  }

  @DisplayName("随机加盐加密")
  @Test
  void encryptPassword() {
    passwordHelper.encryptPassword(oauth2UserEntity);
    assertEquals(32, oauth2UserEntity.getPassword().length());
  }

  @DisplayName("校验加密信息")
  @Test
  void testEncryptPassword() {
    String actualEncyptPwd = passwordHelper.encryptPassword(password, salt);
    assertEquals(actualEncryptPwd, actualEncyptPwd);
  }
}
