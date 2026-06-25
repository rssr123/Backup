package com.example.fms.fms.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ARRRequest {
    @JsonProperty("Type")
    private Type type;
    @JsonProperty("Branch")
    private Branch branch;
    @JsonProperty("CashAccount")
    private CashAccount cashAccount;
    @JsonProperty("CustomerID")
    private CustomerID customerID;
    @JsonProperty("Description")
    private Description description;
    @JsonProperty("PaymentAmount")
    private PaymentAmount paymentAmount;
    @JsonProperty("PaymentMethod")
    private PaymentMethod paymentMethod;
    @JsonProperty("PaymentRef")
    private PaymentRef paymentRef;
    @JsonProperty("ApplicationDate")
    private ApplicationDate applicationDate;
    @JsonProperty("Charges")
    private List<Charges> charges;
    @JsonProperty("DocumentsToApply")
    private List<DocumentToApply> documentsToApply;
    @JsonProperty("custom")
    private Custom custom;

    // Wei Ern: Latest ISD added fields
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public ApplicationDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(ApplicationDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public CashAccount getCashAccount() {
        return cashAccount;
    }

    public void setCashAccount(CashAccount cashAccount) {
        this.cashAccount = cashAccount;
    }

    public CustomerID getCustomerID() {
        return customerID;
    }

    public void setCustomerID(CustomerID customerID) {
        this.customerID = customerID;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public PaymentAmount getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(PaymentAmount paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentRef getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(PaymentRef paymentRef) {
        this.paymentRef = paymentRef;
    }

    public Custom getCustom() {
        return custom;
    }

    public void setCustom(Custom custom) {
        this.custom = custom;
    }

    public List<DocumentToApply> getDocumentsToApply() {
        return documentsToApply;
    }

    public void setDocumentsToApply(List<DocumentToApply> documentsToApply) {
        this.documentsToApply = documentsToApply;
    }

    public List<Charges> getCharges() {
        return charges;
    }

    public void setCharges(List<Charges> charges) {
        this.charges = charges;
    }

    // Inner classes for nested JSON objects

    public static class Type {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Branch {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ApplicationDate {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class CashAccount {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class CustomerID {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Description {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class PaymentAmount {
        private Double value;

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }
    }

    public static class PaymentMethod {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class PaymentRef {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
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

        public static class CurrentDocument {
            @JsonProperty("AttributeSYSNAME")
            private AttributeSYSNAME attributeSYSNAME;

            public AttributeSYSNAME getAttributeSYSNAME() {
                return attributeSYSNAME;
            }

            public void setAttributeSYSNAME(AttributeSYSNAME attributeSYSNAME) {
                this.attributeSYSNAME = attributeSYSNAME;
            }

            public static class AttributeSYSNAME {
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
        }
    }

    // Wei Ern: New field charges in signed-off ISD. 
    public static class Charges{
        @JsonProperty("AccountID")
        private AccountID accountID;
        @JsonProperty("Amount")
        private Amount amount;
        @JsonProperty("DocType")
        private DocType docType;
        @JsonProperty("EntityType")
        private EntityType entityType;
        @JsonProperty("OffsetSubaccount")
        private OffsetSubaccount offsetSubaccount;

        public AccountID getAccountID() {
            return accountID;
        }

        public void setAccountID(AccountID accountID) {
            this.accountID = accountID;
        }

        public Amount getAmount() {
            return amount;
        }

        public void setAmount(Amount amount) {
            this.amount = amount;
        }

        public DocType getDocType() {
            return docType;
        }

        public void setDocType(DocType docType) {
            this.docType = docType;
        }

        public EntityType getEntityType() {
            return entityType;
        }

        public void setEntityType(EntityType entityType) {
            this.entityType = entityType;
        }

        public OffsetSubaccount getOffsetSubaccount() {
            return offsetSubaccount;
        }

        public void setOffsetSubaccount(OffsetSubaccount offsetSubaccount) {
            this.offsetSubaccount = offsetSubaccount;
        }

        public static class AccountID{
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class Amount{
            private Double value;

            public Double getValue() {
                return value;
            }

            public void setValue(Double value) {
                this.value = value;
            }
        }

        public static class DocType{
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class EntityType{
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }


        public static class OffsetSubaccount{
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }

    public static class DocumentToApply {
        @JsonProperty("AmountPaid")
        private AmountPaid amountPaid;
        @JsonProperty("DocType")
        private DocType docType;
        @JsonProperty("ReferenceNbr")
        private ReferenceNbr referenceNbr;

        public DocType getDocType() {
            return docType;
        }

        public void setDocType(DocType docType) {
            this.docType = docType;
        }

        public ReferenceNbr getReferenceNbr() {
            return referenceNbr;
        }

        public void setReferenceNbr(ReferenceNbr referenceNbr) {
            this.referenceNbr = referenceNbr;
        }

        public static class AmountPaid {
            private Double value;

            public Double getValue() {
                return value;
            }

            public void setValue(Double value) {
                this.value = value;
            }
        }

        public static class DocType {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class ReferenceNbr {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
