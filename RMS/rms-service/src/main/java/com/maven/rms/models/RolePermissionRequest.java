package com.maven.rms.models;

import java.math.BigInteger;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionRequest {

    private BigInteger i_role_perm_id;

    private Integer i_role_id;

    private Integer i_perm_id;

    private Integer i_is_allow;

    private Date i_dt_created;

    private Date i_dt_modified;

    private String i_created_by;

    private String i_modified_by;

    private String i_status;

}
