package com.example.fms.fms.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ARRRequestDeserializer extends JsonDeserializer<ARRRequest> {

    @Override
    public ARRRequest deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        ARRRequest arrRequest = new ARRRequest();

        arrRequest.setType(deserializeType(node.get("Type")));
        arrRequest.setBranch(deserializeBranch(node.get("Branch")));
        arrRequest.setCashAccount(deserializeCashAccount(node.get("CashAccount")));
        arrRequest.setCustomerID(deserializeCustomerID(node.get("CustomerID")));
        arrRequest.setDescription(deserializeDescription(node.get("Description")));
        arrRequest.setPaymentAmount(deserializePaymentAmount(node.get("PaymentAmount")));
        arrRequest.setPaymentMethod(deserializePaymentMethod(node.get("PaymentMethod")));
        arrRequest.setPaymentRef(deserializePaymentRef(node.get("PaymentRef")));
        arrRequest.setApplicationDate(deserializeApplicationDate(node.get("ApplicationDate")));
        arrRequest.setCharges(deserializeCharges(node.get("Charges")));
        arrRequest.setDocumentsToApply(deserializeDocumentsToApply(node.get("DocumentsToApply")));
        arrRequest.setCustom(deserializeCustom(node.get("custom")));

        return arrRequest;
    }

    private ARRRequest.Type deserializeType(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.Type type = new ARRRequest.Type();
        type.setValue(node.get("value").asText());
        return type;
    }

    private ARRRequest.Branch deserializeBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.Branch branch = new ARRRequest.Branch();
        branch.setValue(node.get("value").asText());
        return branch;
    }

    private ARRRequest.ApplicationDate deserializeApplicationDate(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.ApplicationDate applicationDate = new ARRRequest.ApplicationDate();
        applicationDate.setValue(node.get("value").asText());
        return applicationDate;
    }

    private ARRRequest.CashAccount deserializeCashAccount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.CashAccount cashAccount = new ARRRequest.CashAccount();
        cashAccount.setValue(node.get("value").asText());
        return cashAccount;
    }

    private ARRRequest.CustomerID deserializeCustomerID(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.CustomerID customerID = new ARRRequest.CustomerID();
        customerID.setValue(node.get("value").asText());
        return customerID;
    }

    private ARRRequest.Description deserializeDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.Description description = new ARRRequest.Description();
        description.setValue(node.get("value").asText());
        return description;
    }

    private ARRRequest.PaymentAmount deserializePaymentAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.PaymentAmount paymentAmount = new ARRRequest.PaymentAmount();
        paymentAmount.setValue(node.get("value").asDouble());
        return paymentAmount;
    }

    private ARRRequest.PaymentMethod deserializePaymentMethod(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.PaymentMethod paymentMethod = new ARRRequest.PaymentMethod();
        paymentMethod.setValue(node.get("value").asText());
        return paymentMethod;
    }

    private ARRRequest.PaymentRef deserializePaymentRef(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.PaymentRef paymentRef = new ARRRequest.PaymentRef();
        paymentRef.setValue(node.get("value").asText());
        return paymentRef;
    }

    private ARRRequest.Custom deserializeCustom(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.Custom custom = new ARRRequest.Custom();
        custom.setCurrentDocument(deserializeCurrentDocument(node.get("CurrentDocument")));
        return custom;
    }

    private ARRRequest.Custom.CurrentDocument deserializeCurrentDocument(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.Custom.CurrentDocument currentDocument = new ARRRequest.Custom.CurrentDocument();
        currentDocument.setAttributeSYSNAME(deserializeAttributeSYSNAME(node.get("AttributeSYSNAME")));
        return currentDocument;
    }

    private ARRRequest.Custom.CurrentDocument.AttributeSYSNAME deserializeAttributeSYSNAME(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.Custom.CurrentDocument.AttributeSYSNAME attributeSYSNAME = new ARRRequest.Custom.CurrentDocument.AttributeSYSNAME();
        attributeSYSNAME.setType(node.get("type").asText());
        attributeSYSNAME.setValue(node.get("value").asText());
        return attributeSYSNAME;
    }

    private List<ARRRequest.DocumentToApply> deserializeDocumentsToApply(JsonNode node) {
        List<ARRRequest.DocumentToApply> documentsToApply = new ArrayList<>();
        if (node == null || !node.isArray()) {
            return documentsToApply;
        }
        for (JsonNode docNode : node) {
            ARRRequest.DocumentToApply documentToApply = new ARRRequest.DocumentToApply();
            documentToApply.setDocType(deserializeDocType(docNode.get("DocType")));
            documentToApply.setReferenceNbr(deserializeReferenceNbr(docNode.get("ReferenceNbr")));
            documentsToApply.add(documentToApply);
        }
        return documentsToApply;
    }

    private ARRRequest.DocumentToApply.DocType deserializeDocType(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.DocumentToApply.DocType docType = new ARRRequest.DocumentToApply.DocType();
        docType.setValue(node.get("value").asText());
        return docType;
    }

    private ARRRequest.DocumentToApply.ReferenceNbr deserializeReferenceNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.DocumentToApply.ReferenceNbr referenceNbr = new ARRRequest.DocumentToApply.ReferenceNbr();
        referenceNbr.setValue(node.get("value").asText());
        return referenceNbr;
    }


    // private ARRRequest.Charges deserializeCharges(JsonNode node) {
    //     if (node == null || !node.isArray()) {
    //         return null;
    //     }
    //     ARRRequest.Charges charges = new ARRRequest.Charges();

    //     charges.setAmount(deserializeAmount(node.get("Amount")));
    //     charges.setDocType(deserializeDocTypeCharges(node.get("DocType")));
    //     charges.setEntityType(deserializeEntityTypeCharges(node.get("EntityType")));
    //     charges.setOffsetSubaccount(deserializeOffsetSubAccountCharges(node.get("OffsetSubaccount")));

    //     return charges;
    // }

    private List<ARRRequest.Charges> deserializeCharges(JsonNode node) {
        List<ARRRequest.Charges> documentsToApply = new ArrayList<>();
        if (node == null || !node.isArray()) {
            return documentsToApply;
        }
        for (JsonNode docNode : node) {
            ARRRequest.Charges charges = new ARRRequest.Charges();
            charges.setAccountID(deserializeAccountID(docNode.get("AccountID")));
            charges.setAmount(deserializeAmount(docNode.get("Amount")));
            charges.setDocType(deserializeDocTypeCharges(docNode.get("DocType")));
            charges.setEntityType(deserializeEntityTypeCharges(docNode.get("EntityType")));
            charges.setOffsetSubaccount(deserializeOffsetSubAccountCharges(docNode.get("OffsetSubaccount")));
            documentsToApply.add(charges);
        }
        return documentsToApply;
    }

    private ARRRequest.Charges.AccountID deserializeAccountID(JsonNode node) {
        if (node == null) {
            return null;
        }

        ARRRequest.Charges.AccountID accountID = new ARRRequest.Charges.AccountID();
        accountID.setValue(node.get("value").asText());
        return accountID;
    }

    private ARRRequest.Charges.Amount deserializeAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.Charges.Amount amount = new ARRRequest.Charges.Amount();
        amount.setValue(node.get("value").asDouble());
        return amount;
    }

    private ARRRequest.Charges.DocType deserializeDocTypeCharges(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.Charges.DocType docType = new ARRRequest.Charges.DocType();
        docType.setValue(node.get("value").asText());
        return docType;
    }

    private ARRRequest.Charges.EntityType deserializeEntityTypeCharges(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.Charges.EntityType entityType = new ARRRequest.Charges.EntityType();
        entityType.setValue(node.get("value").asText());
        return entityType;
    }

    private ARRRequest.Charges.OffsetSubaccount deserializeOffsetSubAccountCharges(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequest.Charges.OffsetSubaccount offsetSubaccount = new ARRRequest.Charges.OffsetSubaccount();
        offsetSubaccount.setValue(node.get("value").asText());
        return offsetSubaccount;
    }
}