package com.example.fms.fms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class APIRequest {

    @JsonProperty("custom")
    private Custom custom;
    @JsonProperty("VendorInfo")
    private List<VendorInfo> vendorInfo;
    @JsonProperty("InvoiceHeader")
    private List<InvoiceHeader> invoiceHeader;

    ///
    
    public Custom getCustom() {
        return custom;
    }

    public void setCustom(Custom custom) {
        this.custom = custom;
    }

    public List<VendorInfo> getVendorInfo() {
        return vendorInfo;
    }

    public void setVendorInfo(List<VendorInfo> vendorInfo) {
        this.vendorInfo = vendorInfo;
    }

    public List<InvoiceHeader> getInvoiceHeader() {
        return invoiceHeader;
    }

    public void setInvoiceHeader(List<InvoiceHeader> invoiceHeader) {
        this.invoiceHeader = invoiceHeader;
    }

    ////

    public static class Custom {
        @JsonProperty("Document")
        private Document document;

        public Document getDocument() {
            return document;
        }

        public void setDocument(Document document) {
            this.document = document;
        }

        public static class Document {
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

    public static class VendorInfo {
        @JsonProperty("Vendor")
        private Vendor vendor;
        @JsonProperty("VendorName")
        private VendorName vendorName;
        @JsonProperty("custom")
        private Custom custom;
        @JsonProperty("PaymentInstructions")
        private List<PaymentInstructions> paymentInstructions;
        @JsonProperty("MainContact")
        private MainContact mainContact;

        public Vendor getVendor() {
            return vendor;
        }

        public void setVendor(Vendor vendor) {
            this.vendor = vendor;
        }

        public VendorName getVendorName() {
            return vendorName;
        }

        public void setVendorName(VendorName vendorName) {
            this.vendorName = vendorName;
        }

        public Custom getCustom() {
            return custom;
        }

        public void setCustom(Custom custom) {
            this.custom = custom;
        }

        public List<PaymentInstructions> getPaymentInstructions() {

            return paymentInstructions;

        }

        public void setPaymentInstructions(List<PaymentInstructions> paymentInstructions) {

            this.paymentInstructions = paymentInstructions;

        }

        public MainContact getMainContact() {
            return mainContact;
        }

        public void setMainContact(MainContact mainContact) {
            this.mainContact = mainContact;
        }

        ///

        public static class Vendor {
            @JsonProperty("value")
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class VendorName {
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
            private CurrentVendor currentVendor;

            public CurrentVendor getCurrentVendor() {
                return currentVendor;
            }

            public void setCurrentVendor(CurrentVendor currentVendor) {
                this.currentVendor = currentVendor;
            }

            public static class CurrentVendor {
                private UsrIdentityType usrIdentityType;
                private UsrIdentityNbr usrIdentityNbr;

                public UsrIdentityType getUsrIdentityType() {
                    return usrIdentityType;
                }

                public void setUsrIdentityType(UsrIdentityType usrIdentityType) {
                    this.usrIdentityType = usrIdentityType;
                }

                public UsrIdentityNbr getUsrIdentityNbr() {
                    return usrIdentityNbr;
                }

                public void setUsrIdentityNbr(UsrIdentityNbr usrIdentityNbr) {
                    this.usrIdentityNbr = usrIdentityNbr;
                }

                public static class UsrIdentityType {
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

                public static class UsrIdentityNbr {
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

        public static class PaymentInstructions {
            private Description description;
            private PaymentInstructionsID paymentInstructionsID;
            private PaymentMethod paymentMethod;
            private Value value;

            public Description getDescription() {
                return description;
            }

            public void setDescription(Description description) {
                this.description = description;
            }

            public PaymentInstructionsID getPaymentInstructionsID() {
                return paymentInstructionsID;
            }

            public void setPaymentInstructionsID(PaymentInstructionsID paymentInstructionsID) {
                this.paymentInstructionsID = paymentInstructionsID;
            }

            public PaymentMethod getPaymentMethod() {
                return paymentMethod;
            }

            public void setPaymentMethod(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
            }

            public Value getValue() {
                return value;
            }

            public void setValue(Value value) {
                this.value = value;
            }

            public static class Description {
                @JsonProperty("value")
                private String value;

                public Description(String value2) {
                    //TODO Auto-generated constructor stub
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class PaymentInstructionsID {
                private String value;

                public PaymentInstructionsID(String value2) {
                    //TODO Auto-generated constructor stub
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class PaymentMethod {
                private String value;

                public PaymentMethod(String value2) {
                    //TODO Auto-generated constructor stub
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class Value {
                private String value;

                public Value(String value2) {
                    //TODO Auto-generated constructor stub
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }

        public static class MainContact {
            private Address address;
            private Email email;

            public Address getAddress() {
                return address;
            }

            public void setAddress(Address address) {
                this.address = address;
            }

            public Email getEmail() {
                return email;
            }

            public void setEmail(Email email) {
                this.email = email;
            }

            public static class Address {
                private AddressLine1 addressLine1;
                private AddressLine2 addressLine2;
                private AddressLine3 addressLine3;
                private State state;
                private PostalCode postalCode;
                private City city;
                private Country country;

                public AddressLine1 getAddressLine1() {
                    return addressLine1;
                }

                public void setAddressLine1(AddressLine1 addressLine1) {
                    this.addressLine1 = addressLine1;
                }

                public AddressLine2 getAddressLine2() {
                    return addressLine2;
                }

                public void setAddressLine2(AddressLine2 addressLine2) {
                    this.addressLine2 = addressLine2;
                }

                public AddressLine3 getAddressLine3() {
                    return addressLine3;
                }

                public void setAddressLine3(AddressLine3 addressLine3) {
                    this.addressLine3 = addressLine3;
                }

                public State getState() {
                    return state;
                }

                public void setState(State state) {
                    this.state = state;
                }

                public PostalCode getPostalCode() {
                    return postalCode;
                }

                public void setPostalCode(PostalCode postalCode) {
                    this.postalCode = postalCode;
                }

                public City getCity() {
                    return city;
                }

                public void setCity(City city) {
                    this.city = city;
                }

                public Country getCountry() {
                    return country;
                }

                public void setCountry(Country country) {
                    this.country = country;
                }

                public static class AddressLine1 {
                    private String value;

                    public AddressLine1(String value2) {
                        //TODO Auto-generated constructor stub
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }

                public static class AddressLine2 {
                    private String value;

                    public AddressLine2(String value2) {
                        //TODO Auto-generated constructor stub
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }

                public static class AddressLine3 {
                    private String value;

                    public AddressLine3(String value2) {
                        //TODO Auto-generated constructor stub
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }

                public static class State {
                    private String value;

                    public State(String value2) {
                        //TODO Auto-generated constructor stub
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }

                public static class PostalCode {
                    private String value;

                    public PostalCode(String value2) {
                        //TODO Auto-generated constructor stub
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }

                public static class City {
                    private String value;

                    public City(String value2) {
                        //TODO Auto-generated constructor stub
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }

                public static class Country {
                    private String value;

                    public Country(String value2) {
                        //TODO Auto-generated constructor stub
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }
            }

            public static class Email {
                private String value;

                public Email(String value2) {
                    //TODO Auto-generated constructor stub
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

    public static class InvoiceHeader {
        private Type type;
        private BranchID branchID;
        private Date date;
        private Description description;
        private Hold hold;
        private VendorRef vendorRef;
        private Amount amount;
        private List<Details> details;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public BranchID getBranchID() {
            return branchID;
        }

        public void setBranchID(BranchID branchID) {
            this.branchID = branchID;
        }

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

        public Hold getHold() {
            return hold;
        }

        public void setHold(Hold hold) {
            this.hold = hold;
        }

        public VendorRef getVendorRef() {
            return vendorRef;
        }

        public void setVendorRef(VendorRef vendorRef) {
            this.vendorRef = vendorRef;
        }

        public Amount getAmount() {
            return amount;
        }

        public void setAmount(Amount amount) {
            this.amount = amount;
        }

        public List<Details> getDetails() {
            return details;
        }

        public void setDetails(List<Details> details) {
            this.details = details;
        }

        ////

        public static class Type {
            private String value;

            public Type(String value2) {
                //TODO Auto-generated constructor stub
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class BranchID {
            private String value;

            public BranchID(String value2) {
                //TODO Auto-generated constructor stub
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class Date {
            private String value;

            public Date(String value2) {
                //TODO Auto-generated constructor stub
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class Description {
            private String value;

            public Description(String value2) {
                //TODO Auto-generated constructor stub
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class Hold {
            private String value;

            public Hold(String value2) {
                //TODO Auto-generated constructor stub
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class VendorRef {
            private String value;

            public VendorRef(String value2) {
                //TODO Auto-generated constructor stub
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class Amount {
            private String value;

            public Amount(String value2) {
                //TODO Auto-generated constructor stub
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class Details {
            private Account account;
            private Amount amount;
            private Branch branch;
            private Qty qty;
            private Subaccount subaccount;
            private TransactionDescription transactionDescription;
            private UnitCost unitCost;
            private UOM uom;

            public Account getAccount() {
                return account;
            }

            public void setAccount(Account account) {
                this.account = account;
            }

            public Amount getAmount() {
                return amount;
            }

            public void setAmount(Amount amount) {
                this.amount = amount;
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

            public UnitCost getUnitCost() {
                return unitCost;
            }

            public void setUnitCost(UnitCost unitCost) {
                this.unitCost = unitCost;
            }

            public UOM getUom() {
                return uom;
            }

            public void setUom(UOM uom) {
                this.uom = uom;
            }

            public static class Account {
                private String value;

                public Account(String value2) {
                    //TODO Auto-generated constructor stub
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class Amount {
                private String value;

                public Amount(String value2) {
                    //TODO Auto-generated constructor stub
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class Branch {
                private String value;

                public Branch(String value2) {
                    //TODO Auto-generated constructor stub
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class Qty {
                private String value;

                public Qty(String value2) {
                    //TODO Auto-generated constructor stub
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class Subaccount {
                private String value;

                public Subaccount(String value2) {
                    //TODO Auto-generated constructor stub
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class TransactionDescription {
                private String value;

                public TransactionDescription(String value2) {
                    //TODO Auto-generated constructor stub
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class UnitCost {
                private String value;

                public UnitCost(String value2) {
                    //TODO Auto-generated constructor stub
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class UOM {
                private String value;

                public UOM(String value2) {
                    //TODO Auto-generated constructor stub
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

}
