package com.maven.rms.services;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IRoleAndPermissionsConfigurationService;
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
import com.maven.rms.repositories.IRoleAndPermissionsConfigurationRepository;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.RICPRepository;

@Service
@Slf4j
public class RoleAndPermissionsConfigurationService implements IRoleAndPermissionsConfigurationService {

    // private static final Logger logger =
    // LoggerFactory.getLogger(StoreProcedureService.class);
    private final IRoleAndPermissionsConfigurationRepository storeProcedureRepository;
    // private final MTTRCPTRepository mttrcptRepository;
    // private final RICPRepository ricpRepository;

    public RoleAndPermissionsConfigurationService(IRoleAndPermissionsConfigurationRepository storeProcedureRepository,
            MTTRCPTRepository mttrcptRepository, RICPRepository ricpRepository) {
        this.storeProcedureRepository = storeProcedureRepository;
        // this.mttrcptRepository=mttrcptRepository;
        // this.ricpRepository=ricpRepository;
    }

    // #regionUserRole
    private List<UserRole> convertUserRoleList(List<Object[]> objects) {
        List<UserRole> userRolesList = new ArrayList<>();

        for (Object[] obj : objects) {
            UserRole userRole = new UserRole();
            userRole.setUserRef((String) obj[0]);
            userRole.setName((String) obj[1]);
            userRole.setEmail((String) obj[2]);
            userRole.setCreatedBy((String) obj[3]);
            userRole.setModifiedBy((String) obj[4]);
            userRole.setStatus((String) obj[5]);
            userRole.setRole_nm_en((String) obj[6]);
            userRole.setTotalRoles((Integer) obj[7]);

            userRolesList.add(userRole);
        }

        return userRolesList;
    }

    // @Override
    // public List<UserRole> sp_getuseranduserroles(UserRoleRequest userRoleRequest)
    // {
    // List<UserRole> result = Collections.emptyList();

    // try {
    // List<Object[]> objects =
    // storeProcedureRepository.sp_getuseranduserroles(userRoleRequest);
    // result = convertUserRoleList(objects);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    @Override
    public List<UserRole> sp_getuseranduserroles(UserRoleRequest userRoleRequest) {
        List<UserRole> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getuseranduserroles(userRoleRequest);
        result = convertUserRoleList(objects);
        return result;
    }

    // @Override
    // public List<Role> sp_getroles(RoleRequest roleRequest) {
    // List<Role> result = Collections.emptyList();

    // try {
    // List<Object[]> objects = storeProcedureRepository.sp_getroles(roleRequest);
    // result = convertToGetRole(objects);

    // } catch (Exception e) {
    // e.printStackTrace();

    // }

    // return result;
    // }

    @Override
    public List<Role> sp_getroles(RoleRequest roleRequest) {
        List<Role> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getroles(roleRequest);
        result = convertToGetRole(objects);
        return result;
    }

    private List<Role> convertToGetRole(List<Object[]> objects) {
        List<Role> roleList = new ArrayList<>();

        for (Object[] obj : objects) {

            // Create a new Role instance using the extracted values
            Role role = new Role();
            role.setRoleId((BigInteger) obj[0]);
            role.setRoleNmEn((String) obj[1]);
            role.setRoleNmBm((String) obj[2]);
            role.setDtModified((Date) obj[3]);
            role.setModifiedBy((String) obj[4]);
            role.setStatus((String) obj[5]);

            roleList.add(role);
        }
        return roleList;
    }

    // @Override
    // public Integer sp_insroles(RoleRequest roleRequest) {
    // Integer result = 0;
    // try {
    // result = storeProcedureRepository.sp_insroles(roleRequest);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return result;
    // }

    @Override
    public Integer sp_insroles(RoleRequest roleRequest) {
        Integer result = 0;
        result = storeProcedureRepository.sp_insroles(roleRequest);
        return result;
    }

    private List<Permission> convertPermissionList(List<Object[]> objects) {
        List<Permission> permissionList = new ArrayList<>();

        for (Object[] obj : objects) {
            Permission permission = new Permission();
            // permission.setPerm_cd((String) obj[0]);
            permission.setModule_nm((String) obj[0]);
            permission.setFunction_nm((String) obj[1]);
            permission.setStatus((String) obj[2]);
            permission.setTotal((Integer) obj[3]);

            permissionList.add(permission);
        }

        return permissionList;
    }

    // @Override
    // public List<Permission> sp_getpermissions() {
    // List<Permission> result = Collections.emptyList();

    // try {
    // List<Object[]> objects = storeProcedureRepository.sp_getpermissions();
    // result = convertPermissionList(objects);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    @Override
    public List<Permission> sp_getpermissions() {
        List<Permission> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getpermissions();
        result = convertPermissionList(objects);
        return result;
    }

