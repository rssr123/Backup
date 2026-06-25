package com.example.fms.fms.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIRequestDeserializer extends JsonDeserializer<APIRequest> {

    @Override
    public APIRequest deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        APIRequest apiRequest = new APIRequest();

        apiRequest.setCustom(deserializeCustom(node.get("custom")));

        List<APIRequest.VendorInfo> vendorInfoList = new ArrayList<>();
        JsonNode vendorInfosNode = node.get("VendorInfo");
        if (vendorInfosNode != null && vendorInfosNode.isArray()) {
            for (JsonNode vendorInfoNode : vendorInfosNode) {
                APIRequest.VendorInfo vendorInfo = deserializeVendorInfo(vendorInfoNode);
                if (vendorInfo != null) {
                    vendorInfoList.add(vendorInfo);
                }
            }
        }
        apiRequest.setVendorInfo(vendorInfoList);

        List<APIRequest.InvoiceHeader> invoiceHeaderList = new ArrayList<>();
        JsonNode invoiceHeadersNode = node.get("InvoiceHeader");
        if (invoiceHeadersNode != null && invoiceHeadersNode.isArray()) {
            for (JsonNode invoiceHeaderNode : invoiceHeadersNode) {
                APIRequest.InvoiceHeader invoiceHeader = deserializeInvoiceHeader(invoiceHeaderNode);
                if (invoiceHeader != null) {
                    invoiceHeaderList.add(invoiceHeader);
                }
            }
        }
        apiRequest.setInvoiceHeader(invoiceHeaderList);

        return apiRequest;
    }

    private String getValue(JsonNode node) {
        return node != null && node.get("value") != null && !node.get("value").isNull()
                ? node.get("value").asText() : null;
    }

    private APIRequest.Custom deserializeCustom(JsonNode node) {
        if (node == null) return null;
        APIRequest.Custom custom = new APIRequest.Custom();
        custom.setDocument(deserializeDocument(node.get("Document")));
        return custom;
    }

    private APIRequest.Custom.Document deserializeDocument(JsonNode node) {
        if (node == null) return null;
        APIRequest.Custom.Document document = new APIRequest.Custom.Document();
        document.setAttributeSYSNAME(deserializeAttributeSYSNAME(node.get("AttributeSYSNAME")));
        return document;
    }

    private APIRequest.Custom.Document.AttributeSYSNAME deserializeAttributeSYSNAME(JsonNode node) {
        if (node == null) return null;
        APIRequest.Custom.Document.AttributeSYSNAME attributeSYSNAME = new APIRequest.Custom.Document.AttributeSYSNAME();
        attributeSYSNAME.setType(getValue(node.get("type")));
        attributeSYSNAME.setValue(getValue(node));
        return attributeSYSNAME;
    }

    private APIRequest.VendorInfo deserializeVendorInfo(JsonNode node) {
        if (node == null) return null;
        APIRequest.VendorInfo vendorInfo = new APIRequest.VendorInfo();
        vendorInfo.setVendor(deserializeVendor(node.get("Vendor")));
        vendorInfo.setVendorName(deserializeVendorName(node.get("VendorName")));
        vendorInfo.setCustom(deserializeVendorInfoCustom(node.get("custom")));
        vendorInfo.setPaymentInstructions(deserializePaymentInstructions(node.get("PaymentInstructions")));
        vendorInfo.setMainContact(deserializeMainContact(node.get("MainContact")));
        return vendorInfo;
    }

    private APIRequest.VendorInfo.Vendor deserializeVendor(JsonNode node) {
        APIRequest.VendorInfo.Vendor vendor = new APIRequest.VendorInfo.Vendor();
        vendor.setValue(getValue(node));
        return vendor;
    }

    private APIRequest.VendorInfo.VendorName deserializeVendorName(JsonNode node) {
        APIRequest.VendorInfo.VendorName vendorName = new APIRequest.VendorInfo.VendorName();
        vendorName.setValue(getValue(node));
        return vendorName;
    }

    private APIRequest.VendorInfo.Custom deserializeVendorInfoCustom(JsonNode node) {
        if (node == null) return null;
        APIRequest.VendorInfo.Custom custom = new APIRequest.VendorInfo.Custom();
        custom.setCurrentVendor(deserializeCurrentVendor(node.get("CurrentVendor")));
        return custom;
    }

    private APIRequest.VendorInfo.Custom.CurrentVendor deserializeCurrentVendor(JsonNode node) {
        if (node == null) return null;
        APIRequest.VendorInfo.Custom.CurrentVendor currentVendor = new APIRequest.VendorInfo.Custom.CurrentVendor();
        currentVendor.setUsrIdentityType(deserializeUsrIdentityType(node.get("UsrIdentityType")));
        currentVendor.setUsrIdentityNbr(deserializeUsrIdentityNbr(node.get("UsrIdentityNbr")));
        return currentVendor;
    }

    private APIRequest.VendorInfo.Custom.CurrentVendor.UsrIdentityType deserializeUsrIdentityType(JsonNode node) {
        APIRequest.VendorInfo.Custom.CurrentVendor.UsrIdentityType type = new APIRequest.VendorInfo.Custom.CurrentVendor.UsrIdentityType();
        type.setType(getValue(node.get("Type")));
        type.setValue(getValue(node));
        return type;
    }

    private APIRequest.VendorInfo.Custom.CurrentVendor.UsrIdentityNbr deserializeUsrIdentityNbr(JsonNode node) {
        APIRequest.VendorInfo.Custom.CurrentVendor.UsrIdentityNbr nbr = new APIRequest.VendorInfo.Custom.CurrentVendor.UsrIdentityNbr();
        nbr.setType(getValue(node.get("Type")));
        nbr.setValue(getValue(node));
        return nbr;
    }

    private List<APIRequest.VendorInfo.PaymentInstructions> deserializePaymentInstructions(JsonNode node) {
        if (node == null || !node.isArray()) return null;
        List<APIRequest.VendorInfo.PaymentInstructions> list = new ArrayList<>();
        for (JsonNode piNode : node) {
            APIRequest.VendorInfo.PaymentInstructions pi = new APIRequest.VendorInfo.PaymentInstructions();
            pi.setDescription(new APIRequest.VendorInfo.PaymentInstructions.Description(getValue(piNode.get("Description"))));
            pi.setPaymentInstructionsID(new APIRequest.VendorInfo.PaymentInstructions.PaymentInstructionsID(getValue(piNode.get("PaymentInstructionsID"))));
            pi.setPaymentMethod(new APIRequest.VendorInfo.PaymentInstructions.PaymentMethod(getValue(piNode.get("PaymentMethod"))));
            pi.setValue(new APIRequest.VendorInfo.PaymentInstructions.Value(getValue(piNode.get("Value"))));
            list.add(pi);
        }
        return list;
    }

    private APIRequest.VendorInfo.MainContact deserializeMainContact(JsonNode node) {
        if (node == null) return null;
        APIRequest.VendorInfo.MainContact contact = new APIRequest.VendorInfo.MainContact();
        JsonNode addr = node.get("Address");
        if (addr != null) {
            APIRequest.VendorInfo.MainContact.Address address = new APIRequest.VendorInfo.MainContact.Address();
            address.setAddressLine1(new APIRequest.VendorInfo.MainContact.Address.AddressLine1(getValue(addr.get("AddressLine1"))));
            address.setAddressLine2(new APIRequest.VendorInfo.MainContact.Address.AddressLine2(getValue(addr.get("AddressLine2"))));
            address.setAddressLine3(new APIRequest.VendorInfo.MainContact.Address.AddressLine3(getValue(addr.get("AddressLine3"))));
            address.setState(new APIRequest.VendorInfo.MainContact.Address.State(getValue(addr.get("State"))));
            address.setPostalCode(new APIRequest.VendorInfo.MainContact.Address.PostalCode(getValue(addr.get("PostalCode"))));
            address.setCity(new APIRequest.VendorInfo.MainContact.Address.City(getValue(addr.get("City"))));
            address.setCountry(new APIRequest.VendorInfo.MainContact.Address.Country(getValue(addr.get("Country"))));
            contact.setAddress(address);
        }
        contact.setEmail(new APIRequest.VendorInfo.MainContact.Email(getValue(node.get("Email"))));
        return contact;
    }

    private APIRequest.InvoiceHeader deserializeInvoiceHeader(JsonNode node) {
        if (node == null) return null;
        APIRequest.InvoiceHeader ih = new APIRequest.InvoiceHeader();
        ih.setType(new APIRequest.InvoiceHeader.Type(getValue(node.get("Type"))));
        ih.setBranchID(new APIRequest.InvoiceHeader.BranchID(getValue(node.get("BranchID"))));
        ih.setDate(new APIRequest.InvoiceHeader.Date(getValue(node.get("Date"))));
        ih.setDescription(new APIRequest.InvoiceHeader.Description(getValue(node.get("Description"))));
        ih.setHold(new APIRequest.InvoiceHeader.Hold(getValue(node.get("Hold"))));
        ih.setVendorRef(new APIRequest.InvoiceHeader.VendorRef(getValue(node.get("VendorRef"))));
        ih.setAmount(new APIRequest.InvoiceHeader.Amount(getValue(node.get("Amount"))));
        ih.setDetails(deserializeDetails(node.get("Details")));
        return ih;
    }

    private List<APIRequest.InvoiceHeader.Details> deserializeDetails(JsonNode node) {
        if (node == null || !node.isArray()) return null;
        List<APIRequest.InvoiceHeader.Details> list = new ArrayList<>();
        for (JsonNode detailNode : node) {
            APIRequest.InvoiceHeader.Details detail = new APIRequest.InvoiceHeader.Details();
            detail.setAccount(new APIRequest.InvoiceHeader.Details.Account(getValue(detailNode.get("Account"))));
            detail.setAmount(new APIRequest.InvoiceHeader.Details.Amount(getValue(detailNode.get("Amount"))));
            detail.setBranch(new APIRequest.InvoiceHeader.Details.Branch(getValue(detailNode.get("Branch"))));
            detail.setQty(new APIRequest.InvoiceHeader.Details.Qty(getValue(detailNode.get("Qty"))));
            detail.setSubaccount(new APIRequest.InvoiceHeader.Details.Subaccount(getValue(detailNode.get("Subaccount"))));
            detail.setTransactionDescription(new APIRequest.InvoiceHeader.Details.TransactionDescription(getValue(detailNode.get("TransactionDescription"))));
            detail.setUnitCost(new APIRequest.InvoiceHeader.Details.UnitCost(getValue(detailNode.get("UnitCost"))));
            detail.setUom(new APIRequest.InvoiceHeader.Details.UOM(getValue(detailNode.get("UOM"))));
            list.add(detail);
        }
        return list;
    }
}



