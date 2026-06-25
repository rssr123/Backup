package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaymentRequest {

    @NotNull(message = "Source System Code")
    @NotEmpty(message="Source System")
    @Size(min = 1, max = 10, message="Source System")
    private String ss_cd;

    @NotNull(message = "Payment Method")
    @NotEmpty(message="Payment Method")
    @Size(min = 1, max = 10, message="Payment Method")
    private String pymt_method;

    @NotNull(message = "Order Number")
    @NotEmpty(message="Order Number")
    @Size(min = 1, max = 20, message="Order Number")
    private String orn_no;

    @PastOrPresent(message = "Order Date")
    @NotNull(message = "Order Date")
    private Date orn_dt;

    @NotNull(message = "Customer Name")
    @NotEmpty(message="Customer Name")
    @Size(min = 1, max = 255, message="Customer Name")
    private String cust_nm;

    @NotNull(message = "Customer Address")
    @NotEmpty(message="Customer Address")
    @Size(min = 1, max = 150, message="Customer Address")
    private String cust_addr_1;
    private String cust_addr_2;
    private String cust_addr_3;

    @NotNull(message = "Customer Postcode")
    @NotEmpty(message="Customer Postcode")
    @Size(min = 1, max = 15, message="Customer Postcode")
    private String cust_postcode;

    @NotNull(message = "Customer City")
    @NotEmpty(message="Customer City")
    @Size(min = 1, max = 50, message="Customer City")
    private String cust_city;

    @NotNull(message = "Customer State")
    @NotEmpty(message="Customer State")
    @Size(min = 1, max = 50, message="Customer State")
    private String cust_state;
    private String cust_email;

    @NotNull(message = "Customer Phone Number")
    @NotEmpty(message="Customer Phone Number")
    @Size(min = 1, max = 15, message="Customer Phone Number")
    private String cust_phone;

    private BigDecimal total_amt;
    @NotNull(message = "Return URL")
    @NotEmpty(message="Return URL")
    @Size(min = 1, max = 2000, message="Return URL")
    private String ss_return_url;

    private String ss_callback_url;

    private Integer email_flag;

    private String order_status;

    private String collection_slip;

    private List<PaymentItemDetails> payment_item_details;

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "ss_cd='" + ss_cd + '\'' +
                ", pymt_method='" + pymt_method + '\'' +
                ", orn_no='" + orn_no + '\'' +
                ", orn_dt=" + orn_dt +
                ", cust_nm='" + cust_nm + '\'' +
                ", cust_addr_1='" + cust_addr_1 + '\'' +
                ", cust_addr_2='" + cust_addr_2 + '\'' +
                ", cust_addr_3='" + cust_addr_3 + '\'' +
                ", cust_postcode='" + cust_postcode + '\'' +
                ", cust_city='" + cust_city + '\'' +
                ", cust_state='" + cust_state + '\'' +
                ", cust_email='" + cust_email + '\'' +
                ", cust_phone='" + cust_phone + '\'' +
                ", total_amt=" + total_amt +
                ", ss_return_url='" + ss_return_url + '\'' +
                ", ss_callback_url='" + ss_callback_url + '\'' +
                ", email_flag=" + email_flag +
                ", order_status=" + order_status +
                ", collection_slip='" + collection_slip + '\'' +
                ", payment_item_details=" + payment_item_details +
                '}';
    }
}
