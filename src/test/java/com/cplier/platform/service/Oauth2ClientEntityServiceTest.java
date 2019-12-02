package com.cplier.platform.service;

import com.cplier.platform.entity.Oauth2ClientEntity;
import com.cplier.platform.repository.Oauth2ClientRepository;
import com.cplier.platform.service.impl.Oauth2ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Oauth2ClientEntityServiceTest {

  private Oauth2ClientEntity oauth2ClientEntity;
  private List<Oauth2ClientEntity> oauth2ClientEntityList;

  @Mock Oauth2ClientRepository oauth2ClientRepository;

  @InjectMocks Oauth2ClientService oauth2ClientService = new Oauth2ClientServiceImpl();

  @BeforeEach
  void setUp() {
    oauth2ClientEntity = new Oauth2ClientEntity();
    oauth2ClientEntityList = new LinkedList<>();

    oauth2ClientEntityList.add(oauth2ClientEntity);

    when(oauth2ClientRepository.findAll()).thenReturn(oauth2ClientEntityList);
    when(oauth2ClientRepository.findById(any())).thenReturn(Optional.of(oauth2ClientEntity));
    when(oauth2ClientRepository.findByClientSecret(any()))
        .thenReturn(Optional.of(oauth2ClientEntity));
    when(oauth2ClientRepository.saveAndFlush(oauth2ClientEntity)).thenReturn(oauth2ClientEntity);
    doNothing().when(oauth2ClientRepository).deleteById(any());
  }

  @DisplayName("查找所有客户端信息")
  @Test
  void findAll() {
    assertEquals(oauth2ClientEntityList, oauth2ClientService.findAll());
  }

  @DisplayName("查找特定client_id")
  @Test
  void findByClientId() {
    assertEquals(oauth2ClientEntity, oauth2ClientService.findByClientId(""));
  }

  @DisplayName("查找特定client_secret")
  @Test
  void findByClientSecret() {
    assertEquals(oauth2ClientEntity, oauth2ClientService.findByClientSecret(""));
  }

  @DisplayName("更新或新增一条client信息")
  @Test
  void saveOrUpdate() {
    assertEquals(oauth2ClientEntity, oauth2ClientService.saveOrUpdate(oauth2ClientEntity));
  }

  @DisplayName("删除对应某一client_id记录")
  @Test
  void deleteByClientId() {
    oauth2ClientService.deleteByClientId("");
    verify(oauth2ClientRepository).deleteById(any());
  }
}
