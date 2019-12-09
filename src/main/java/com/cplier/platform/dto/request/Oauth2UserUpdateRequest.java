package com.cplier.platform.dto.request;

import com.cplier.platform.entity.Oauth2UserEntity;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class Oauth2UserUpdateRequest implements Serializable, Cloneable {

  private String name;
  private String avatar;
  private Integer status;
  private String desc;
  private String company;
  private String location;
  private String website;

  public Oauth2UserEntity wrap(Oauth2UserEntity entity) {

    if (StringUtils.isNotBlank(name)) entity.setName(name);
    if (StringUtils.isNotBlank(avatar)) entity.setAvatar(avatar);
    if (status != null) entity.setStatus(status);
    if (StringUtils.isNotBlank(desc)) entity.setAvatar(desc);
    if (StringUtils.isNotBlank(company)) entity.setAvatar(company);
    if (StringUtils.isNotBlank(location)) entity.setAvatar(location);
    if (StringUtils.isNotBlank(website)) entity.setAvatar(website);

    return entity;
  }
}
