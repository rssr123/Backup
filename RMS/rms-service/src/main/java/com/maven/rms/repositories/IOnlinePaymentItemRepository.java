package com.maven.rms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.OnlinePaymentItem;

@Repository
public interface IOnlinePaymentItemRepository extends JpaRepository<OnlinePaymentItem, Integer>  {


    @Query(value = "CALL sp_getmttitem(:i_mtt_id);", nativeQuery = true)
	List<OnlinePaymentItem> sp_getmttitem(@Param("i_mtt_id") Integer mttId);
}
