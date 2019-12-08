package com.cplier.platform.controller;

import com.cplier.platform.common.Result;
import com.cplier.platform.dto.request.Oauth2UserCreateRequest;
import com.cplier.platform.dto.request.Oauth2UserUpdateRequest;
import com.cplier.platform.entity.Oauth2UserEntity;
import com.cplier.platform.service.Oauth2UserService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
@Api(tags = "user相关")
public class Oauth2UserController {
  @Resource private Oauth2UserService oauth2UserService;

  @ApiOperation(value = "列出所有的user信息", notes = "获取客户端列表", produces = "application/json")
  @ApiResponses({
    @ApiResponse(code = 200, message = "成功", response = Result.class),
    @ApiResponse(code = 404, message = "失败", response = Result.class)
  })
  @GetMapping
  public Result list() {
    return Result.success(oauth2UserService.findAll());
  }

  @ApiOperation(value = "新增一条记录", notes = "新增", produces = "application/json")
  @ApiResponses({
    @ApiResponse(code = 200, message = "成功", response = Result.class),
    @ApiResponse(code = 404, message = "失败", response = Result.class)
  })
  @PutMapping
  public Result create(@ApiParam("用户信息") @RequestBody Oauth2UserCreateRequest request) {
    Oauth2UserEntity entity = request.mapping();
    oauth2UserService.saveOrUpdate(entity);
    return Result.success(entity);
  }

  @ApiOperation(value = "获取user信息", notes = "查找", produces = "application/json")
  @ApiResponses({
    @ApiResponse(code = 200, message = "成功", response = Result.class),
    @ApiResponse(code = 404, message = "失败", response = Result.class)
  })
  @GetMapping("{uid}")
  public Result find(@ApiParam("用户id") @PathVariable("uid") String uid) {
    return Result.success(oauth2UserService.findById(uid));
  }

  @ApiOperation(value = "更新user信息", notes = "修改对应uid的名称", produces = "application/json")
  @ApiResponses({
    @ApiResponse(code = 200, message = "成功", response = Result.class),
    @ApiResponse(code = 404, message = "失败", response = Result.class)
  })
  @PostMapping("{uid}")
  public Result update(
      @ApiParam("uid") @PathVariable("uid") String uid,
      @ApiParam("json") @RequestBody Oauth2UserUpdateRequest request) {
    Oauth2UserEntity entity = oauth2UserService.findById(uid);
    entity.setUsername(request.getUsername());
    oauth2UserService.saveOrUpdate(entity);
    return Result.success(entity);
  }

  @ApiOperation(value = "删除一条user", notes = "删除", produces = "application/json")
  @ApiResponses({
    @ApiResponse(code = 200, message = "成功", response = Result.class),
    @ApiResponse(code = 404, message = "失败", response = Result.class)
  })
  @DeleteMapping("{uid}")
  public Result deleteByUid(@ApiParam("uid") @PathVariable("uid") String uid) {
    oauth2UserService.deleteById(uid);
    return Result.success();
  }

  @ApiOperation(value = "修改密码", notes = "改密码", produces = "application/json")
  @ApiResponses({
    @ApiResponse(code = 200, message = "成功", response = Result.class),
    @ApiResponse(code = 404, message = "失败", response = Result.class)
  })
  @PostMapping("/{uid}/changePassword")
  public Result changePassword(
      @ApiParam("uid") @PathVariable("uid") String uid, String newPassword) {
    oauth2UserService.changePwd(uid, newPassword);
    return Result.success();
  }
}
