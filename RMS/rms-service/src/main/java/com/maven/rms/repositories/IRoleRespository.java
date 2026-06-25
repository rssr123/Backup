package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.Role;


public interface IRoleRespository extends JpaRepository<Role, BigInteger>{
    

    @Query(value = "CALL sp_getUserRole(:i_username, :i_email);", nativeQuery = true)
    Role sp_getUserRole(@Param("i_username") String username, @Param("i_email") String email);



}
