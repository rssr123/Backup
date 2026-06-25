package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSARRJson {

    @JsonProperty("Type")
    private GenericValue<String> type;

    @JsonProperty("Branch")
    private GenericValue<String> branch;

    @JsonProperty("ApplicationDate")
    private GenericValue<String> applicationDate;

    @JsonProperty("CashAccount")
    private GenericValue<String> cashAccount;

    @JsonProperty("CustomerID")
    private GenericValue<String> customerID;

    @JsonProperty("Description")
    private GenericValue<String> description;

    @JsonProperty("PaymentAmount")
    private GenericValue<BigDecimal> paymentAmount;

    @JsonProperty("PaymentMethod")
    private GenericValue<String> paymentMethod;

    @JsonProperty("PaymentRef")
    private GenericValue<String> paymentRef;

    @JsonProperty("custom")
    private Custom custom;

    @JsonProperty("Charges")
    private List<Charge> charges;

    @JsonProperty("DocumentsToApply")
    private List<DocumentToApply> documentsToApply;

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
        private Attribute attributeSYSNAME;

        @JsonProperty("AttributeDOCNO")
        private Attribute attributeDOCNO;
    }

    @Getter
    @Setter
    public static class Attribute {
        @JsonProperty("type")
        private String type;
        @JsonProperty("value")
        private String value;
    }

    @Getter
    @Setter
    public static class Charge {
        @JsonProperty("Amount")
        private GenericValue<BigDecimal> amount;
        @JsonProperty("DocType")
        private GenericValue<String> docType;
        @JsonProperty("EntityType")
        private GenericValue<String> entityType;
        @JsonProperty("OffsetSubaccount")
        private GenericValue<String> offsetSubaccount;
        @JsonProperty("AccountID")
        private GenericValue<String> accountID;
    }

    @Getter
    @Setter
    public static class DocumentToApply {
        @JsonProperty("DocType")
        private GenericValue<String> docType;
        @JsonProperty("ReferenceNbr")
        private GenericValue<String> referenceNbr;
        @JsonProperty("AmountPaid")
        private GenericValue<BigDecimal> amt_paid;
    }
}