package com.cplier.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@JsonIgnoreProperties(value = {"updatedTime", "createdTime"}, allowGetters = true)
@Getter
@Setter
public abstract class AuditModel extends BaseEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time", columnDefinition = "timestamp default now() on update now()", nullable = false)
    @LastModifiedDate
    private Date updatedTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", columnDefinition = "timestamp default now()", nullable = false, updatable = false)
    @CreatedDate
    private Date createdTime;
}