package com.cplier.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cplier.platform.entity.Oauth2ClientEntity;
import com.cplier.platform.service.Oauth2ClientService;
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
@WebMvcTest(Oauth2ClientController.class)
class Oauth2ClientEntityControllerTest {

  @MockBean
  Oauth2ClientService oauth2ClientService;
  @Resource ObjectMapper objectMapper;
  @Resource
  MockMvc mockMvc;

  private static Oauth2ClientEntity oauth2ClientEntity;
  private static List<Oauth2ClientEntity> oauth2ClientEntityList;

  @BeforeEach
  void setUp() {
    oauth2ClientEntity = new Oauth2ClientEntity();
    oauth2ClientEntityList = new LinkedList<>();
    oauth2ClientEntityList.add(oauth2ClientEntity);
  }

  @DisplayName("查找所有Client信息")
  @Test
  void list() throws Exception {
    given(oauth2ClientService.findAll()).willReturn(oauth2ClientEntityList);
    this.mockMvc
        .perform(get("/client"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(success())));
  }

  @DisplayName("新增一条Client")
  @Test
  void create() throws Exception {
    given(oauth2ClientService.saveOrUpdate(oauth2ClientEntity)).willReturn(oauth2ClientEntity);
    this.mockMvc
        .perform(put("/client"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(success())));
  }

  @DisplayName("新增一条Client[失败]")
  @Test
  void createFail() throws Exception {
    given(oauth2ClientService.saveOrUpdate(oauth2ClientEntity)).willReturn(oauth2ClientEntity);
    this.mockMvc
        .perform(put("/client"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().json(objectMapper.writeValueAsString(fail())));
  }

  @DisplayName("根据client_id查找一条记录")
  @Test
  void find() throws Exception {
    given(oauth2ClientService.findByClientId("client_id")).willReturn(oauth2ClientEntity);
    this.mockMvc
        .perform(get("/client/clientId"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(success(oauth2ClientEntity))));
  }

  @DisplayName("更新或新增一条client信息")
  @Test
  void update() throws Exception {
    given(oauth2ClientService.saveOrUpdate(oauth2ClientEntity)).willReturn(oauth2ClientEntity);
    this.mockMvc
        .perform(post("/client"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(success())));
  }

  @DisplayName("更新或新增一条client信息[失败]")
  @Test
  void updateFail() throws Exception {
    given(oauth2ClientService.saveOrUpdate(oauth2ClientEntity)).willThrow(EntityNotFoundException.class);
    this.mockMvc
        .perform(post("/client"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().json(objectMapper.writeValueAsString(fail())));
  }

  @DisplayName("根据client_id进行删除")
  @Test
  void deleteByClientId() throws Exception {
    doNothing().when(oauth2ClientService).deleteByClientId("client_id");
    this.mockMvc
        .perform(delete("/client/clientId"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(success())));
  }

  @DisplayName("根据client_id删除[失败]")
  @Test
  void deleteByClientIdFail() throws Exception {
    doNothing().when(oauth2ClientService).deleteByClientId("client_id");
    this.mockMvc
        .perform(delete("/client/clientId"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().json(objectMapper.writeValueAsString(fail())));
  }
}
