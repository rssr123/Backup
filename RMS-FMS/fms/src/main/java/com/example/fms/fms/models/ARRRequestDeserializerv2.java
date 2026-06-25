package com.example.fms.fms.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ARRRequestDeserializerv2 extends JsonDeserializer<ARRRequestv2> {

    @Override
    public ARRRequestv2 deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        ARRRequestv2 arrRequest = new ARRRequestv2();

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

    private ARRRequestv2.Type deserializeType(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.Type type = new ARRRequestv2.Type();
        type.setValue(node.get("value").asText());
        return type;
    }

    private ARRRequestv2.Branch deserializeBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.Branch branch = new ARRRequestv2.Branch();
        branch.setValue(node.get("value").asText());
        return branch;
    }

    private ARRRequestv2.ApplicationDate deserializeApplicationDate(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.ApplicationDate applicationDate = new ARRRequestv2.ApplicationDate();
        applicationDate.setValue(node.get("value").asText());
        return applicationDate;
    }

    private ARRRequestv2.CashAccount deserializeCashAccount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.CashAccount cashAccount = new ARRRequestv2.CashAccount();
        cashAccount.setValue(node.get("value").asText());
        return cashAccount;
    }

    private ARRRequestv2.CustomerID deserializeCustomerID(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.CustomerID customerID = new ARRRequestv2.CustomerID();
        customerID.setValue(node.get("value").asText());
        return customerID;
    }

    private ARRRequestv2.Description deserializeDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.Description description = new ARRRequestv2.Description();
        description.setValue(node.get("value").asText());
        return description;
    }

    private ARRRequestv2.PaymentAmount deserializePaymentAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.PaymentAmount paymentAmount = new ARRRequestv2.PaymentAmount();
        paymentAmount.setValue(node.get("value").asDouble());
        return paymentAmount;
    }

    private ARRRequestv2.PaymentMethod deserializePaymentMethod(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.PaymentMethod paymentMethod = new ARRRequestv2.PaymentMethod();
        paymentMethod.setValue(node.get("value").asText());
        return paymentMethod;
    }

    private ARRRequestv2.PaymentRef deserializePaymentRef(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.PaymentRef paymentRef = new ARRRequestv2.PaymentRef();
        paymentRef.setValue(node.get("value").asText());
        return paymentRef;
    }

    private ARRRequestv2.Custom deserializeCustom(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.Custom custom = new ARRRequestv2.Custom();
        custom.setCurrentDocument(deserializeCurrentDocument(node.get("CurrentDocument")));
        return custom;
    }

    private ARRRequestv2.Custom.CurrentDocument deserializeCurrentDocument(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.Custom.CurrentDocument currentDocument = new ARRRequestv2.Custom.CurrentDocument();
        currentDocument.setAttributeSYSNAME(deserializeAttributeSYSNAME(node.get("AttributeSYSNAME")));
        currentDocument.setAttributeDOCNO(deserializeAttributeDOCNO(node.get("AttributeDOCNO")));
        return currentDocument;
    }

    private ARRRequestv2.Custom.CurrentDocument.AttributeSYSNAME deserializeAttributeSYSNAME(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.Custom.CurrentDocument.AttributeSYSNAME attributeSYSNAME = new ARRRequestv2.Custom.CurrentDocument.AttributeSYSNAME();
        attributeSYSNAME.setType(node.get("type").asText());
        attributeSYSNAME.setValue(node.get("value").asText());
        return attributeSYSNAME;
    }

    private ARRRequestv2.Custom.CurrentDocument.AttributeDOCNO deserializeAttributeDOCNO(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.Custom.CurrentDocument.AttributeDOCNO attributeDOCNO = new ARRRequestv2.Custom.CurrentDocument.AttributeDOCNO();
        attributeDOCNO.setType(node.get("type").asText());
        attributeDOCNO.setValue(node.get("value").asText());
        return attributeDOCNO;
    }

    private List<ARRRequestv2.DocumentToApply> deserializeDocumentsToApply(JsonNode node) {
        List<ARRRequestv2.DocumentToApply> documentsToApply = new ArrayList<>();
        if (node == null || !node.isArray()) {
            return documentsToApply;
        }
        for (JsonNode docNode : node) {
            ARRRequestv2.DocumentToApply documentToApply = new ARRRequestv2.DocumentToApply();
            documentToApply.setDocType(deserializeDocType(docNode.get("DocType")));
            documentToApply.setReferenceNbr(deserializeReferenceNbr(docNode.get("ReferenceNbr")));
            documentsToApply.add(documentToApply);
        }
        return documentsToApply;
    }

    private ARRRequestv2.DocumentToApply.DocType deserializeDocType(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.DocumentToApply.DocType docType = new ARRRequestv2.DocumentToApply.DocType();
        docType.setValue(node.get("value").asText());
        return docType;
    }

    private ARRRequestv2.DocumentToApply.ReferenceNbr deserializeReferenceNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.DocumentToApply.ReferenceNbr referenceNbr = new ARRRequestv2.DocumentToApply.ReferenceNbr();
        referenceNbr.setValue(node.get("value").asText());
        return referenceNbr;
    }


    // private ARRRequestv2.Charges deserializeCharges(JsonNode node) {
    //     if (node == null || !node.isArray()) {
    //         return null;
    //     }
    //     ARRRequestv2.Charges charges = new ARRRequestv2.Charges();

    //     charges.setAmount(deserializeAmount(node.get("Amount")));
    //     charges.setDocType(deserializeDocTypeCharges(node.get("DocType")));
    //     charges.setEntityType(deserializeEntityTypeCharges(node.get("EntityType")));
    //     charges.setOffsetSubaccount(deserializeOffsetSubAccountCharges(node.get("OffsetSubaccount")));

    //     return charges;
    // }

    private List<ARRRequestv2.Charges> deserializeCharges(JsonNode node) {
        List<ARRRequestv2.Charges> documentsToApply = new ArrayList<>();
        if (node == null || !node.isArray()) {
            return documentsToApply;
        }
        for (JsonNode docNode : node) {
            ARRRequestv2.Charges charges = new ARRRequestv2.Charges();
            charges.setAccountID(deserializeAccountID(docNode.get("AccountID")));
            charges.setAmount(deserializeAmount(docNode.get("Amount")));
            charges.setDocType(deserializeDocTypeCharges(docNode.get("DocType")));
            charges.setEntityType(deserializeEntityTypeCharges(docNode.get("EntityType")));
            charges.setOffsetSubaccount(deserializeOffsetSubAccountCharges(docNode.get("OffsetSubaccount")));
            documentsToApply.add(charges);
        }
        return documentsToApply;
    }

    private ARRRequestv2.Charges.AccountID deserializeAccountID(JsonNode node) {
        if (node == null) {
            return null;
        }

        ARRRequestv2.Charges.AccountID accountID = new ARRRequestv2.Charges.AccountID();
        accountID.setValue(node.get("value").asText());
        return accountID;
    }

    private ARRRequestv2.Charges.Amount deserializeAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.Charges.Amount amount = new ARRRequestv2.Charges.Amount();
        amount.setValue(node.get("value").asDouble());
        return amount;
    }

    private ARRRequestv2.Charges.DocType deserializeDocTypeCharges(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.Charges.DocType docType = new ARRRequestv2.Charges.DocType();
        docType.setValue(node.get("value").asText());
        return docType;
    }

    private ARRRequestv2.Charges.EntityType deserializeEntityTypeCharges(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.Charges.EntityType entityType = new ARRRequestv2.Charges.EntityType();
        entityType.setValue(node.get("value").asText());
        return entityType;
    }

    private ARRRequestv2.Charges.OffsetSubaccount deserializeOffsetSubAccountCharges(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARRRequestv2.Charges.OffsetSubaccount offsetSubaccount = new ARRRequestv2.Charges.OffsetSubaccount();
        offsetSubaccount.setValue(node.get("value").asText());
        return offsetSubaccount;
    }
}