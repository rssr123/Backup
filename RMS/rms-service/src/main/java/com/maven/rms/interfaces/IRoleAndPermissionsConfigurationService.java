package com.maven.rms.interfaces;

// import java.math.BigInteger;
// import java.util.Date;
import java.util.List;
import com.maven.rms.models.CheckRoleRequest;
import com.maven.rms.models.Permission;
import com.maven.rms.models.PermissionByID;
import com.maven.rms.models.RolePermissionGet;
import com.maven.rms.models.RolePermissionGetRequest;
import com.maven.rms.models.RolePermissionRequest;
import com.maven.rms.models.RoleRequest;
import com.maven.rms.models.Role;
import com.maven.rms.models.UserRole;
import com.maven.rms.models.UserRoleRequest;

public interface IRoleAndPermissionsConfigurationService {

    List<UserRole> sp_getuseranduserroles(UserRoleRequest userRoleRequest);

    List<Role> sp_getroles(RoleRequest roleRequest);

    Integer sp_insroles(RoleRequest roleRequest);

    List<Permission> sp_getpermissions();

    Integer sp_updrolestatus(RoleRequest roleRequest);

    // Integer sp_insroleperm(RolePermissionRequest rolePermissionRequest);

    Integer sp_insroleperm(List<RolePermissionRequest> rolePermissionRequest);

    Integer sp_insroleperm_v2(List<RolePermissionRequest> rolePermissionRequest);

    List<PermissionByID> sp_getpermissionsbyid();

    Integer sp_delroleperm(RolePermissionRequest rolePermissionRequest);

    List<RolePermissionGet> sp_getroleperm(RolePermissionGetRequest rolePermissionGetRequest);

    String sp_checkuserrole(CheckRoleRequest checkRoleRequest);

}
