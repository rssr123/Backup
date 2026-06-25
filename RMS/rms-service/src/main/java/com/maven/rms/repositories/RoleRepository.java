package com.maven.rms.repositories;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, BigInteger> {

    Optional<Role> findRoleByRoleNmEn(String RoleName);

}
