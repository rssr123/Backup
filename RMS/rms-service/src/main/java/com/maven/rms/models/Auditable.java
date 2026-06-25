package com.maven.rms.models;

import java.time.LocalDateTime;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U> {
    @CreatedBy
    protected U auditCreatedBy;

    @CreatedDate
    protected LocalDateTime auditCreatedDate;

    @LastModifiedBy
    protected U lastModifiedBy;

    @LastModifiedDate
    protected LocalDateTime lastModifiedDate;

    // public U getAuditCreatedBy() {
    //     return auditCreatedBy;
    // }

    // public void setAuditCreatedBy(U auditCreatedBy) {
    //     this.auditCreatedBy = auditCreatedBy;
    // }

    // public LocalDateTime getAuditCreatedDate() {
    //     return auditCreatedDate;
    // }

    // public void setAuditCreatedDate(LocalDateTime auditCreatedDate) {
    //     this.auditCreatedDate = auditCreatedDate;
    // }

    // public U getLastModifiedBy() {
    //     return lastModifiedBy;
    // }

    // public void setLastModifiedBy(U lastModifiedBy) {
    //     this.lastModifiedBy = lastModifiedBy;
    // }

    // public LocalDateTime getLastModifiedDate() {
    //     return lastModifiedDate;
    // }

    // public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
    //     this.lastModifiedDate = lastModifiedDate;
    // }
}
