package com.maven.rms.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.ParameterMode;

@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "sp_getTaxCode", procedureName = "sp_getTaxCode", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_page", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_size", type = Integer.class)
        }),
        @NamedStoredProcedureQuery(name = "sp_getMFTWFHistWithED", procedureName = "sp_getMFTWFHistWithED", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_page", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_size", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_wf_id", type = Integer.class)
        }),
        @NamedStoredProcedureQuery(name = "sp_updateMTT", procedureName = "sp_updateMTT", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_orn_no", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_cust_nm", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_cust_addr_1", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_cust_addr_2", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_cust_addr_3", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_cust_postcode", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_cust_city", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_cust_state", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "sp_getMTTItem", procedureName = "sp_getMTTItem", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_mtt_id", type = Integer.class)
        }),
        @NamedStoredProcedureQuery(name = "sp_checkLatestOrderStatus", procedureName = "sp_checkLatestOrderStatus", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_orn_no", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "sp_insertPayment", procedureName = "sp_insertPayment", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_mtt_id", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_pg_pymt_method", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_pg_service_id", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_pg_pymt_id", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_pg_pymt_amt", type = BigDecimal.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_pg_lang_cd", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_username_c", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_username_m", type = String.class)

        }),
        @NamedStoredProcedureQuery(name = "sp_getparam", procedureName = "sp_getparam", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_page", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_size", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_param_cd", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_param_grp_nm", type = String.class)
        }),
        // Fee Group Start
        @NamedStoredProcedureQuery(name = "sp_getfeegroup_v2", procedureName = "sp_getfeegroup_v2", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_page", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_size", type = Integer.class),
                //@StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fee_grp_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fee_grp_nm_en", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fee_grp_nm_bm", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_modified_by", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_dt_modified_fr", type = Date.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_dt_modified_to", type = Date.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_status", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "sp_insfeegroup", procedureName = "sp_insfeegroup", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fee_grp_nm_en", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fee_grp_nm_bm", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_created_by", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_modified_by", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_status", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "sp_updfeegroup", procedureName = "sp_updfeegroup", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fee_grp_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fee_grp_nm_en", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fee_grp_nm_bm", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_modified_by", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_status", type = String.class)
        }),

        // Fee Group End
        // // FMS Start
        // @NamedStoredProcedureQuery(name = "sp_getfms", procedureName = "sp_getfms", parameters = {
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_page", type = Integer.class),
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_size", type = Integer.class),
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fms_cd", type = String.class),
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_modified_by", type = String.class),
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_modified_by_nm", type = String.class),
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_dt_modified_fr", type = Date.class),
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_dt_modified_to", type = Date.class),
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_status", type = String.class)
        // }),
        // @NamedStoredProcedureQuery(name = "sp_updfms_activation", procedureName = "sp_updfms_activation", parameters = {
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fms_cd", type = String.class),
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_modified_by", type = String.class)

        // }),
        // @NamedStoredProcedureQuery(name = "sp_updfms_ina", procedureName = "sp_updfms_ina", parameters = {
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fms_id", type = Integer.class),
        //         @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_modified_by", type = String.class)


        // Deferred Income Start
        @NamedStoredProcedureQuery(name = "sp_getdi", procedureName = "sp_getdi", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_page", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_size", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_di_id", type = BigInteger.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_fee_detail_id", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_txn_type", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_entity_type", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_entity_no", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_dt_effective", type = Date.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_dt_expiry", type = Date.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_item_ref_no", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_approval_status", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_dt_approval", type = Date.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_status", type = String.class)
        }),

        @NamedStoredProcedureQuery(name = "sp_getricp", procedureName = "sp_getricp", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_page", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_size", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_ricp_id", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_entity_type", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_entity_no", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_cp_no", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_dt_issuance", type = Date.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_dt_expiry", type = Date.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_cp_amt", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_accr_amt", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_cp_tier", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_cp_tier_amt", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "i_status", type = String.class)
                
        })
        // FMS End
})
public class StoreProcedures {

}
