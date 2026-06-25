package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GHLPayment {

    private String transaction_type;
    private String pymt_method;
    private String service_id;
    private String pymt_id;
    private String ord_no;
    private String pymt_desc;
    private String return_url;
    private String callback_url;
    private String approved_url;
    private String unapproved_url;
    private BigDecimal amt;
    private String curr_cd;
    private String cust_ip;
    private String cust_nm;
    private String cust_ph;
    private String cust_email;
    private String hash_value;
    private Integer page_timeout;
    private String ss_return_url;
    private String order_status;
    private String rcpt_no;
    private String rcpt_dt;

    // public String getCust_email() {
    //     return cust_email;
    // }
    
    // public void setCust_email(String cust_email) {
    //     this.cust_email = cust_email;
    // }

    // public String getSs_return_url() {
    //     return ss_return_url;
    // }

    // public void setSs_return_url(String ss_return_url) {
    //     this.ss_return_url = ss_return_url;
    // }

    // public String getTransaction_type() {
    //     return transaction_type;
    // }
    // public void setTransaction_type(String transaction_type) {
    //     this.transaction_type = transaction_type;
    // }
    // public String getPymt_method() {
    //     return pymt_method;
    // }
    // public void setPymt_method(String pymt_method) {
    //     this.pymt_method = pymt_method;
    // }
    // public String getService_id() {
    //     return service_id;
    // }
    // public void setService_id(String service_id) {
    //     this.service_id = service_id;
    // }
    // public String getPymt_id() {
    //     return pymt_id;
    // }
    // public void setPymt_id(String pymt_id) {
    //     this.pymt_id = pymt_id;
    // }
    // public String getOrd_no() {
    //     return ord_no;
    // }
    // public void setOrd_no(String ord_no) {
    //     this.ord_no = ord_no;
    // }
    // public String getPymt_desc() {
    //     return pymt_desc;
    // }
    // public void setPymt_desc(String pymt_desc) {
    //     this.pymt_desc = pymt_desc;
    // }
    // public String getReturn_url() {
    //     return return_url;
    // }
    // public void setReturn_url(String return_url) {
    //     this.return_url = return_url;
    // }
    // public BigDecimal getAmt() {
    //     return amt;
    // }
    // public void setAmt(BigDecimal amt) {
    //     this.amt = amt;
    // }
    // public String getCurr_cd() {
    //     return curr_cd;
    // }
    // public void setCurr_cd(String curr_cd) {
    //     this.curr_cd = curr_cd;
    // }
    // public String getCust_ip() {
    //     return cust_ip;
    // }
    // public void setCust_ip(String cust_ip) {
    //     this.cust_ip = cust_ip;
    // }
    // public String getCust_nm() {
    //     return cust_nm;
    // }
    // public void setCust_nm(String cust_nm) {
    //     this.cust_nm = cust_nm;
    // }
    // public String getCust_ph() {
    //     return cust_ph;
    // }
    // public void setCust_ph(String cust_ph) {
    //     this.cust_ph = cust_ph;
    // }
    // public String getHash_value() {
    //     return hash_value;
    // }
    // public void setHash_value(String hash_value) {
    //     this.hash_value = hash_value;
    // }
    // public Integer getPage_timeout() {
    //     return page_timeout;
    // }
    // public void setPage_timeout(Integer page_timeout) {
    //     this.page_timeout = page_timeout;
    // }

    /* 
    private String TransactionType;
    private String PymtMethod;
    private String ServiceID;
    private String PaymentID;
    private String OrderNumber;
    private String PaymentDesc;
    private String MerchantRerturnURL;
    private String MerchantCallbackURL;
    private BigDecimal Amount;
    private String CurrencyCode;
    private String CustIP;
    private String CustName;
    private String CustEmail;
    private String CustPhone;
    private String HashValue;
    private Integer PageTimeout;

    
    public String getTransactionType() {
        return TransactionType;
    }
    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }
    public String getPymtMethod() {
        return PymtMethod;
    }
    public void setPymtMethod(String pymtMethod) {
        PymtMethod = pymtMethod;
    }
    public String getServiceID() {
        return ServiceID;
    }
    public void setServiceID(String serviceID) {
        ServiceID = serviceID;
    }
    public String getPaymentID() {
        return PaymentID;
    }
    public void setPaymentID(String paymentID) {
        PaymentID = paymentID;
    }
    public String getOrderNumber() {
        return OrderNumber;
    }
    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }
    public String getPaymentDesc() {
        return PaymentDesc;
    }
    public void setPaymentDesc(String paymentDesc) {
        PaymentDesc = paymentDesc;
    }
    public String getMerchantRerturnURL() {
        return MerchantRerturnURL;
    }
    public void setMerchantRerturnURL(String merchantRerturnURL) {
        MerchantRerturnURL = merchantRerturnURL;
    }
    public String getMerchantCallbackURL() {
        return MerchantCallbackURL;
    }
    public void setMerchantCallbackURL(String merchantCallbackURL) {
        MerchantCallbackURL = merchantCallbackURL;
    }
    public BigDecimal getAmount() {
        return Amount;
    }
    public void setAmount(BigDecimal amount) {
        Amount = amount;
    }
    public String getCurrencyCode() {
        return CurrencyCode;
    }
    public void setCurrencyCode(String currencyCode) {
        CurrencyCode = currencyCode;
    }
    public String getCustIP() {
        return CustIP;
    }
    public void setCustIP(String custIP) {
        CustIP = custIP;
    }
    public String getCustName() {
        return CustName;
    }
    public void setCustName(String custName) {
        CustName = custName;
    }
    public String getCustEmail() {
        return CustEmail;
    }
    public void setCustEmail(String custEmail) {
        CustEmail = custEmail;
    }
    public String getCustPhone() {
        return CustPhone;
    }
    public void setCustPhone(String custPhone) {
        CustPhone = custPhone;
    }
    public String getHashValue() {
        return HashValue;
    }
    public void setHashValue(String hashValue) {
        HashValue = hashValue;
    }
    public Integer getPageTimeout() {
        return PageTimeout;
    }
    public void setPageTimeout(Integer pageTimeout) {
        PageTimeout = pageTimeout;
    }
*/
}
