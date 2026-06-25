package com.maven.rms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.MTTRCPT;

@Repository
public interface MTTRCPTRepository extends JpaRepository<MTTRCPT, Long> {

	//Optional<MTTRCPT> findMTTRCPTByRmsMTTMttId(int rmsMTTId);
	//Optional<MTTRCPT> findMTTRCPTByRcptNo(String rcptNo);
	//List<MTTRCPT> findByIsUploadedAndVersionIdIsNull(int isUploaded); 


	@Query(value = "CALL sp_getrunno('OR');", nativeQuery = true)
	String sp_getrunno();
	
    //get rcpt count
    @Query(value = "SELECT count(1) FROM rms_mtt_rcpt WHERE rcpt_no = :i_rcpt_no", nativeQuery = true)
    Integer sp_checkRcptDuplicate(@Param("i_rcpt_no") String rcptNo);

    //get rcpt count
    @Query(value = "SELECT count(1) FROM rms_mtt_rcpt WHERE mtt_id = :i_mtt_id", nativeQuery = true)
    Integer sp_checkRcptDuplicate(@Param("i_mtt_id") int mttId);

    //get rcpt
    @Query(value = "SELECT limit 1 * FROM rms_mtt_rcpt WHERE mtt_id = :i_mtt_id and rcpt_status = 'A' ORDER BY dt_created DESC", nativeQuery = true)
    Optional<MTTRCPT> sp_getRcptByMttId(@Param("i_mtt_id") int mttId);
    
    //get rcpt
    @Query(value = "SELECT limit 1 * FROM rms_mtt_rcpt WHERE rcpt_no = :i_rcpt_no and rcpt_status = 'A' ORDER BY dt_created DESC", nativeQuery = true)
    Optional<MTTRCPT> sp_getRcptByRcptNo(@Param("i_rcpt_no") String rcptNo);
    
    @Query(value = "SELECT * FROM rms_mtt_rcpt WHERE ver_id is null and is_uploaded = :i_is_uploaded and status = 'A'", nativeQuery = true)
    List<MTTRCPT> sp_getRcptListForIsUploaded(@Param("i_is_uploaded")int isUploaded);
}
