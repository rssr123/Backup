package com.maven.rms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maven.rms.models.MTTPG;
import com.maven.rms.models.OnlinePayment;

@Transactional
@Repository
public interface MTTPGRepository extends JpaRepository<MTTPG, Long> {
	List<MTTPG> findMTTPGByPgTxnStatus(int status);
	List<MTTPG> findMTTPGByPgTxnExsts(int status);
	
	List<MTTPG> findAllByrmsMTT_mttId(int mttId);
	
	Optional<MTTPG> findFirstByrmsMTT_mttIdOrderByPymtSubmitDtDesc(int mttId);
	
	Optional<MTTPG> findMTTPGByMttPgId(Long mttPgID);
	
	@Query(value = "CALL sp_getconfig(:i_config_cd);", nativeQuery = true)
	String getConfig(@Param("i_config_cd") String configName);

	@Query(value = "CALL sp_getMTTPGToCheck();", nativeQuery = true)
	List<MTTPG> sp_getMTTPGToCheck();
	
	@Query(value = "CALL sp_getexpiredmttpg(:daysLimit,:expiryString);", nativeQuery = true)
	List<MTTPG> sp_getExpiredMTTPG(@Param("daysLimit") int daysLimit, @Param("expiryString") String expiryString);
		
	@Modifying
	@Query(value = "CALL sp_updateLatestOrderStatus(:o_mtt_id);", nativeQuery = true)
	void sp_updateLatestOrderStatus(@Param("o_mtt_id") int mttID);
}
