package com.cplier.platform.service.impl;

import com.cplier.platform.entity.Oauth2ClientEntity;
import com.cplier.platform.exception.DuplicatedException;
import com.cplier.platform.exception.EntityNotExistsException;
import com.cplier.platform.repository.Oauth2ClientRepository;
import com.cplier.platform.service.Oauth2ClientService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@CacheConfig(cacheNames = {"clients"})
public class Oauth2ClientServiceImpl implements Oauth2ClientService {

  @Resource private Oauth2ClientRepository oauth2ClientRepository;

  @Cacheable
  @Override
  public List<Oauth2ClientEntity> findAll() {
    return oauth2ClientRepository.findAll();
  }

  @Cacheable(key = "#clientId")
  @Override
  public Oauth2ClientEntity findByClientId(String clientId) {
    return oauth2ClientRepository
        .findById(clientId)
        .orElseThrow(
            () ->
                new EntityNotExistsException(
                    String.format("the clientId=%s does not exists", clientId)));
  }

  @Override
  public Oauth2ClientEntity findByClientSecret(String clientSecret) {
    return oauth2ClientRepository
        .findByClientSecret(clientSecret)
        .orElseThrow(
            () ->
                new EntityNotExistsException(
                    String.format("the clientSecret=%s does not exists", clientSecret)));
  }

  /**
   * 更新时，清空所有缓存数据
   *
   * @param client 插入或更新的数据
   * @return 插入或更新后的数据
   */
  @Caching(
      evict = {
        @CacheEvict(key = "#client.clientId", allEntries = true)
      })
  @Override
  public Oauth2ClientEntity saveOrUpdate(Oauth2ClientEntity client) {
      Optional<Oauth2ClientEntity> bff = oauth2ClientRepository.findByClientName(client.getClientName());
      if (bff.isPresent()) throw new DuplicatedException("客户端名重复");
      return oauth2ClientRepository.saveAndFlush(client);
  }

  /**
   * 删除时，删除缓存数据
   *
   * @param clientId clientId
   */
  @CacheEvict(key = "#clientId", allEntries = true)
  @Override
  public void deleteByClientId(String clientId) {
      try {
          oauth2ClientRepository.deleteById(clientId);
      } catch (EmptyResultDataAccessException e) {
          throw new EntityNotExistsException(e.getMessage(), "没有这个clientId");
      }
  }
}
