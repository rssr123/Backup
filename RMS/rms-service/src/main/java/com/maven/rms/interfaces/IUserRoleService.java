package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRequest;

public interface IUserRoleService {
    List<RMSUser> sp_getuserbyrole(RMSUserRequest rmsUserRequest);
      RMSUser sp_getuserdetail(RMSUserRequest rmsUserRequest);
}
