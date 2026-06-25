package com.maven.rms.interfaces;

// import java.math.BigInteger;
// import java.util.Date;
import java.util.List;

import com.maven.rms.models.CheckRoleRequest;
import com.maven.rms.models.RolePermissionGetRequest;
import com.maven.rms.models.RolePermissionRequest;
import com.maven.rms.models.RoleRequest;
import com.maven.rms.models.UserRoleRequest;

public interface IRoleAndPermissionsConfigurationInterface {
        // #region UserRole
        List<Object[]> sp_getuseranduserroles(UserRoleRequest userRoleRequest);

        List<Object[]> sp_getroles(RoleRequest roleRequest);

        // #endregion

        // region role and permissions configurations
        Integer sp_insroles(RoleRequest roleRequest);

        List<Object[]> sp_getpermissions();

        Integer sp_updrolestatus(RoleRequest roleRequest);

        // Integer sp_insroleperm(RolePermissionRequest rolePermissionRequest);

        Integer sp_insroleperm(List<RolePermissionRequest> rolePermissionRequest);

        Integer sp_insroleperm_v2(List<RolePermissionRequest> rolePermissionRequest);

        List<Object[]> sp_getpermissionsbyid();

        Integer sp_delroleperm(RolePermissionRequest rolePermissionRequest);

        List<Object[]> sp_getroleperm(RolePermissionGetRequest rolePermissionGetRequest);

        String sp_checkuserrole(CheckRoleRequest checkRoleRequest);
}
