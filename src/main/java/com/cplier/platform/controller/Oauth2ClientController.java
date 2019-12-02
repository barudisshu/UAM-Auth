package com.cplier.platform.controller;

import com.cplier.platform.common.Result;
import com.cplier.platform.entity.Oauth2ClientEntity;
import com.cplier.platform.service.Oauth2ClientService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("client")
public class Oauth2ClientController {

  @Resource private Oauth2ClientService oauth2ClientService;

  @GetMapping
  public Result list() {
    List<Oauth2ClientEntity> oauth2ClientEntityList = oauth2ClientService.findAll();
    return Result.success(oauth2ClientEntityList);
  }

  @PutMapping
  public Result create(Oauth2ClientEntity oauth2ClientEntity) {
    oauth2ClientService.saveOrUpdate(oauth2ClientEntity);
    return Result.success(oauth2ClientEntity);
  }

  @GetMapping("{client_id}")
  public Result find(@PathVariable("client_id") String clientId) {
    Oauth2ClientEntity oauth2ClientEntity = oauth2ClientService.findByClientId(clientId);
    return Result.success(oauth2ClientEntity);
  }

  @PostMapping
  public Result update(Oauth2ClientEntity client) {
    oauth2ClientService.saveOrUpdate(client);
    return Result.success(client);
  }

  @DeleteMapping("{client_id}")
  public Result deleteByClientId(@PathVariable("client_id") String clientId) {
    oauth2ClientService.deleteByClientId(clientId);
    return Result.success();
  }
}