    // @Override
    // public Integer sp_updrolestatus(RoleRequest roleRequest) {
    // Integer result = 0;
    // try {
    // result = storeProcedureRepository.sp_updrolestatus(roleRequest);
    // } catch (Exception e) {
    // log.error("Error calling stored procedure sp_updrolestatus with parameters:
    // {}",
    // roleRequest, e);
    // throw e; // rethrow to allow further handling
    // // e.printStackTrace();
    // }
    // return result;
    // }

    @Override
    public Integer sp_updrolestatus(RoleRequest roleRequest) {
        Integer result = 0;
        result = storeProcedureRepository.sp_updrolestatus(roleRequest);
        return result;
    }

    // @Override
    // public Integer sp_insroleperm(RolePermissionRequest rolePermissionRequest) {

    // Integer result = 0;
    // try {
    // result = storeProcedureRepository.sp_insroleperm(rolePermissionRequest);
    // }
    // catch (Exception e) {
    // e.printStackTrace();
    // }
    // return result;
    // }

    // @Override
    // public Integer sp_insroleperm(RolePermissionRequest rolePermissionRequest) {

    // Integer result = 0;
    // result = storeProcedureRepository.sp_insroleperm(rolePermissionRequest);
    // return result;
    // }

    @Override
    public Integer sp_insroleperm(List<RolePermissionRequest> rolePermissionRequest) {

        Integer result = 0;
        result = storeProcedureRepository.sp_insroleperm(rolePermissionRequest);
        return result;
    }

    @Override
    public Integer sp_insroleperm_v2(List<RolePermissionRequest> rolePermissionRequest) {

        Integer result = 0;
        result = storeProcedureRepository.sp_insroleperm_v2(rolePermissionRequest);
        return result;
    }

    private List<PermissionByID> convertPermissionByIDList(List<Object[]> objects) {
        List<PermissionByID> permissionList = new ArrayList<>();

        for (Object[] obj : objects) {
            PermissionByID permission = new PermissionByID();
            permission.setPerm_id((Integer) obj[0]);
            permission.setPerm_cd((String) obj[1]);
            permission.setModule_nm((String) obj[2]);
            permission.setFunction_nm((String) obj[3]);
            permission.setStatus((String) obj[4]);
            permission.setTotal((Integer) obj[5]);

            permissionList.add(permission);
        }

        return permissionList;
    }

    // @Override
    // public List<PermissionByID> sp_getpermissionsbyid() {
    // List<PermissionByID> result = Collections.emptyList();

    // try {
    // List<Object[]> objects = storeProcedureRepository.sp_getpermissionsbyid();
    // result = convertPermissionByIDList(objects);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    @Override
    public List<PermissionByID> sp_getpermissionsbyid() {
        List<PermissionByID> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getpermissionsbyid();
        result = convertPermissionByIDList(objects);
        return result;
    }

    // @Override
    // public Integer sp_delroleperm(RolePermissionRequest rolePermissionRequest) {

    // Integer result = 0;
    // try {
    // result = storeProcedureRepository.sp_delroleperm(rolePermissionRequest);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return result;
    // }

    @Override
    public Integer sp_delroleperm(RolePermissionRequest rolePermissionRequest) {
        Integer result = 0;
        result = storeProcedureRepository.sp_delroleperm(rolePermissionRequest);
        return result;
    }

    // @Override
    // public List<RolePermissionGet> sp_getroleperm(RolePermissionGetRequest
    // rolePermissionGetRequest) {
    // List<RolePermissionGet> result = Collections.emptyList();
    // try {

    // List<Object[]> objects =
    // storeProcedureRepository.sp_getroleperm(rolePermissionGetRequest);
    // result = convertToGetRolePermissions(objects);

    // } catch (Exception e) {
    // e.printStackTrace();

    // }

    // return result;
    // }

    @Override
    public List<RolePermissionGet> sp_getroleperm(RolePermissionGetRequest rolePermissionGetRequest) {
        List<RolePermissionGet> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getroleperm(rolePermissionGetRequest);
        result = convertToGetRolePermissions(objects);
        return result;
    }

    private List<RolePermissionGet> convertToGetRolePermissions(List<Object[]> objects) {
        List<RolePermissionGet> rolePermList = new ArrayList<>();

        for (Object[] obj : objects) {

            // Create a new Role instance using the extracted values
            RolePermissionGet rolePerm = new RolePermissionGet();
            rolePerm.setPerm_id((Integer) obj[0]);
            rolePerm.setIs_allow((Integer) obj[1]);
            rolePerm.setStatus((String) obj[2]);
            rolePerm.setTotal((Integer) obj[3]);

            rolePermList.add(rolePerm);
        }
        return rolePermList;
    }

    // @Override
    // public String sp_checkuserrole(CheckRoleRequest checkRoleRequest) {
    // String result = "";
    // try {
    // result = storeProcedureRepository.sp_checkuserrole(checkRoleRequest);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return result;
    // }

    @Override
    public String sp_checkuserrole(CheckRoleRequest checkRoleRequest) {
        String result = "";
        result = storeProcedureRepository.sp_checkuserrole(checkRoleRequest);
        return result;
    }

}
