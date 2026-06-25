package com.maven.rms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maven.rms.models.RMSUser;

@Repository
public interface UserRepository extends JpaRepository<RMSUser, String> {

	Optional<RMSUser> findRMSUserBySsm4uuserrefno(String username);

	Optional<RMSUser> findRMSUserByEmail(String email);

	Long deleteBySsm4uuserrefno(String username);

	Boolean existsRMSUserBySsm4uuserrefno(String username);

	@Query(value = "CALL sp_getUpperUser(:i_username, :i_email);", nativeQuery = true)
	List<RMSUser> sp_getUpperUser(@Param("i_username") String username, @Param("i_email") String email);
	
	@Query(value = "CALL sp_getusersbyrole(:i_role_nm_en);", nativeQuery = true)
	Optional<List<RMSUser>> sp_getusersbyrole(@Param("i_role_nm_en") String roleNameEn);

	@Query(value = "CALL sp_updatessm4uuserrefno(:i_ssm4uid, :i_email);", nativeQuery = true)
	Integer sp_updatessm4uuserrefno(@Param("i_ssm4uid") String username, @Param("i_email") String email);
}
