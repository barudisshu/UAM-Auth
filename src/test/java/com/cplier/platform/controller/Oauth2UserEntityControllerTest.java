package com.cplier.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cplier.platform.entity.Oauth2UserEntity;
import com.cplier.platform.service.Oauth2UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.util.LinkedList;
import java.util.List;

import static com.cplier.platform.common.Result.fail;
import static com.cplier.platform.common.Result.success;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(Oauth2UserController.class)
class Oauth2UserEntityControllerTest {

  @MockBean
  Oauth2UserService oauth2UserService;
  @Resource ObjectMapper objectMapper;
  @Resource
  MockMvc mockMvc;

  private static Oauth2UserEntity oauth2UserEntity;
  private static List<Oauth2UserEntity> oauth2UserEntityList;

  @BeforeEach
  void setUp() {
    oauth2UserEntity = new Oauth2UserEntity();
    oauth2UserEntityList = new LinkedList<>();
    oauth2UserEntityList.add(oauth2UserEntity);
  }

  @DisplayName("查找所有User信息")
  @Test
  void list() throws Exception {
    given(oauth2UserService.findAll()).willReturn(oauth2UserEntityList);
    this.mockMvc
        .perform(get("/user"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(success(oauth2UserEntityList))));
  }

  @DisplayName("新增一条User")
  @Test
  void create() throws Exception {
    given(oauth2UserService.saveOrUpdate(oauth2UserEntity)).willReturn(oauth2UserEntity);
    this.mockMvc
        .perform(put("/user"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(success())));
  }

  @DisplayName("新增一条User[失败]")
  @Test
  void createFail() throws Exception {
    given(oauth2UserService.saveOrUpdate(oauth2UserEntity)).willThrow(EntityNotFoundException.class);
    this.mockMvc
        .perform(put("/user"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().json(objectMapper.writeValueAsString(fail())));
  }

  @DisplayName("根据uid查找一条记录")
  @Test
  void find() throws Exception {
    given(oauth2UserService.findById("uid")).willReturn(oauth2UserEntity);
    this.mockMvc
        .perform(get("/user/uid"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(success(oauth2UserEntity))));
  }

  @DisplayName("更新或新增一条user信息")
  @Test
  void update() throws Exception {
    given(oauth2UserService.saveOrUpdate(oauth2UserEntity)).willReturn(oauth2UserEntity);
    this.mockMvc
        .perform(post("/user"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(success())));
  }

  @DisplayName("更新或新增一条user信息[失败]")
  @Test
  void updateFail() throws Exception {
    given(oauth2UserService.saveOrUpdate(oauth2UserEntity)).willThrow(EntityNotFoundException.class);
    this.mockMvc
        .perform(post("/user"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().json(objectMapper.writeValueAsString(fail())));
  }

  @DisplayName("根据uid进行删除")
  @Test
  void deleteByUid() throws Exception {
    doNothing().when(oauth2UserService).deleteById("uid");
    this.mockMvc
        .perform(delete("/user/uid"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(success())));
  }

  @DisplayName("根据uid进行删除[失败]")
  @Test
  void deleteByUidFail() throws Exception {
    doNothing().when(oauth2UserService).deleteById("uid");
    this.mockMvc
        .perform(delete("/user/uid"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().json(objectMapper.writeValueAsString(fail())));
  }

  @DisplayName("更改对应用户ID的密码")
  @Test
  void changePassword() throws Exception {
    given(oauth2UserService.changePwd("uid", "newPwd")).willReturn(oauth2UserEntity);
    this.mockMvc
        .perform(post("/user/uid/change_password").param("newPassword", "newPwd"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(success())));
  }

  @DisplayName("更改对应用户ID的密码[失败]")
  @Test
  void changePasswordFail() throws Exception {
    given(oauth2UserService.changePwd("uid", "newPwd")).willThrow(EntityNotFoundException.class);
    this.mockMvc
        .perform(post("/user/uid/change_password").param("newPassword", "newPwd"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().json(objectMapper.writeValueAsString(fail())));
  }
}
