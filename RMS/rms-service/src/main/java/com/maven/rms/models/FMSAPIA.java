package com.maven.rms.models;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FMSAPIA {

    private String fms_apia_v_id;
    private String ext_sys;
    private String vendor_id;
    private String vendor_nm;
    private String id_ty;
    private String id_no;
    private String pm;
    private String p_desc;
    private String p_id;
    private String p_bankname;
    private String p_value;
    private String addr1;
    private String addr2;
    private String addr3;
    private String city;
    private String country;
    private String postcode;
    private String state;
    private String email;
    private String phone;
    private String fms_ext_sys;
    private String fms_ref_no;
    private String fms_ven_ref;
    private String fms_status;
    private String fms_msg;
    private Date fms_date;
    private String ih_amt;
    private Date ih_date;
    private String ih_desc;
    private String ih_hold;
    private String ih_type;
    private String vendor_ref;
    private String fms_apia_ih_id;
    private String acct;
    private String amt;
    private String branch;
    private String ex_cost;
    private String qty;
    private String sub_acct;
    private String tax_ca;
    private String txn_desc;
    private String unit;
    private String uom;
    // new column
    private String description1;
    private String paymentinstructionsid1;
    private String paymentmethod1;
    private String value1;
    private String description2;
    private String paymentinstructionsid2;
    private String paymentmethod2;
    private String value2;
    private String description3;
    private String paymentinstructionsid3;
    private String paymentmethod3;
    private String value3;

    // sp_getrefunddetails
    // NVARCHAR(255) AS ent_nm,
    // NVARCHAR(50) AS id_ty,
    // NVARCHAR(50) AS id_no,
    // NVARCHAR(50) AS rtt_app_no,
    // NVARCHAR(255) AS cust_email,
    // NVARCHAR(50) AS refund_slip_no,
    // DECIMAL(18, 2) AS refund_total_amt;

    private String ent_nm;
    // private String id_ty;
    // private String id_no;
    private String rtt_app_no;
    private String cust_email;
    private String refund_slip_no;
    private String refund_total_amt;

    // invoice details
    private List<PaymentItemDetails> payment_item_details;

    // fmsResponse
    private String resp_attr_ext_sys;
    private String resp_ref_no;
    private String resp_vendor_ref;
    private String resp_status;
    private String resp_msg;
    private Date resp_date;

}
