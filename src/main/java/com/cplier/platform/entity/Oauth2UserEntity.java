package com.cplier.platform.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "oauth2_user")
@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
public class Oauth2UserEntity extends AuditModel {

  @Id
  @GeneratedValue(generator = "uuid2")
  @Column(name = "`uid`", unique = true, length = 64, nullable = false)
  private String uid;

  @Column(name = "`name`", length = 100, nullable = false)
  private String name;

  @Column(name = "`username`", unique = true, length = 100, nullable = false)
  private String username;

  @Column(name = "`password`", length = 100, nullable = false)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @Column(name = "`salt`", length = 100, nullable = false)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String salt;

  @Column(name = "`email`", length = 100, nullable = false)
  private String email;

  @Column(name = "`avatar`")
  private String avatar;

  @Column(name = "`status`", nullable = false)
  private int status;

  @Column(name = "`desc`")
  private String desc;

  @Column(name = "`company`")
  private String company;

  @Column(name = "`location`")
  private String location;

  @Column(name = "`website`")
  private String website;

  @Column(name = "`joined_at`")
  private Date joinedAt;
}
