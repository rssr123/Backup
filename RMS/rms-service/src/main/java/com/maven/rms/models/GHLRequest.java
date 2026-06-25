package com.maven.rms.models;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GHLRequest {
    private String i_txn_ty; 
    private String i_pymt_method;
    private String i_service_id;
    private String i_pymt_id;
    private String i_orn_no;
    private BigDecimal i_amt;
    private String i_cur_cd;
    private String i_hash_val;
    private String i_hash_val2;
    private String i_txn_id;
    private String i_iss_bank;
    private String i_txn_status;
    private String i_txn_msg;
    private String i_auth_cd;
    private String i_bank_ref_no;
    private String i_token_ty;
    private String i_token;
    private String i_resp_time;
    private String i_card_no_mask;
    private String i_card_holder;
    private String i_card_ty;
    private String i_card_exp;
    private String i_param7;
    private String i_created_by;
    private String i_modified_by;
    private Integer i_processed;

    @Override
    public String toString(){
        return "GHLRequest{" +
                "i_txn_ty='" + i_txn_ty + '\'' +
                ", i_pymt_method='" + i_pymt_method + '\'' +
                ", i_service_id='" + i_service_id + '\'' +
                ", i_pymt_id='" + i_pymt_id + '\'' +
                ", i_orn_no='" + i_orn_no + '\'' +
                ", i_amt=" + i_amt +
                ", i_cur_cd='" + i_cur_cd + '\'' +
                ", i_hash_val='" + i_hash_val + '\'' +
                ", i_hash_val2='" + i_hash_val2 + '\'' +
                ", i_txn_id='" + i_txn_id + '\'' +
                ", i_iss_bank='" + i_iss_bank + '\'' +
                ", i_txn_status='" + i_txn_status + '\'' +
                ", i_txn_msg='" + i_txn_msg + '\'' +
                ", i_auth_cd='" + i_auth_cd + '\'' +
                ", i_bank_ref_no='" + i_bank_ref_no + '\'' +
                ", i_token_ty='" + i_token_ty + '\'' +
                ", i_token='" + i_token + '\'' +
                ", i_resp_time='" + i_resp_time + '\'' +
                ", i_card_no_mask='" + i_card_no_mask + '\'' +
                ", i_card_holder='" + i_card_holder + '\'' +
                ", i_card_ty='" + i_card_ty + '\'' +
                ", i_card_exp='" + i_card_exp + '\'' +
                ", i_param7='" + i_param7 + '\'' +
                ", i_created_by='" + i_created_by + '\'' +
                ", i_modified_by='" + i_modified_by + '\'' +
                ", i_processed=" + i_processed +
                '}';
    }
}

