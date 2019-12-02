package com.cplier.platform.service;

import com.cplier.platform.component.PasswordHelper;
import com.cplier.platform.entity.Oauth2UserEntity;
import com.cplier.platform.repository.Oauth2UserRepository;
import com.cplier.platform.service.impl.Oauth2UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Oauth2UserEntityServiceTest {

  private Oauth2UserEntity oauth2UserEntity;
  private List<Oauth2UserEntity> oauth2UserEntityList;

  @Mock Oauth2UserRepository oauth2UserRepository;
  @Mock PasswordHelper passwordHelper;

  @InjectMocks Oauth2UserService oauth2UserService = new Oauth2UserServiceImpl();

  @BeforeEach
  void setUp() {
    oauth2UserEntity = new Oauth2UserEntity();
    oauth2UserEntity.setUsername("Peter");
    oauth2UserEntity.setPassword("passW0rd");
    oauth2UserEntity.setSalt("S@lt");
    oauth2UserEntityList = new LinkedList<>();

    oauth2UserEntityList.add(oauth2UserEntity);

    ReflectionTestUtils.setField(passwordHelper, "algorithmName", "md5");

    when(oauth2UserRepository.findAll()).thenReturn(oauth2UserEntityList);
    when(oauth2UserRepository.findById(any())).thenReturn(Optional.of(oauth2UserEntity));
    when(oauth2UserRepository.saveAndFlush(any())).thenReturn(oauth2UserEntity);
    doNothing().when(oauth2UserRepository).deleteById(any());
    when(oauth2UserRepository.findByUsername(any())).thenReturn(Optional.of(oauth2UserEntity));
    when(passwordHelper.encryptPassword(
            oauth2UserEntity.getUsername(),
            oauth2UserEntity.getPassword(),
            oauth2UserEntity.getSalt()))
        .thenCallRealMethod();
  }

  @DisplayName("查找所有用户")
  @Test
  void findAll() {
    assertEquals(oauth2UserEntityList, oauth2UserService.findAll());
  }

  @DisplayName("查找特定用户信息")
  @Test
  void findById() {
    assertEquals(oauth2UserEntity, oauth2UserService.findById(""));
  }

  @DisplayName("新增或更新用户")
  @Test
  void saveOrUpdate() {
    assertEquals(oauth2UserEntity, oauth2UserService.saveOrUpdate(oauth2UserEntity));
  }

  @DisplayName("删除特定用户")
  @Test
  void deleteById() {
    oauth2UserService.deleteById("");
    verify(oauth2UserRepository).deleteById(any());
  }

  @DisplayName("更新特定用户的密码")
  @Test
  void changePwd() {
    assertEquals(oauth2UserEntity, oauth2UserService.changePwd("", "pwd"));
  }

  @DisplayName("查找特定用户名")
  @Test
  void selectByUsername() {
    assertEquals(oauth2UserEntity, oauth2UserService.findByUsername(""));
  }

  @DisplayName("检测用户登录账号信息")
  @Test
  void checkUser() {
    boolean valid =
        oauth2UserService.checkUser(
            oauth2UserEntity.getUsername(),
            oauth2UserEntity.getPassword(),
            oauth2UserEntity.getSalt(),
            "052dd288ba734312bdc0d800f21c85f0");
    assertTrue(valid);
  }
}
