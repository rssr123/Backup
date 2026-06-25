package com.maven.rms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class FMSAPIAJson {

    @JsonProperty("custom")
    private Custom custom;

    @JsonProperty("VendorInfo")
    private List<VendorInfo> vendorInfo;

    @JsonProperty("InvoiceHeader")
    private List<InvoiceHeader> invoiceHeader;

    @Getter
    @Setter
    public static class Custom {
        @JsonProperty("Document")
        private Document document;
    }

    @Getter
    @Setter
    public static class Document {
        @JsonProperty("AttributeSYSNAME")
        private Attribute attributeSYSNAME;
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
    public static class VendorInfo {
        @JsonProperty("Vendor")
        private GenericValue<String> vendor;
        @JsonProperty("VendorName")
        private GenericValue<String> vendorName; //bigdecimal
        @JsonProperty("custom")
        private VendorCustom custom;
        @JsonProperty("PaymentInstructions")
        private List<PaymentInstruction> paymentInstructions;
        @JsonProperty("MainContact")
        private MainContact mainContact;
    }

    @Getter
    @Setter
    public static class VendorCustom {
        @JsonProperty("CurrentVendor")
        private CurrentVendor currentVendor;
    }

    @Getter
    @Setter
    public static class CurrentVendor {
        @JsonProperty("UsrIdentityType")
        private Attribute usrIdentityType;
        @JsonProperty("UsrIdentityNbr")
        private Attribute usrIdentityNbr;
    }



    @Getter
    @Setter
    public static class PaymentInstruction {
        @JsonProperty("Description")
        private GenericValue<String> description;
        @JsonProperty("PaymentInstructionsID")
        private GenericValue<String> paymentInstructionsID;
        @JsonProperty("PaymentMethod")
        private GenericValue<String> paymentMethod;
        @JsonProperty("Value")
        private GenericValue<String> value;
    }

    @Getter
    @Setter
    public static class MainContact {
        @JsonProperty("Address")
        private Address address;
        @JsonProperty("Email")
        private GenericValue<String> email;
    }

    @Getter
    @Setter
    public static class Address {
        @JsonProperty("AddressLine1")
        private GenericValue<String> addressLine1;
        @JsonProperty("AddressLine2")
        private GenericValue<String> addressLine2;
        @JsonProperty("AddressLine3")
        private GenericValue<String> addressLine3;
        @JsonProperty("State")
        private GenericValue<String> state;
        @JsonProperty("PostalCode")
        private GenericValue<String> postalCode;
        @JsonProperty("City")
        private GenericValue<String> city;
        @JsonProperty("Country")
        private GenericValue<String> country;
    }

    @Getter
    @Setter
    public static class InvoiceHeader {
        @JsonProperty("Type")
        private GenericValue<String> type;
        @JsonProperty("BranchID")
        private GenericValue<String> branchID;
        @JsonProperty("Date")
        private GenericValue<String> date;
        @JsonProperty("Description")
        private GenericValue<String> description;
        @JsonProperty("Hold")
        private GenericValue<Boolean> hold;
        @JsonProperty("VendorRef")
        private GenericValue<String> vendorRef;
        @JsonProperty("Amount")
        private GenericValue<String> amount;//bigdecimal
        @JsonProperty("Details")
        private List<Detail> details;
    }

    @Getter
    @Setter
    public static class Detail {
        @JsonProperty("Account")
        private GenericValue<String> account;
        @JsonProperty("Amount")
        private GenericValue<String> amount; //bigdecimal
        @JsonProperty("Branch")
        private GenericValue<String> branch;
        @JsonProperty("Qty")
        private GenericValue<String> qty; //bigdecimal
        @JsonProperty("Subaccount")
        private GenericValue<String> subaccount;
        @JsonProperty("TransactionDescription")
        private GenericValue<String> transactionDescription;
        @JsonProperty("UnitCost")
        private GenericValue<String> unitCost; //bigdecimal
        @JsonProperty("UOM")
        private GenericValue<String> uom;
    }

    // @Getter
    // @Setter
    // public static class GenericValue<T> {
    //     @JsonProperty("value")
    //     private T value;
    // }
}