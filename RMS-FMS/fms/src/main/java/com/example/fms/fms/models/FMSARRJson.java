package com.example.fms.fms.models;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FMSARRJson {
    @JsonProperty("Type")
    private GenericValue<String> type;

    @JsonProperty("Branch")
    private GenericValue<String> branch;

    @JsonProperty("ApplicationDate")
    private GenericValue<Date> applicationDate;

    @JsonProperty("CashAccount")
    private GenericValue<String> cashAccount;

    @JsonProperty("CustomerID")
    private GenericValue<String> customerID;

    @JsonProperty("Description")
    private GenericValue<String> description;

    @JsonProperty("PaymentAmount")
    private GenericValue<Double> paymentAmount;

    @JsonProperty("PaymentMethod")
    private GenericValue<String> paymentMethod;

    @JsonProperty("PaymentRef")
    private GenericValue<String> paymentRef;

    @JsonProperty("custom")
    private Custom custom;

    @JsonProperty("DocumentsToApply")
    private List<DocumentToApply> documentsToApply;

    public GenericValue<String> getType() {
        return type;
    }

    public void setType(GenericValue<String> type) {
        this.type = type;
    }

    public GenericValue<String> getBranch() {
        return branch;
    }

    public void setBranch(GenericValue<String> branch) {
        this.branch = branch;
    }

    public GenericValue<Date> getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(GenericValue<Date> applicationDate) {
        this.applicationDate = applicationDate;
    }

    public GenericValue<String> getCashAccount() {
        return cashAccount;
    }

    public void setCashAccount(GenericValue<String> cashAccount) {
        this.cashAccount = cashAccount;
    }

    public GenericValue<String> getCustomerID() {
        return customerID;
    }

    public void setCustomerID(GenericValue<String> customerID) {
        this.customerID = customerID;
    }

    public GenericValue<String> getDescription() {
        return description;
    }

    public void setDescription(GenericValue<String> description) {
        this.description = description;
    }

    public GenericValue<Double> getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(GenericValue<Double> paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public GenericValue<String> getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(GenericValue<String> paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public GenericValue<String> getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(GenericValue<String> paymentRef) {
        this.paymentRef = paymentRef;
    }

    public Custom getCustom() {
        return custom;
    }

    public void setCustom(Custom custom) {
        this.custom = custom;
    }

    // public List<Charge> getCharges() {
    //     return charges;
    // }

    // public void setCharges(List<Charge> charges) {
    //     this.charges = charges;
    // }

    public List<DocumentToApply> getDocumentsToApply() {
        return documentsToApply;
    }

    public void setDocumentsToApply(List<DocumentToApply> documentsToApply) {
        this.documentsToApply = documentsToApply;
    }

    public static class Custom {
        @JsonProperty("CurrentDocument")
        private CurrentDocument currentDocument;

        public CurrentDocument getCurrentDocument() {
            return currentDocument;
        }

        public void setCurrentDocument(CurrentDocument currentDocument) {
            this.currentDocument = currentDocument;
        }
    }

    public static class CurrentDocument {
        @JsonProperty("AttributeSYSNAME")
        private Attribute attributeSYSNAME;

        public Attribute getAttributeSYSNAME() {
            return attributeSYSNAME;
        }

        public void setAttributeSYSNAME(Attribute attributeSYSNAME) {
            this.attributeSYSNAME = attributeSYSNAME;
        }
    }

    public static class Attribute {
        private String type;
        private String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    // public static class Charge {
    //     private GenericValue<Double> amount;
    //     private GenericValue<String> docType;
    //     private GenericValue<String> entityType;
    //     private GenericValue<String> offsetSubaccount;

    //     public GenericValue<Double> getAmount() {
    //         return amount;
    //     }

    //     public void setAmount(GenericValue<Double> amount) {
    //         this.amount = amount;
    //     }

    //     public GenericValue<String> getDocType() {
    //         return docType;
    //     }

    //     public void setDocType(GenericValue<String> docType) {
    //         this.docType = docType;
    //     }

    //     public GenericValue<String> getEntityType() {
    //         return entityType;
    //     }

    //     public void setEntityType(GenericValue<String> entityType) {
    //         this.entityType = entityType;
    //     }

    //     public GenericValue<String> getOffsetSubaccount() {
    //         return offsetSubaccount;
    //     }

    //     public void setOffsetSubaccount(GenericValue<String> offsetSubaccount) {
    //         this.offsetSubaccount = offsetSubaccount;
    //     }
    // }

    public static class DocumentToApply {
        @JsonProperty("DocType")
        private GenericValue<String> docType;
        @JsonProperty("ReferenceNbr")
        private GenericValue<String> referenceNbr;

        public GenericValue<String> getDocType() {
            return docType;
        }

        public void setDocType(GenericValue<String> docType) {
            this.docType = docType;
        }

        public GenericValue<String> getReferenceNbr() {
            return referenceNbr;
        }

        public void setReferenceNbr(GenericValue<String> referenceNbr) {
            this.referenceNbr = referenceNbr;
        }
    }
}