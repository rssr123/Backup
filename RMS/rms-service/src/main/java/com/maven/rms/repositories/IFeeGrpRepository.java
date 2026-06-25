package com.maven.rms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.FeeGrp;
import com.maven.rms.models.MFT;

@Repository
public interface IFeeGrpRepository extends JpaRepository<FeeGrp, Integer> {


	@Query(value = "CALL sp_getFeeGroup();", nativeQuery = true)
    List<FeeGrp> sp_getFeeGroup();



}
