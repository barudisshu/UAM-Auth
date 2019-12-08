package com.cplier.platform.controller;

import com.cplier.platform.common.Result;
import com.cplier.platform.dto.request.Oauth2ClientCreateRequest;
import com.cplier.platform.dto.request.Oauth2ClientUpdateRequest;
import com.cplier.platform.entity.Oauth2ClientEntity;
import com.cplier.platform.service.Oauth2ClientService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("client")
@Api(tags = "client相关")
public class Oauth2ClientController {

  @Resource private Oauth2ClientService oauth2ClientService;

  @ApiOperation(value = "列出所有的client信息", notes = "获取客户端列表", produces = "application/json")
  @ApiResponses({
    @ApiResponse(code = 200, message = "成功", response = Result.class),
    @ApiResponse(code = 404, message = "失败", response = Result.class)
  })
  @GetMapping
  public Result list() {
    List<Oauth2ClientEntity> oauth2ClientEntityList = oauth2ClientService.findAll();
    return Result.success(oauth2ClientEntityList);
  }

  @ApiOperation(value = "新增一条记录", notes = "新增", produces = "application/json")
  @ApiResponses({
    @ApiResponse(code = 200, message = "成功", response = Result.class),
    @ApiResponse(code = 404, message = "失败", response = Result.class)
  })
  @PutMapping
  public Result create(@ApiParam("客户端名") @RequestBody Oauth2ClientCreateRequest request) {
    oauth2ClientService.saveOrUpdate(request.mapping());
    return Result.success(request);
  }

  @ApiOperation(value = "获取client信息", notes = "查找", produces = "application/json")
  @ApiResponses({
    @ApiResponse(code = 200, message = "成功", response = Result.class),
    @ApiResponse(code = 404, message = "失败", response = Result.class)
  })
  @GetMapping("{clientId}")
  public Result find(@ApiParam("客户端id") @PathVariable("clientId") String clientId) {
    return Result.success(oauth2ClientService.findByClientId(clientId));
  }

  @ApiOperation(value = "更新client信息", notes = "修改对应clientId的名称", produces = "application/json")
  @ApiResponses({
    @ApiResponse(code = 200, message = "成功", response = Result.class),
    @ApiResponse(code = 404, message = "失败", response = Result.class)
  })
  @PostMapping("{clientId}")
  public Result update(
      @ApiParam("客户端id") @PathVariable("clientId") String clientId,
      @ApiParam("json") @RequestBody Oauth2ClientUpdateRequest request) {
    Oauth2ClientEntity entity = oauth2ClientService.findByClientId(clientId);
    entity.setClientName(request.getClientName());
    oauth2ClientService.saveOrUpdate(entity);
    return Result.success(entity);
  }

  @ApiOperation(value = "删除一条client", notes = "删除", produces = "application/json")
  @ApiResponses({
    @ApiResponse(code = 200, message = "成功", response = Result.class),
    @ApiResponse(code = 404, message = "失败", response = Result.class)
  })
  @DeleteMapping("{clientId}")
  public Result deleteByClientId(@ApiParam("客户端id") @PathVariable("clientId") String clientId) {
    oauth2ClientService.deleteByClientId(clientId);
    return Result.success();
  }
}
