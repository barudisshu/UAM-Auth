package com.cplier.platform.controller;

import com.cplier.platform.common.Result;
import com.cplier.platform.entity.Oauth2UserEntity;
import com.cplier.platform.service.Oauth2UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
public class Oauth2UserController {
  @Resource private Oauth2UserService oauth2UserService;

  @GetMapping
  public Result list() {
    return Result.success(oauth2UserService.findAll());
  }

  @PutMapping
  public Result create(Oauth2UserEntity oauth2UserEntity) {
    oauth2UserService.saveOrUpdate(oauth2UserEntity);
    return Result.success(oauth2UserEntity);
  }

  @GetMapping("{uid}")
  public Result find(@PathVariable("uid") String uid) {
    Oauth2UserEntity oauth2UserEntity = oauth2UserService.findById(uid);
    return Result.success(oauth2UserEntity);
  }

  @PostMapping
  public Result update(Oauth2UserEntity oauth2UserEntity) {
    oauth2UserService.saveOrUpdate(oauth2UserEntity);
    return Result.success(oauth2UserEntity);
  }

  @DeleteMapping("{uid}")
  public Result deleteByUid(@PathVariable("uid") String uid) {
    oauth2UserService.deleteById(uid);
    return Result.success();
  }

  @PostMapping("/{uid}/change_password")
  public Result changePassword(@PathVariable("uid") String uid, String newPassword) {
    oauth2UserService.changePwd(uid, newPassword);
    return Result.success();
  }
}
