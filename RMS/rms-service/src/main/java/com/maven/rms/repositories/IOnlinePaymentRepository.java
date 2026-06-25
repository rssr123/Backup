package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.OnlinePayment;

@Repository
public interface IOnlinePaymentRepository extends JpaRepository<OnlinePayment, Integer>  {
    
	Optional<OnlinePayment> getOnlinePaymentByMttId(Integer id);
	//Optional<OnlinePayment> getOnlinePaymentByOrnNo(String ornNo);
    //get mtt
    @Query(value = "SELECT * from rms_mtt where orn_no = :i_orn_no order by dt_modified desc LIMIT 1", nativeQuery = true)
    Optional<OnlinePayment> sp_getOneMTT(@Param("i_orn_no") String ornNo);

    //check repayment //will return status
    @Query(value = "CALL sp_checkORN(:i_orn_no);", nativeQuery = true)
    String sp_checkORN(@Param("i_orn_no") String ornNo);

    //get mtt
    @Query(value = "CALL sp_getMTT(:i_orn_no);", nativeQuery = true)
    Optional<OnlinePayment> sp_getMTT(@Param("i_orn_no") String ornNo);

    
    //get mtt count
    @Query(value = "SELECT count(1) FROM rms_mtt WHERE orn_no = :i_orn_no", nativeQuery = true)
    Integer sp_checkOrnDuplicate(@Param("i_orn_no") String ornNo);

    /*
    //insert mtt
    @Query(value = "CALL sp_inspaymentmtt(:i_rms_type,:i_ss_cd,:i_orn_no,:i_orn_dt,:i_cust_ip,:i_cust_nm,:i_cust_addr_1,:i_cust_addr_2,:i_cust_addr_3 ,:i_cust_postcode,:i_cust_city,:i_cust_state,:i_cust_email,:i_cust_phone,:i_total_amt ,:i_ss_return_url ,:i_username_c, :i_username_m);", nativeQuery = true)
    Integer sp_insertPaymentMTT(@Param("i_rms_type") String rmsType, @Param("i_ss_cd") String ssCd, @Param("i_orn_no") String ornNo,@Param("i_orn_dt") Date ornDate,@Param("i_cust_ip") String custIP,
        @Param("i_cust_nm") String custNm,@Param("i_cust_addr_1") String custAddr1,@Param("i_cust_addr_2") String custAddr2,@Param("i_cust_addr_3") String custAddr3,@Param("i_cust_postcode") String custPostCode,
        @Param("i_cust_city") String custCity,@Param("i_cust_state") String custState,@Param("i_cust_email") String custEmail,@Param("i_cust_phone") String custPhone,@Param("i_total_amt") BigDecimal totalAmt,
        @Param("i_ss_return_url") String ssReturnURL,@Param("i_username_c") String UserNameC,@Param("i_username_m") String UsernameM);

    //insert mtt_item
    @Query(value = "CALL sp_inspaymentmttitem(:i_mtt_id,:i_fee_detail_id,:i_item_ref_no,:i_item_desc,:i_line_no,:i_qty,:i_unit_fee,:i_gross_amt,:i_grant_cd,:i_disc_amt,:i_tax_pct,:i_tax_amt,:i_net_amt,:i_entity_type,:i_entity_no,:i_entity_nm,:i_cp_no,:i_cp_tier,:i_cp_tier_amt,:i_cp_tier_disc_pct,:i_username_c,:i_username_m)", nativeQuery = true)
    Integer sp_insertPaymentMTTItem(@Param("i_mtt_id") Integer MTTId, @Param("i_fee_detail_id") String feeDetailId,@Param("i_item_ref_no") String itemRefNo,
        @Param("i_item_desc") String itemDesc,@Param("i_line_no") Integer lineNo,@Param("i_qty") Integer qty,@Param("i_unit_fee") BigDecimal unitFee,@Param("i_gross_amt") BigDecimal grossAmt,
        @Param("i_grant_cd") String grantCd,@Param("i_disc_amt") BigDecimal discAmt,@Param("i_tax_pct") BigDecimal taxPct,@Param("i_tax_amt") BigDecimal taxAmt,@Param("i_net_amt") BigDecimal netAmt,
        @Param("i_entity_type") String entityType,@Param("i_entity_no") String entityNo,@Param("i_entity_nm") String entityNm,@Param("i_cp_no") String cpNo,@Param("i_cp_tier") Integer cpTier,
        @Param("i_cp_tier_amt") BigDecimal cpTierAmt,@Param("i_cp_tier_disc_pct") BigDecimal cpTierDiscPct,@Param("i_username_c") String usernameC,@Param("i_username_m") String usernameM);
    */
}
