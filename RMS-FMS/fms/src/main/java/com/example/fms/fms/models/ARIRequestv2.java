package com.example.fms.fms.models;

import java.util.List;

public class ARIRequestv2 {
    
    private Type type;
    private LinkBranch linkBranch;
    private Amount amount;
    private CustomerID customer;
    private CustomerOrder customerOrder;
    private Date date;
    private InvoiceDate inv_dt;
    private Description description;
    private Custom custom;
    private List<Detail> details;

    // Getters and Setters

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

    public CustomerID getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerID customer) {
        this.customer = customer;
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

    public InvoiceDate getInv_dt() {
        return inv_dt;
    }

    public void setInv_dt(InvoiceDate inv_dt) {
        this.inv_dt = inv_dt;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Custom getCustom() {
        return custom;
    }

    public void setCustom(Custom custom) {
        this.custom = custom;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    // Nested classes for structure mirroring JSON

    public static class Type {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class LinkBranch {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Amount {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
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

    public static class CustomerOrder {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Date {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class InvoiceDate {
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

    public static class Custom {
        private CurrentDocument currentDocument;

        public CurrentDocument getCurrentDocument() {
            return currentDocument;
        }

        public void setCurrentDocument(CurrentDocument currentDocument) {
            this.currentDocument = currentDocument;
        }

        public static class CurrentDocument {
            private AttributeSYSNAME attributeSYSNAME;

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

            public static class AttributeGENPDF {
                private String type;
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
        private LineNbr lineNbr; // new field
        private ChartofAccount1 chartofAccount1;
        private ChartofAccount2 chartofAccount2;
        private Branch branch;
        private Qty qty;
        private Subaccount subaccount;
        private TransactionDescription transactionDescription;
        private UnitPrice unitPrice;
        private DiscountAmount discountAmount;
        private DepositID depositID;
        private DepositTask depositTask;

        // Removed field
        // private ReceiptNumber receiptNumber;
        // private PayeeInfo payeeInfo;
        // private EntityName entityName;
        // private EntityNumber entityNumber;
        // private EntityType entityType;
        // private ItemAmount itemAmount;
        // private ChartofAccount chartofAccount;
        // private PaymentMode paymentMode;
        // private ItemTaxAmount itemTaxAmount;
        // private Quantity quantity;

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

        // public ReceiptNumber getReceiptNumber() {
        //     return receiptNumber;
        // }

        // public void setReceiptNumber(ReceiptNumber receiptNumber) {
        //     this.receiptNumber = receiptNumber;
        // }

        // public PayeeInfo getPayeeInfo() {
        //     return payeeInfo;
        // }

        // public void setPayeeInfo(PayeeInfo payeeInfo) {
        //     this.payeeInfo = payeeInfo;
        // }

        // public EntityName getEntityName() {
        //     return entityName;
        // }

        // public void setEntityName(EntityName entityName) {
        //     this.entityName = entityName;
        // }

        // public EntityNumber getEntityNumber() {
        //     return entityNumber;
        // }

        // public void setEntityNumber(EntityNumber entityNumber) {
        //     this.entityNumber = entityNumber;
        // }

        // public EntityType getEntityType() {
        //     return entityType;
        // }

        // public void setEntityType(EntityType entityType) {
        //     this.entityType = entityType;
        // }

        // public ItemAmount getItemAmount() {
        //     return itemAmount;
        // }

        // public void setItemAmount(ItemAmount itemAmount) {
        //     this.itemAmount = itemAmount;
        // }

        // public ChartofAccount getChartofAccount() {
        //     return chartofAccount;
        // }

        // public void setChartofAccount(ChartofAccount chartofAccount) {
        //     this.chartofAccount = chartofAccount;
        // }

        // public PaymentMode getPaymentMode() {
        //     return paymentMode;
        // }

        // public void setPaymentMode(PaymentMode paymentMode) {
        //     this.paymentMode = paymentMode;
        // }

        // public ItemTaxAmount getItemTaxAmount() {
        //     return itemTaxAmount;
        // }

        // public void setItemTaxAmount(ItemTaxAmount itemTaxAmount) {
        //     this.itemTaxAmount = itemTaxAmount;
        // }

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

        // public Quantity getQuantity() {
        //     return quantity;
        // }

        // public void setQuantity(Quantity quantity) {
        //     this.quantity = quantity;
        // }

        public static class LineNbr {
            private int value;

            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }
        }

        public static class ChartofAccount1 {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class ChartofAccount2 {
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

        public static class Qty {
            private int value;

            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }
        }

        public static class Subaccount {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class TransactionDescription {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class UnitPrice {
            private int value;

            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }
        }

        public static class ReceiptNumber {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class PayeeInfo {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class EntityName {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class EntityNumber {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class EntityType {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class ItemAmount {
            private Double value;

            public Double getValue() {
                return value;
            }

            public void setValue(Double value) {
                this.value = value;
            }
        }

        // public static class ChartofAccount {
        //     private String value;

        //     public String getValue() {
        //         return value;
        //     }

        //     public void setValue(String value) {
        //         this.value = value;
        //     }
        // }

        public static class PaymentMode {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class ItemTaxAmount {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class DiscountAmount {
            private Double value;

            public Double getValue() {
                return value;
            }

            public void setValue(Double value) {
                this.value = value;
            }
        }

        public static class DepositID {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class DepositTask {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        // public static class Quantity {
        //     private String value;

        //     public String getValue() {
        //         return value;
        //     }

        //     public void setValue(String value) {
        //         this.value = value;
        //     }
        // }
    }
}
