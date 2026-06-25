package com.maven.rms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_rtt_chargeback")
@Table(name = "rms_rtt_wf")
public class RTTChargeback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rtt_wf_id")
    private Integer rttWfId;

    @Column(name = "rtt_app_no")
    private String rttAppNo;

    @Column(name = "dt_process")
    private Date dtProcess;

    @Column(name = "rcpt_no")
    private String rcptNo;

    @Column(name = "rcpt_date")
    private Date rcptDate;

    @Column(name = "orn_no")
    private String ornNo;

    @Column(name = "txn_id")
    private String txnId;

    @Column(name = "ent_no")
    private String entNo;

    @Column(name = "ent_nm")
    private String entNm;

    @Column(name = "cust_email")
    private String custEmail;

    @Column(name = "sme_email")
    private String smeEmail;

    @Column(name = "appeal_cnt")
    private Integer appealCnt;

    @Column(name = "rtt_status")
    private String rttstatus;

    @Column(name = "pickup_by")
    private String pickupBy;

    @Column(name = "date_pick")
    private Date datePick;

    @Column(name = "dt_created")
    private Date dtCreated;

    @Column(name = "dt_modified")
    private Date dtModified;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "status")
    private String status;

    @Column(name = "dt_requested")
    private Date dtRequested;

    @Column(name = "dt_approved")
    private Date dtApproved;

    @Column(name = "refund_ty")
    private String refundTy;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "branch_cd")
    private String branchCd;

    @Column(name = "refund_total_amt")
    private BigDecimal refundTotalAmt;

    @Column(name = "dt_rejected")
    private Date dtRejected;

    @Column(name = "refund_cd")
    private String refundCd;

    @Column(name = "assign_to")
    private String assignTo;

    @Column(name = "refund_reason")
    private String refundReason;
}
