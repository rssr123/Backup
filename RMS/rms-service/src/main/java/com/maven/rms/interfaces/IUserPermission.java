package com.maven.rms.interfaces;

import java.util.Map;

public interface IUserPermission {

    Map<String, Object> getUserPermissions(String email);

}
