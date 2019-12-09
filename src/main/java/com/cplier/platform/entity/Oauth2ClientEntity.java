package com.cplier.platform.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "oauth2_client")
@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
public class Oauth2ClientEntity extends AuditModel {

  @Id
  @GeneratedValue(generator = "uuid2")
  @Column(name = "`client_id`", unique = true, length = 64, nullable = false)
  private String clientId;

  @Column(name = "`client_secret`", unique = true, length = 64, nullable = false)
  private String clientSecret;

  @Column(name = "`client_name`", length = 100)
  private String clientName;
}
