package com.maven.rms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {

	// Optional<UserRole> findUserRoleByUserName(String username);

	// Optional<UserRole> findRMSUserByEmail(String email);

    @Query(value = "CALL sp_getuseranduserroles(:i_user, , :i_user_role);", nativeQuery = true)
	List<UserRole> sp_getmttitem(@Param("i_user") String user, @Param("i_user_role") String user_role);

	
}
