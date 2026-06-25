package com.example.fms.fms.models;

import java.util.List;

public class CreditRequestV2 {

    private Type type;
    private CustomerID customerID;
    private CustomerOrder customerOrder;
    private Date date;
    private Description description;
    private LinkBranch linkBranch;
    private Amount amount;
    // private Project project;
    // private Hold hold;
    private Custom custom;
    // Wei Ern: change field name
    private List<ApplicationsCreditMemo> applicationsCreditMemos;
    private List<Detail> details;

    public static class Type {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

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

    // public Project getProject() {
    //     return project;
    // }

    // public void setProject(Project project) {
    //     this.project = project;
    // }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    // public Hold getHold() {
    //     return hold;
    // }

    // public void setHold(Hold hold) {
    //     this.hold = hold;
    // }

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

    public List<ApplicationsCreditMemo> getApplicationsCreditMemo() {
        return applicationsCreditMemos;
    }

    public void setApplicationsCreditMemo(List<ApplicationsCreditMemo> applicationsCreditMemos) {
        this.applicationsCreditMemos = applicationsCreditMemos;
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

    public static class Project {
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

    public static class Description {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Hold {
        private boolean value;

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }

    public static class Custom {
        private CurrentDocument currentDocument;

        public static class CurrentDocument {
            private AttributeSYSNAME attributeSYSNAME;
            private AttributeGENPDF attributeGENPDF;

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
        }

        public CurrentDocument getCurrentDocument() {
            return currentDocument;
        }

        public void setCurrentDocument(CurrentDocument currentDocument) {
            this.currentDocument = currentDocument;
        }
    }

    public static class Detail {
        private LineNbr lineNbr;
        private ChartofAccount1 chartofAccount1;
        private ChartofAccount2 chartofAccount2;
        private Branch branch;
        private DiscountAmount discountAmount;
        private Qty qty;
        private Subaccount subaccount;
        private TransactionDescription transactionDescription;
        private UnitPrice unitPrice;
        private DepositID depositID;
        private DepositTask depositTask;

        // private EntityName entityName;
        // private EntityNumber entityNumber;
        // private EntityType entityType;
        // private Account account;

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

        public LineNbr getLineNbr() {
            return lineNbr;
        }

        public void setLineNbr(LineNbr lineNbr) {
            this.lineNbr = lineNbr;
        }

        public Branch getBranch() {
            return branch;
        }

        public void setBranch(Branch branch) {
            this.branch = branch;
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

        public Qty getQty() {
            return qty;
        }

        public void setQty(Qty qty) {
            this.qty = qty;
        }

        public UnitPrice getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(UnitPrice unitPrice) {
            this.unitPrice = unitPrice;
        }

        public DiscountAmount getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(DiscountAmount discountAmount) {
            this.discountAmount = discountAmount;
        }

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

        // public Account getAccount() {
        //     return account;
        // }

        // public void setAccount(Account account) {
        //     this.account = account;
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

        public static class Branch {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
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

        public static class Qty {
            private double value;

            public double getValue() {
                return value;
            }

            public void setValue(double value) {
                this.value = value;
            }
        }

        public static class UnitPrice {
            private double value;

            public double getValue() {
                return value;
            }

            public void setValue(double value) {
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

        // Wei Ern: Added 2 new fields in the latest ISD
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

        public static class Account {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class DiscountAmount {
            private double value;

            public double getValue() {
                return value;
            }

            public void setValue(double value) {
                this.value = value;
            }
        }
    }

    public static class ApplicationsCreditMemo {
        private ReferenceNbr referenceNbr;
        private AmountPaid amountPaid;
        private DocType docType;

        public ReferenceNbr getReferenceNbr() {
            return referenceNbr;
        }

        public void setReferenceNbr(ReferenceNbr referenceNbr) {
            this.referenceNbr = referenceNbr;
        }

        public AmountPaid getAmountPaid() {
            return amountPaid;
        }

        public void setAmountPaid(AmountPaid amountPaid) {
            this.amountPaid = amountPaid;
        }

        public DocType getDocType() {
            return docType;
        }

        public void setDocType(DocType docType) {
            this.docType = docType;
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

        public static class AmountPaid {
            private double value;

            public double getValue() {
                return value;
            }

            public void setValue(double value) {
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
    }
}
