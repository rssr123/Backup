package com.maven.rms.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IUserRoleService;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRequest;
import com.maven.rms.repositories.IUserRoleRepository;

@Service
@Slf4j
public class UserRoleService implements IUserRoleService {
    //private static final Logger logger = LoggerFactory.getLogger(StoreProcedureService.class);
    private final IUserRoleRepository userRoleRepository;

    public UserRoleService(IUserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;

    }

    // @Override
    // public List<RMSUser> sp_getuserbyrole(RMSUserRequest rmsUserRequest) {

    //     List<RMSUser> result = Collections.emptyList();

    //     try {

    //         List<Object[]> objects = userRoleRepository.sp_getuserbyrole(rmsUserRequest);

    //         result = convertToGetUserByRole(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return result;
    // }

    @Override
    public List<RMSUser> sp_getuserbyrole(RMSUserRequest rmsUserRequest) {

        List<RMSUser> result = Collections.emptyList();
        List<Object[]> objects = userRoleRepository.sp_getuserbyrole(rmsUserRequest);
        result = convertToGetUserByRole(objects);
        return result;
    }

    private List<RMSUser> convertToGetUserByRole(List<Object[]> objects) {
        List<RMSUser> userList = new ArrayList<>();

        for (Object[] obj : objects) {

            RMSUser user = new RMSUser();
            user.setSsm4uuserrefno((String) obj[0]);
            user.setNm((String) obj[1]);
            user.setEmail((String) obj[2]);

            userList.add(user);
        }

        return userList;
    }

    // @Override
    // public RMSUser sp_getuserdetail(RMSUserRequest rmsUserRequest) {
    //     RMSUser result = new RMSUser();

    //     try {

    //         Object objects = userRoleRepository.sp_getuserdetail(rmsUserRequest);

    //         result = convertToGetUserDetail(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return result;
    // }

    @Override
    public RMSUser sp_getuserdetail(RMSUserRequest rmsUserRequest) {
        RMSUser result = new RMSUser();
        Object objects = userRoleRepository.sp_getuserdetail(rmsUserRequest);
        result = convertToGetUserDetail(objects);
        return result;
    }

    private RMSUser convertToGetUserDetail(Object objects) {
        // RMSUser userList = new ArrayList<>();
        RMSUser user = new RMSUser();
        // for (Object obj : objects) {

        if (objects instanceof Object[]) {
            Object[] objArray = (Object[]) objects;

            // if (objArray.length >= 4) {
            user.setSsm4uuserrefno((String) objArray[0]);
            user.setNm((String) objArray[1]);
            user.setEmail((String) objArray[2]);
            user.setStatus((String) objArray[3]);
            // }
        }

        // userList.add(user);
        // }

        return user;
    }
    // #endregion
}
