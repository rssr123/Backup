package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Type",
        "LinkBranch",
        "Amount",
        "CustomerID",
        "CustomerOrder",
        "InvoiceDate",
        "Date",
        "Description",
        "Details",
        "custom"
})
public class FMSARIRequest {
    @JsonProperty("Type")
    private Type type;
    @JsonProperty("LinkBranch")
    private LinkBranch linkBranch;
    @JsonProperty("Amount")
    private Amount amount;
    @JsonProperty("CustomerID")
    private CustomerID customerID;
    @JsonProperty("CustomerOrder")
    private CustomerOrder customerOrder;
    @JsonProperty("InvoiceDate")
    private InvoiceDate InvDate;
    @JsonProperty("Date")
    private Date date;
    @JsonProperty("Description")
    private Description description;
    @JsonProperty("custom")
    private Custom Custom;
    @JsonProperty("Details")
    private List<Detail> details;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public LinkBranch getLinkBranch() {
        return linkBranch;
    }

    public void setLinkBranch(LinkBranch linkBranch) {
        this.linkBranch = linkBranch;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public CustomerID getCustomerID() {
        return customerID;
    }

    public void setCustomerID(CustomerID customerID) {
        this.customerID = customerID;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @JsonIgnore
    public InvoiceDate getInvoiceDate() {
        return InvDate;
    }

    @JsonIgnore
    public void setInvoiceDate(InvoiceDate InvDate) {
        this.InvDate = InvDate;
    }
	
    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Custom getCustom() {
        return Custom;
    }

    public void setCustom(Custom custom) {
        this.Custom = custom;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    public static class Type {
        @JsonProperty("value")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class LinkBranch {
        @JsonProperty("value")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Amount {
        @JsonProperty("value")
        private BigDecimal value;

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }

    public static class CustomerID {
        @JsonProperty("value")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class CustomerOrder {
        @JsonProperty("value")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class InvoiceDate {
        @JsonProperty("value")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private java.util.Date value;

        public java.util.Date getValue() {
            return value;
        }

        public void setValue(java.util.Date value) {
            this.value = value;
        }
    }

    public static class Date {
        @JsonProperty("value")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private java.util.Date value;

        public java.util.Date getValue() {
            return value;
        }

        public void setValue(java.util.Date value) {
            this.value = value;
        }
    }

    public static class Description {
        @JsonProperty("value")
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
            @JsonProperty("AttributeGENPDF")
            private AttributeGENPDF attributeGENPDF;

            public AttributeSYSNAME getAttributeSYSNAME() {
                return attributeSYSNAME;
            }

            public void setAttributeSYSNAME(AttributeSYSNAME attributeSYSNAME) {
                this.attributeSYSNAME = attributeSYSNAME;
            }

            public AttributeGENPDF getAttributeGENPDF() {
                return attributeGENPDF;
            }

            public void setAttributeGENPDF(AttributeGENPDF attributeGENPDF) {
                this.attributeGENPDF = attributeGENPDF;
            }

            public static class AttributeSYSNAME {
                @JsonProperty("type")
                private String type;
                @JsonProperty("value")
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

            public static class AttributeGENPDF {
                @JsonProperty("type")
                private String type;
                @JsonProperty("value")
                private Boolean value;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public Boolean getValue() {
                    return value;
                }

                public void setValue(Boolean value) {
                    this.value = value;
                }
            }
        }
    }

    public static class Detail {
        @JsonProperty("LineNbr")
        private LineNbr lineNbr;
        @JsonProperty("ChartofAccount1")
        private ChartofAccount1 chartofAccount1;
        @JsonProperty("ChartofAccount2")
        private ChartofAccount2 chartofAccount2;
        @JsonProperty("Branch")
        private Branch branch;
        @JsonProperty("Qty")
        private Qty qty;
        @JsonProperty("Subaccount")
        private Subaccount subaccount;
        @JsonProperty("TransactionDescription")
        private TransactionDescription transactionDescription;
        @JsonProperty("UnitPrice")
        private UnitPrice unitPrice;
        @JsonProperty("ReceiptNumber")
        private ReceiptNumber receiptNumber;
        @JsonProperty("PayeeInfo")
        private PayeeInfo payeeInfo;
        @JsonProperty("EntityName")
        private EntityName entityName;
        @JsonProperty("EntityNumber")
        private EntityNumber entityNumber;
        @JsonProperty("EntityType")
        private EntityType entityType;
        @JsonProperty("ItemAmount")
        private ItemAmount itemAmount;
        @JsonProperty("PaymentMode")
        private PaymentMode paymentMode;
        @JsonProperty("ItemTaxAmount")
        private ItemTaxAmount itemTaxAmount;
        @JsonProperty("DiscountAmount")
        private DiscountAmount discountAmount;
        @JsonProperty("DepositID")
        private DepositID depositID;
        @JsonProperty("DepositTask")
        private DepositTask depositTask;
        // Getters and setters can be added as needed

     

        public static class LineNbr {
            @JsonProperty("value")
            private int value;
            public int getValue() {
                return value;
            }
            public void setValue(int value) {
                this.value = value;
            }
        }

        public LineNbr getLineNbr() {
            return lineNbr;
        }

        public void setLineNbr(LineNbr lineNbr) {
            this.lineNbr = lineNbr;
        }

        public ChartofAccount1 getChartofAccount1() {
            return chartofAccount1;
        }

        public void setChartofAccount1(ChartofAccount1 chartofAccount1) {
            this.chartofAccount1 = chartofAccount1;
        }

        public ChartofAccount2 getChartofAccount2() {
            return chartofAccount2;
        }

        public void setChartofAccount2(ChartofAccount2 chartofAccount2) {
            this.chartofAccount2 = chartofAccount2;
        }

        public Branch getBranch() {
            return branch;
        }

        public void setBranch(Branch branch) {
            this.branch = branch;
        }

        public Qty getQty() {
            return qty;
        }

        public void setQty(Qty qty) {
            this.qty = qty;
        }

        public Subaccount getSubaccount() {
            return subaccount;
        }

        public void setSubaccount(Subaccount subaccount) {
            this.subaccount = subaccount;
        }

        public TransactionDescription getTransactionDescription() {
            return transactionDescription;
        }

        public void setTransactionDescription(TransactionDescription transactionDescription) {
            this.transactionDescription = transactionDescription;
        }

        public UnitPrice getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(UnitPrice unitPrice) {
            this.unitPrice = unitPrice;
        }

        public ReceiptNumber getReceiptNumber() {
            return receiptNumber;
        }

        public void setReceiptNumber(ReceiptNumber receiptNumber) {
            this.receiptNumber = receiptNumber;
        }

        public PayeeInfo getPayeeInfo() {
            return payeeInfo;
        }

        public void setPayeeInfo(PayeeInfo payeeInfo) {
            this.payeeInfo = payeeInfo;
        }

        public EntityName getEntityName() {
            return entityName;
        }

        public void setEntityName(EntityName entityName) {
            this.entityName = entityName;
        }

        public EntityNumber getEntityNumber() {
            return entityNumber;
        }

        public void setEntityNumber(EntityNumber entityNumber) {
            this.entityNumber = entityNumber;
        }

        public EntityType getEntityType() {
            return entityType;
        }

        public void setEntityType(EntityType entityType) {
            this.entityType = entityType;
        }

        public ItemAmount getItemAmount() {
            return itemAmount;
        }

        public void setItemAmount(ItemAmount itemAmount) {
            this.itemAmount = itemAmount;
        }

        public PaymentMode getPaymentMode() {
            return paymentMode;
        }

        public void setPaymentMode(PaymentMode paymentMode) {
            this.paymentMode = paymentMode;
        }

        public ItemTaxAmount getItemTaxAmount() {
            return itemTaxAmount;
        }

        public void setItemTaxAmount(ItemTaxAmount itemTaxAmount) {
            this.itemTaxAmount = itemTaxAmount;
        }

        public DiscountAmount getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(DiscountAmount discountAmount) {
            this.discountAmount = discountAmount;
        }

        public DepositID getDepositID() {
            return depositID;
        }

        public void setDepositID(DepositID depositID) {
            this.depositID = depositID;
        }

        public DepositTask getDepositTask() {
            return depositTask;
        }

        public void setDepositTask(DepositTask depositTask) {
            this.depositTask = depositTask;
        }

        public static class ChartofAccount1 {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class ChartofAccount2 {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class Branch {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class Qty {
            @JsonProperty("value")
            private int value;
            public int getValue() {
                return value;
            }
            public void setValue(int value) {
                this.value = value;
            }
        }

        public static class Subaccount {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class TransactionDescription {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class UnitPrice {
            @JsonProperty("value")
            private BigDecimal value;
            public BigDecimal getValue() {
                return value;
            }
            public void setValue(BigDecimal value) {
                this.value = value;
            }
        }

        public static class ReceiptNumber {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class PayeeInfo {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class EntityName {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class EntityNumber {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class EntityType {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class ItemAmount {
            @JsonProperty("value")
            private BigDecimal value;
            public BigDecimal getValue() {
                return value;
            }
            public void setValue(BigDecimal value) {
                this.value = value;
            }
        }

        public static class PaymentMode {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class ItemTaxAmount {
            @JsonProperty("value")
            private BigDecimal value;
            public BigDecimal getValue() {
                return value;
            }
            public void setValue(BigDecimal value) {
                this.value = value;
            }
        }

        public static class DiscountAmount {
            @JsonProperty("value")
            private BigDecimal value;
            public BigDecimal getValue() {
                return value;
            }
            public void setValue(BigDecimal value) {
                this.value = value;
            }

        }

        public static class DepositID {
            @JsonProperty("value")
            private String value;
            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class DepositTask {
            @JsonProperty("value")
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
