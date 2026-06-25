package com.maven.rms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rms_mtt_pg", 
uniqueConstraints = { 
		@UniqueConstraint(columnNames = "mtt_pg_id")})
public class MTTPG {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="mtt_pg_id")
	private Long mttPgId;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mtt_id")
	private OnlinePayment rmsMTT;
	
	@Column(name="pymt_submit_dt")
    private LocalDateTime pymtSubmitDt;

	@Size(max=7)
	@Column(name="pg_txn_type")
	private String pgTxnType;
	
	@Size(max=3)
	@Column(name="pg_pymt_method")
	private String pgPymtMethod;
	
	@Size(max=3)
	@Column(name="pg_service_id")
	private String pgServiceId;
	
	@Size(max=20)
	@Column(name="pg_pymt_id")
	private String pgPymtId;
	
	@Size(max=100)
	@Column(name="pg_pymt_desc")
	private String pgPymtDesc;
	
	@Column(name="pg_pymt_amt")
	private BigDecimal pgPymtAmt;
	
	@Size(max=3)
	@Column(name="pg_curr_cd")
	private String pgCurrCd;
	
	@Column(name="pg_b4tax_amt")
	private BigDecimal pgB4TaxAmt;
	
	@Column(name="pg_tax_amt")
	private BigDecimal pgTaxAmt;
	
	@Size(max=2)
	@Column(name="pg_lang_cd")
	private String pgLangCd;
	
	@Size(max=30)
	@Column(name="pg_txn_id")
	private String pgTxnId;
	
	@Size(max=30)
	@Column(name="pg_issuing_bank")
	private String pgIssuingBank;
	
	@Size(max=12)
	@Column(name="pg_auth_cd")
	private String pgAuthCd;
	
	@Column(name="pg_txn_status")
	private Integer pgTxnStatus;
	
	@Size(max=255)
	@Column(name="pg_txn_msg")
	private String pgTxnMsg;
	
	@Size(max=100)
	@Column(name="pg_hash_value")
	private String pgHashValue;
	
	@Size(max=100)
	@Column(name="pg_hash_value2")
	private String pgHashValue2;
	
	@Size(max=100)
	@Column(name="pg_qhash_value")
	private String pgQhashValue;
	
	@Column(name="pg_txn_exists")
	private Integer pgTxnExsts;
	
	@Size(max=255)
	@Column(name="pg_query_desc")
	private String pgQueryDesc;
	
	@Column(name="dt_created")
    private LocalDateTime dtCreated;
	
	@Column(name="dt_modified")
    private LocalDateTime dtModified;
    
	@Size(max=25)
	@Column(name="created_by")
	private String createdBy;
	
	@Size(max=255)
	@Column(name="modified_by")
	private String modifiedBy;

	@Column(name="pg_refund_amt")
	private BigDecimal pgRefundAmt;
	
	@Size(max=255)
	@Column(name="pg_bank_refno")
	private String pgBankRefNo;
	
	@Size(max=255)
	@Column(name="pg_session_id")
	private String pgSessionId;
	
	@Size(max=3)
	@Column(name="pg_token_type")
	private String pgTokenType;
	
	@Size(max=50)
	@Column(name="pg_token")
	private String pgToken;

	@Column(name="pg_resp_time")
	private LocalDateTime pgRespTime;
	
	@Size(max=19)
	@Column(name="pg_cardno_mask")
	private String pgCardNoMask;
	
	@Size(max=30)
	@Column(name="pg_cardholder")
	private String pgCardHolder;
	
	@Size(max=10)
	@Column(name="pg_cardtype")
	private String pgCardType;
	
	@Size(max=6)
	@Column(name="pg_cardexp")
	private String pgCardExp;

	public MTTPG() {}

	public MTTPG(OnlinePayment rmsMTT, LocalDateTime pymtSubmitDt,
			@Size(max = 7) String pgTxnType, @Size(max = 3) String pgPymtMethod, @Size(max = 3) String pgServiceId,
			@Size(max = 20) String pgPymtId, @Size(max = 100) String pgPymtDesc, BigDecimal pgPymtAmt,
			@Size(max = 3) String pgCurrCd, BigDecimal pgB4TaxAmt, BigDecimal pgTaxAmt,
			@Size(max = 2) String pgLangCd, @Size(max = 30) String pgTxnId, @Size(max = 30) String pgIssuingBank,
			@Size(max = 12) String pgAuthCd, Integer pgTxnStatus, @Size(max = 255) String pgTxnMsg,
			@Size(max = 100) String pgHashValue, @Size(max = 100) String pgHashValue2,
			@Size(max = 100) String pgQhashValue, Integer pgTxnExsts, @Size(max = 255) String pgQueryDesc,
			@Size(max = 25) String createdBy, @Size(max = 255) String modifiedBy) {
		super();
		this.rmsMTT = rmsMTT;
		this.pymtSubmitDt = pymtSubmitDt;
		this.pgTxnType = pgTxnType;
		this.pgPymtMethod = pgPymtMethod;
		this.pgServiceId = pgServiceId;
		this.pgPymtId = pgPymtId;
		this.pgPymtDesc = pgPymtDesc;
		this.pgPymtAmt = pgPymtAmt;
		this.pgCurrCd = pgCurrCd;
		this.pgB4TaxAmt = pgB4TaxAmt;
		this.pgTaxAmt = pgTaxAmt;
		this.pgLangCd = pgLangCd;
		this.pgTxnId = pgTxnId;
		this.pgIssuingBank = pgIssuingBank;
		this.pgAuthCd = pgAuthCd;
		this.pgTxnStatus = pgTxnStatus;
		this.pgTxnMsg = pgTxnMsg;
		this.pgHashValue = pgHashValue;
		this.pgHashValue2 = pgHashValue2;
		this.pgQhashValue = pgQhashValue;
		this.pgTxnExsts = pgTxnExsts;
		this.pgQueryDesc = pgQueryDesc;
		this.dtCreated = LocalDateTime.now();
		this.dtModified = dtCreated;
		this.createdBy = createdBy;
		this.modifiedBy = modifiedBy;
	}
	
	public String getPymtDtString() {
		return pgRespTime != null ? pgRespTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")) : null;
	}
	
	@Override
	public String toString() {
	    return "MTTPG{" +
	            "mttPgId=" + mttPgId +
	            ", rmsMTTId=" + (rmsMTT != null ? rmsMTT.getMttId() : "null") +
	            ", pymtSubmitDt=" + pymtSubmitDt +
	            ", pgTxnType='" + pgTxnType + '\'' +
	            ", pgPymtMethod='" + pgPymtMethod + '\'' +
	            ", pgServiceId='" + pgServiceId + '\'' +
	            ", pgPymtId='" + pgPymtId + '\'' +
	            ", pgPymtDesc='" + pgPymtDesc + '\'' +
	            ", pgPymtAmt=" + pgPymtAmt +
	            ", pgCurrCd='" + pgCurrCd + '\'' +
	            ", pgB4TaxAmt=" + pgB4TaxAmt +
	            ", pgTaxAmt=" + pgTaxAmt +
	            ", pgLangCd='" + pgLangCd + '\'' +
	            ", pgTxnId='" + pgTxnId + '\'' +
	            ", pgIssuingBank='" + pgIssuingBank + '\'' +
	            ", pgAuthCd='" + pgAuthCd + '\'' +
	            ", pgTxnStatus=" + pgTxnStatus +
	            ", pgTxnMsg='" + pgTxnMsg + '\'' +
	            ", pgHashValue='" + pgHashValue + '\'' +
	            ", pgHashValue2='" + pgHashValue2 + '\'' +
	            ", pgQhashValue='" + pgQhashValue + '\'' +
	            ", pgTxnExsts=" + pgTxnExsts +
	            ", pgQueryDesc='" + pgQueryDesc + '\'' +
	            ", dtCreated=" + dtCreated +
	            ", dtModified=" + dtModified +
	            ", createdBy='" + createdBy + '\'' +
	            ", modifiedBy='" + modifiedBy + '\'' +
	            ", pgRefundAmt=" + pgRefundAmt +
	            ", pgBankRefNo='" + pgBankRefNo + '\'' +
	            ", pgSessionId='" + pgSessionId + '\'' +
	            ", pgTokenType='" + pgTokenType + '\'' +
	            ", pgToken='" + pgToken + '\'' +
	            ", pgRespTime=" + pgRespTime +
	            ", pgCardNoMask='" + pgCardNoMask + '\'' +
	            ", pgCardHolder='" + pgCardHolder + '\'' +
	            ", pgCardType='" + pgCardType + '\'' +
	            ", pgCardExp='" + pgCardExp + '\'' +
	            '}';
	}
}
