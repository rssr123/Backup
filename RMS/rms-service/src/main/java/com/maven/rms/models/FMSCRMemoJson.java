package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)  // This annotation excludes null fields from JSON
public class FMSCRMemoJson {
    @JsonProperty("Type")
    private GenericValue<String> type;
    
    @JsonProperty("LinkBranch")
    private GenericValue<String> linkBranch;
    
    @JsonProperty("Amount")
    private GenericValue<BigDecimal> amount;
    
    @JsonProperty("CustomerID")
    private GenericValue<String> customer;

    @JsonProperty("CustomerOrder")
    private GenericValue<String> customerOrder;

    @JsonProperty("Project")
    private GenericValue<String> project;
    
    @JsonProperty("Date")
    private GenericValue<String> date;
    
    @JsonProperty("Description")
    private GenericValue<String> description;
    
    @JsonProperty("Hold")
    private GenericValue<Boolean> hold = new GenericValue<>(true);
    
    @JsonProperty("custom")
    private Custom custom;
    
    @JsonProperty("Details")
    private List<Detail> details;
    
    @JsonProperty("ApplicationsCreditMemo")
    //@JsonProperty("DocumentsToApply") // 241010: Modify Field Name based on ISD example
    private List<CreditMemo> applicationsCreditMemo;

    @Getter
    @Setter
    public static class Custom {
        @JsonProperty("CurrentDocument")
        private CurrentDocument currentDocument;
    }

    @Getter
    @Setter
    public static class CurrentDocument {
        @JsonProperty("AttributeSYSNAME")
        private Attribute attribute;
        @JsonProperty("AttributeGENPDF")
        private Attribute2 attribute2;
    }

    @Getter
    @Setter
    public static class Attribute {
        @JsonProperty("type")
        private String type = "CustomStringField";
        @JsonProperty("value")
        private String value;
    }
    
    @Getter
    @Setter
    public static class Attribute2 {
        @JsonProperty("type")
        private String type = "CustomIntField";
        @JsonProperty("value")
        private Boolean value = false;
    }

    @Getter
    @Setter
    public static class Detail {
    	@JsonProperty("Account")
        private GenericValue<String> account;
        
        @JsonProperty("ChartofAccount1")
        private GenericValue<String> chartOfAccount1;
        
        @JsonProperty("ChartofAccount2")
        private GenericValue<String> chartOfAccount2;
        
        @JsonProperty("Branch")
        private GenericValue<String> branch;
        
        @JsonProperty("LineNbr")
        private GenericValue<String> lineNbr;
        
        @JsonProperty("Qty")
        private GenericValue<Integer> qty;
        
        @JsonProperty("Subaccount")
        private GenericValue<String> subaccount;
        
        @JsonProperty("TransactionDescription")
        private GenericValue<String> transactionDescription;
        
        @JsonProperty("UnitPrice")
        private GenericValue<BigDecimal> unitPrice;
        
        // 241010: Add 2 new fields, depositID and depositTask
        @JsonProperty("DepositID")
        private GenericValue<String> depositID;
        
        @JsonProperty("DepositTask")
        private GenericValue<String> depositTask;

        @JsonProperty("EntityName")
        private GenericValue<String> entityName;
        
        @JsonProperty("EntityNumber")
        private GenericValue<String> entityNumber;
        
        @JsonProperty("EntityType")
        private GenericValue<String> entityType;

        @JsonProperty("ReceiptNumber")
        private GenericValue<String> rcptNo;

        @JsonProperty("PayeeInfo")
        private GenericValue<String> payeeInfo;

        @JsonProperty("ItemAmount")
        private GenericValue<Integer> itemAmt;

        @JsonProperty("PaymentMode")
        private GenericValue<String> pymtMd;

        @JsonProperty("ItemTaxAmount")
        private GenericValue<BigDecimal> itmTaxAmt;

        @JsonProperty("DiscountAmount")
        private GenericValue<BigDecimal> discAmt;
    }
    
    @Getter
    @Setter
    public static class CreditMemo {
        @JsonProperty("DocType")
        private GenericValue<String> docType;
        @JsonProperty("ReferenceNbr")
        private GenericValue<String> referenceNbr;
        @JsonProperty("AmountPaid")
        private GenericValue<BigDecimal> amountPaid;
    }    
}
