package com.example.fms.fms.models;

import java.util.List;

public class JournalRequest {
    private BranchID branchID;
    private Description description;
    private LedgerID ledgerID;
    private Module module;
    private TransactionDate transactionDate;
    private Custom custom;
    private List<Detail> details;

    public BranchID getBranchID() {
        return branchID;
    }

    public void setBranchID(BranchID branchID) {
        this.branchID = branchID;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public LedgerID getLedgerID() {
        return ledgerID;
    }

    public void setLedgerID(LedgerID ledgerID) {
        this.ledgerID = ledgerID;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public TransactionDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(TransactionDate transactionDate) {
        this.transactionDate = transactionDate;
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

    // Inner classes for nested JSON objects

    public static class BranchID {
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

    public static class LedgerID {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Module {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class TransactionDate {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    // public static class Custom {
    //     private CurrentDocument currentDocument;
    //     private AttributeSYSNAME attributeSYSNAME;

    //     public CurrentDocument getCurrentDocument() {
    //         return currentDocument;
    //     }

    //     public void setCurrentDocument(CurrentDocument currentDocument) {
    //         this.currentDocument = currentDocument;
    //     }

    //     public AttributeSYSNAME getAttributeSYSNAME() {
    //         return attributeSYSNAME;
    //     }

    //     public void setAttributeSYSNAME(AttributeSYSNAME attributeSYSNAME) {
    //         this.attributeSYSNAME = attributeSYSNAME;
    //     }

    //     public static class CurrentDocument {
    //         private AttributeExtRefNbr attributeExtRefNbr;

    //         public AttributeExtRefNbr getAttributeExtRefNbr() {
    //             return attributeExtRefNbr;
    //         }

    //         public void setAttributeExtRefNbr(AttributeExtRefNbr attributeExtRefNbr) {
    //             this.attributeExtRefNbr = attributeExtRefNbr;
    //         }

    //         public static class AttributeExtRefNbr {
    //             private String type;
    //             private String value;

    //             public String getType() {
    //                 return type;
    //             }

    //             public void setType(String type) {
    //                 this.type = type;
    //             }

    //             public String getValue() {
    //                 return value;
    //             }

    //             public void setValue(String value) {
    //                 this.value = value;
    //             }
    //         }
    //     }

    //     public static class AttributeSYSNAME {
    //         private String type;
    //         private String value;

    //         public String getType() {
    //             return type;
    //         }

    //         public void setType(String type) {
    //             this.type = type;
    //         }

    //         public String getValue() {
    //             return value;
    //         }

    //         public void setValue(String value) {
    //             this.value = value;
    //         }
    //     }
    // }

    public static class Custom {
        private BatchModule batchModule;
    
        public BatchModule getBatchModule() {
            return batchModule;
        }
    
        public void setBatchModule(BatchModule batchModule) {
            this.batchModule = batchModule;
        }
    
        public static class BatchModule {
            private AttributeEXTREFNBR attributeEXTREFNBR;
            private AttributeSYSNAME attributeSYSNAME;
    
            public AttributeEXTREFNBR getAttributeEXTREFNBR() {
                return attributeEXTREFNBR;
            }
    
            public void setAttributeEXTREFNBR(AttributeEXTREFNBR attributeEXTREFNBR) {
                this.attributeEXTREFNBR = attributeEXTREFNBR;
            }
    
            public AttributeSYSNAME getAttributeSYSNAME() {
                return attributeSYSNAME;
            }
    
            public void setAttributeSYSNAME(AttributeSYSNAME attributeSYSNAME) {
                this.attributeSYSNAME = attributeSYSNAME;
            }
    
            public static class AttributeEXTREFNBR {
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

    public static class Detail {
        private Account account;
        private BranchID branchID;
        private CreditAmount creditAmount;
        private DebitAmount debitAmount;
        private Subaccount subaccount;
        private TransactionDescription transactionDescription;

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
        }

        public BranchID getBranchID() {
            return branchID;
        }

        public void setBranchID(BranchID branchID) {
            this.branchID = branchID;
        }

        public CreditAmount getCreditAmount() {
            return creditAmount;
        }

        public void setCreditAmount(CreditAmount creditAmount) {
            this.creditAmount = creditAmount;
        }

        public DebitAmount getDebitAmount() {
            return debitAmount;
        }

        public void setDebitAmount(DebitAmount debitAmount) {
            this.debitAmount = debitAmount;
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

        public static class Account {
            private String value;
    
            public String getValue() {
                return value;
            }
    
            public void setValue(String value) {
                this.value = value;
            }
        }
    
        public static class CreditAmount {
            private Double value;
    
            public Double getValue() {
                return value;
            }
    
            public void setValue(Double value) {
                this.value = value;
            }
        }
    
        public static class DebitAmount {
            private Double value;
    
            public Double getValue() {
                return value;
            }
    
            public void setValue(Double value) {
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
    }
}
