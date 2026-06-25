package com.example.fms.fms.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class DebitRequestDeserializer extends JsonDeserializer<DebitRequest> {

    @Override
    public DebitRequest deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        // Deserialize basic fields
        DebitRequest.Type type = deserializeType(node.get("Type"));
        DebitRequest.LinkBranch linkBranch = deserializeLinkBranch(node.get("LinkBranch"));
        DebitRequest.Amount amount = deserializeAmount(node.get("Amount"));
        DebitRequest.CustomerID customer = deserializeCustomerID(node.get("CustomerID"));
        DebitRequest.CustomerOrder customerOrder = deserializeCustomerOrder(node.get("CustomerOrder"));
        DebitRequest.Date date = deserializeDate(node.get("Date"));
        DebitRequest.Description description = deserializeDescription(node.get("Description"));
        DebitRequest.Hold hold = deserializeHold(node.get("Hold"));
        DebitRequest.Custom custom = deserializeCustom(node.get("custom"));
        // Deserialize lists
        List<DebitRequest.Detail> details = deserializeDetails(node.get("Details"));
        List<DebitRequest.DocumentsToApply> documentsToApply = deserializeDocumentsToApply(node.get("DocumentsToApply"));

        // Create and populate the DebitRequest object
        DebitRequest debitRequest = new DebitRequest();
        debitRequest.setType(type);
        debitRequest.setLinkBranch(linkBranch);
        debitRequest.setAmount(amount);
        debitRequest.setCustomer(customer);
        debitRequest.setCustomerOrder(customerOrder);
        debitRequest.setDate(date);
        debitRequest.setDescription(description);
        debitRequest.setHold(hold);
        debitRequest.setCustom(custom);
        debitRequest.setDetails(details);
        debitRequest.setDocumentsToApply(documentsToApply);

        return debitRequest;
    }

    private List<DebitRequest.DocumentsToApply> deserializeDocumentsToApply(JsonNode node) {
        List<DebitRequest.DocumentsToApply> documentsToApply = new ArrayList<>();
        if (node == null || !node.isArray()) {
            return documentsToApply;
        }
        for (JsonNode docNode : node) {
            DebitRequest.DocumentsToApply documentToApply = new DebitRequest.DocumentsToApply();
            documentToApply.setReferenceNbr(deserializeReferenceNbr(docNode.get("ReferenceNbr")));
            documentToApply.setAmountPaid(deserializeAmountPaid(docNode.get("AmountPaid")));
            documentToApply.setDocType(deserializeDocType(docNode.get("DocType")));

            documentsToApply.add(documentToApply);
        }
        return documentsToApply;
    }


    private DebitRequest.DocumentsToApply.DocType deserializeDocType(JsonNode node) {
        if (node == null) {
            return null;
        }
        DebitRequest.DocumentsToApply.DocType docType = new DebitRequest.DocumentsToApply.DocType();
        docType.setValue(node.get("value").asText());
        return docType;
    }

    private DebitRequest.DocumentsToApply.AmountPaid deserializeAmountPaid(JsonNode node) {
        if (node == null) {
            return null;
        }
        DebitRequest.DocumentsToApply.AmountPaid amountPaid = new DebitRequest.DocumentsToApply.AmountPaid();
        amountPaid.setValue(node.get("value").asDouble());
        return amountPaid;
    }

    private DebitRequest.DocumentsToApply.ReferenceNbr deserializeReferenceNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        DebitRequest.DocumentsToApply.ReferenceNbr referenceNbr = new DebitRequest.DocumentsToApply.ReferenceNbr();
        referenceNbr.setValue(node.get("value").asText());
        return referenceNbr;
    }

    private DebitRequest.Type deserializeType(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Type type = new DebitRequest.Type();
        type.setValue(value);
        return type;
    }

    private DebitRequest.LinkBranch deserializeLinkBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.LinkBranch linkBranch = new DebitRequest.LinkBranch();
        linkBranch.setValue(value);
        return linkBranch;
    }

    private DebitRequest.Amount deserializeAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        DebitRequest.Amount amount = new DebitRequest.Amount();
        amount.setValue(value);
        return amount;
    }

    private DebitRequest.CustomerID deserializeCustomerID(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.CustomerID customer = new DebitRequest.CustomerID();
        customer.setValue(value);
        return customer;
    }

    private DebitRequest.CustomerOrder deserializeCustomerOrder(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.CustomerOrder customerOrder = new DebitRequest.CustomerOrder();
        customerOrder.setValue(value);
        return customerOrder;
    }

    private DebitRequest.Date deserializeDate(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Date date = new DebitRequest.Date();
        date.setValue(value);
        return date;
    }

    private DebitRequest.Description deserializeDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Description description = new DebitRequest.Description();
        description.setValue(value);
        return description;
    }

    private DebitRequest.Hold deserializeHold(JsonNode node) {
        if (node == null) {
            return null;
        }
        boolean value = node.get("value").asBoolean();
        DebitRequest.Hold hold = new DebitRequest.Hold();
        hold.setValue(value);
        return hold;
    }

    private DebitRequest.Custom deserializeCustom(JsonNode node) {
        if (node == null) {
            return null;
        }
        DebitRequest.Custom custom = new DebitRequest.Custom();
        JsonNode currentDocumentNode = node.get("CurrentDocument");
        if (currentDocumentNode != null) {
            DebitRequest.Custom.CurrentDocument currentDocument = new DebitRequest.Custom.CurrentDocument();
            JsonNode attributeSYSNAMENode = currentDocumentNode.get("AttributeSYSNAME");
            if (attributeSYSNAMENode != null) {
                DebitRequest.Custom.CurrentDocument.AttributeSYSNAME attributeSYSNAME = new DebitRequest.Custom.CurrentDocument.AttributeSYSNAME();
                attributeSYSNAME.setType(attributeSYSNAMENode.get("type").asText());
                attributeSYSNAME.setValue(attributeSYSNAMENode.get("value").asText());
                currentDocument.setAttributeSYSNAME(attributeSYSNAME);
            }
            custom.setCurrentDocument(currentDocument);
        }
        return custom;
    }

    private List<DebitRequest.Detail> deserializeDetails(JsonNode node) {
        if (node == null || !node.isArray()) {
            return new ArrayList<>();
        }
        List<DebitRequest.Detail> details = new ArrayList<>();
        for (JsonNode detailNode : node) {
            DebitRequest.Detail detail = new DebitRequest.Detail();
            detail.setLineNbr(deserializeLineNbr(detailNode.get("LineNbr")));
            detail.setBranch(deserializeBranch(detailNode.get("Branch")));
            detail.setChartofAccount1(deserializeChartofAccount1(detailNode.get("ChartofAccount1")));
            detail.setChartofAccount2(deserializeChartofAccount2(detailNode.get("ChartofAccount2")));
            detail.setSubaccount(deserializeSubaccount(detailNode.get("Subaccount")));
            detail.setTransactionDescription(deserializeTransactionDescription(detailNode.get("TransactionDescription")));
            detail.setQty(deserializeQty(detailNode.get("Qty")));
            detail.setUnitPrice(deserializeUnitPrice(detailNode.get("UnitPrice")));
            detail.setDiscountAmt(deserializeDiscountAmt(detailNode.get("DiscountAmt")));
            // 241010: Added 2 new fields, depositID and depositTask. 
            detail.setDepositID(deserializeDepositID(detailNode.get("DepositID")));
            detail.setDepositTask(deserializeDepositTask(detailNode.get("DepositTask")));
            detail.setEntityName(deserializeEntityName(detailNode.get("EntityName")));
            detail.setEntityNumber(deserializeEntityNumber(detailNode.get("EntityNumber")));
            detail.setEntityType(deserializeEntityType(detailNode.get("EntityType")));
            
            details.add(detail);
        }
        return details;
    }

    private DebitRequest.Detail.LineNbr deserializeLineNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        int value = node.get("value").asInt();
        DebitRequest.Detail.LineNbr lineNbr = new DebitRequest.Detail.LineNbr();
        lineNbr.setValue(value);
        return lineNbr;
    }

    private DebitRequest.Detail.Branch deserializeBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Detail.Branch branch = new DebitRequest.Detail.Branch();
        branch.setValue(value);
        return branch;
    }

    private DebitRequest.Detail.ChartofAccount1 deserializeChartofAccount1(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Detail.ChartofAccount1 chartofAccount1 = new DebitRequest.Detail.ChartofAccount1();
        chartofAccount1.setValue(value);
        return chartofAccount1;
    }

    private DebitRequest.Detail.ChartofAccount2 deserializeChartofAccount2(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Detail.ChartofAccount2 chartofAccount2 = new DebitRequest.Detail.ChartofAccount2();
        chartofAccount2.setValue(value);
        return chartofAccount2;
    }

    private DebitRequest.Detail.Subaccount deserializeSubaccount(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Detail.Subaccount subaccount = new DebitRequest.Detail.Subaccount();
        subaccount.setValue(value);
        return subaccount;
    }

    private DebitRequest.Detail.TransactionDescription deserializeTransactionDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Detail.TransactionDescription transactionDescription = new DebitRequest.Detail.TransactionDescription();
        transactionDescription.setValue(value);
        return transactionDescription;
    }

    private DebitRequest.Detail.Qty deserializeQty(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        DebitRequest.Detail.Qty qty = new DebitRequest.Detail.Qty();
        qty.setValue(value);
        return qty;
    }

    private DebitRequest.Detail.UnitPrice deserializeUnitPrice(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        DebitRequest.Detail.UnitPrice unitPrice = new DebitRequest.Detail.UnitPrice();
        unitPrice.setValue(value);
        return unitPrice;
    }

    private DebitRequest.Detail.DiscountAmt deserializeDiscountAmt(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        DebitRequest.Detail.DiscountAmt discountAmt = new DebitRequest.Detail.DiscountAmt();
        discountAmt.setValue(value);
        return discountAmt;
    }

    private DebitRequest.Detail.EntityName deserializeEntityName(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Detail.EntityName entityName = new DebitRequest.Detail.EntityName();
        entityName.setValue(value);
        return entityName;
    }

    private DebitRequest.Detail.EntityNumber deserializeEntityNumber(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Detail.EntityNumber entityNumber = new DebitRequest.Detail.EntityNumber();
        entityNumber.setValue(value);
        return entityNumber;
    }

    private DebitRequest.Detail.EntityType deserializeEntityType(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Detail.EntityType entityType = new DebitRequest.Detail.EntityType();
        entityType.setValue(value);
        return entityType;
    }

    private DebitRequest.Detail.DepositID deserializeDepositID(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Detail.DepositID depositID = new DebitRequest.Detail.DepositID();
        depositID.setValue(value);
        return depositID;
    }

    private DebitRequest.Detail.DepositTask deserializeDepositTask(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequest.Detail.DepositTask depositTask = new DebitRequest.Detail.DepositTask();
        depositTask.setValue(value);
        return depositTask;
    }
}
