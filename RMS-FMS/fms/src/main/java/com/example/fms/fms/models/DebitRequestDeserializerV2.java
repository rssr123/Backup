package com.example.fms.fms.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class DebitRequestDeserializerV2 extends JsonDeserializer<DebitRequestV2> {

    @Override
    public DebitRequestV2 deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        // Deserialize basic fields
        DebitRequestV2.Type type = deserializeType(node.get("Type"));
        DebitRequestV2.LinkBranch linkBranch = deserializeLinkBranch(node.get("LinkBranch"));
        DebitRequestV2.Amount amount = deserializeAmount(node.get("Amount"));
        DebitRequestV2.CustomerID customer = deserializeCustomer(node.get("CustomerID"));
        DebitRequestV2.CustomerOrder customerOrder = deserializeCustomerOrder(node.get("CustomerOrder"));
        DebitRequestV2.Date date = deserializeDate(node.get("Date"));
        DebitRequestV2.Description description = deserializeDescription(node.get("Description"));
        DebitRequestV2.Hold hold = deserializeHold(node.get("Hold"));
        DebitRequestV2.Custom custom = deserializeCustom(node.get("custom"));
        List<DebitRequestV2.DocumentsToApply> documentsToApply = deserializeDocumentsToApply(node.get("DocumentsToApply"));
        List<DebitRequestV2.Detail> details = deserializeDetails(node.get("Details"));

        // Create and populate the DebitRequestV2 object
        DebitRequestV2 debitRequest = new DebitRequestV2();
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

    private List<DebitRequestV2.DocumentsToApply> deserializeDocumentsToApply(JsonNode node) {
        List<DebitRequestV2.DocumentsToApply> documentsToApply = new ArrayList<>();
        if (node == null || !node.isArray()) {
            return documentsToApply;
        }
        for (JsonNode docNode : node) {
            DebitRequestV2.DocumentsToApply documentToApply = new DebitRequestV2.DocumentsToApply();
            documentToApply.setReferenceNbr(deserializeReferenceNbr(docNode.get("ReferenceNbr")));
            documentToApply.setAmountPaid(deserializeAmountPaid(docNode.get("AmountPaid")));
            documentToApply.setDocType(deserializeDocType(docNode.get("DocType")));

            documentsToApply.add(documentToApply);
        }
        return documentsToApply;
    }


    private DebitRequestV2.DocumentsToApply.DocType deserializeDocType(JsonNode node) {
        if (node == null) {
            return null;
        }
        DebitRequestV2.DocumentsToApply.DocType docType = new DebitRequestV2.DocumentsToApply.DocType();
        docType.setValue(node.get("value").asText());
        return docType;
    }

    private DebitRequestV2.DocumentsToApply.AmountPaid deserializeAmountPaid(JsonNode node) {
        if (node == null) {
            return null;
        }
        DebitRequestV2.DocumentsToApply.AmountPaid amountPaid = new DebitRequestV2.DocumentsToApply.AmountPaid();
        amountPaid.setValue(node.get("value").asDouble());
        return amountPaid;
    }

    private DebitRequestV2.DocumentsToApply.ReferenceNbr deserializeReferenceNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        DebitRequestV2.DocumentsToApply.ReferenceNbr referenceNbr = new DebitRequestV2.DocumentsToApply.ReferenceNbr();
        referenceNbr.setValue(node.get("value").asText());
        return referenceNbr;
    }

    private DebitRequestV2.Type deserializeType(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Type type = new DebitRequestV2.Type();
        type.setValue(value);
        return type;
    }

    private DebitRequestV2.LinkBranch deserializeLinkBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.LinkBranch linkBranch = new DebitRequestV2.LinkBranch();
        linkBranch.setValue(value);
        return linkBranch;
    }

    private DebitRequestV2.Amount deserializeAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        DebitRequestV2.Amount amount = new DebitRequestV2.Amount();
        amount.setValue(value);
        return amount;
    }

    private DebitRequestV2.CustomerID deserializeCustomer(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.CustomerID customer = new DebitRequestV2.CustomerID();
        customer.setValue(value);
        return customer;
    }

    private DebitRequestV2.CustomerOrder deserializeCustomerOrder(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.CustomerOrder customerOrder = new DebitRequestV2.CustomerOrder();
        customerOrder.setValue(value);
        return customerOrder;
    }

    private DebitRequestV2.Date deserializeDate(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Date date = new DebitRequestV2.Date();
        date.setValue(value);
        return date;
    }

    private DebitRequestV2.Description deserializeDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Description description = new DebitRequestV2.Description();
        description.setValue(value);
        return description;
    }

    private DebitRequestV2.Hold deserializeHold(JsonNode node) {
        if (node == null) {
            return null;
        }
        boolean value = node.get("value").asBoolean();
        DebitRequestV2.Hold hold = new DebitRequestV2.Hold();
        hold.setValue(value);
        return hold;
    }

    private DebitRequestV2.Custom deserializeCustom(JsonNode node) {
        if (node == null) {
            return null;
        }
        DebitRequestV2.Custom custom = new DebitRequestV2.Custom();
        JsonNode currentDocumentNode = node.get("CurrentDocument");
        if (currentDocumentNode != null) {
            DebitRequestV2.Custom.CurrentDocument currentDocument = new DebitRequestV2.Custom.CurrentDocument();
            JsonNode attributeSYSNAMENode = currentDocumentNode.get("AttributeSYSNAME");
            JsonNode attributeGENPDF = currentDocumentNode.get("AttributeGENPDF");
            if (attributeSYSNAMENode != null) {
                DebitRequestV2.Custom.CurrentDocument.AttributeSYSNAME attributeSYSNAME = new DebitRequestV2.Custom.CurrentDocument.AttributeSYSNAME();
                attributeSYSNAME.setType(attributeSYSNAMENode.get("type").asText());
                attributeSYSNAME.setValue(attributeSYSNAMENode.get("value").asText());
                currentDocument.setAttributeSYSNAME(attributeSYSNAME);
            }
            if (attributeGENPDF != null) {
                DebitRequestV2.Custom.CurrentDocument.AttributeGENPDF attributeGENPDFObj = new DebitRequestV2.Custom.CurrentDocument.AttributeGENPDF();
                attributeGENPDFObj.setType(attributeGENPDF.get("type").asText());
                attributeGENPDFObj.setValue(attributeGENPDF.get("value").asBoolean());
                currentDocument.setAttributeGENPDF(attributeGENPDFObj);
            }
            custom.setCurrentDocument(currentDocument);
        }
        return custom;
    }

    private List<DebitRequestV2.Detail> deserializeDetails(JsonNode node) {
        if (node == null || !node.isArray()) {
            return new ArrayList<>();
        }
        List<DebitRequestV2.Detail> details = new ArrayList<>();
        for (JsonNode detailNode : node) {
            DebitRequestV2.Detail detail = new DebitRequestV2.Detail();
            detail.setLineNbr(deserializeLineNbr(detailNode.get("LineNbr")));
            detail.setBranch(deserializeBranch(detailNode.get("Branch")));
            detail.setChartofAccount1(deserializeChartofAccount1(detailNode.get("ChartofAccount1")));
            detail.setChartofAccount2(deserializeChartofAccount2(detailNode.get("ChartofAccount2")));
            detail.setSubaccount(deserializeSubaccount(detailNode.get("Subaccount")));
            detail.setTransactionDescription(deserializeTransactionDescription(detailNode.get("TransactionDescription")));
            detail.setQty(deserializeQty(detailNode.get("Qty")));
            detail.setUnitPrice(deserializeUnitPrice(detailNode.get("UnitPrice")));
            // 250405: New field added in details JSON, discount_amt
            detail.setDiscountAmount(deserializeDiscountAmount(detailNode.get("DiscountAmt")));
            // 241010: Added 2 new fields, depositID and depositTask. 
            detail.setDepositID(deserializeDepositID(detailNode.get("DepositID")));
            detail.setDepositTask(deserializeDepositTask(detailNode.get("DepositTask")));
            // detail.setEntityName(deserializeEntityName(detailNode.get("EntityName")));
            // detail.setEntityNumber(deserializeEntityNumber(detailNode.get("EntityNumber")));
            // detail.setEntityType(deserializeEntityType(detailNode.get("EntityType")));
            details.add(detail);
        }
        return details;
    }

    private DebitRequestV2.Detail.LineNbr deserializeLineNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        int value = node.get("value").asInt();
        DebitRequestV2.Detail.LineNbr lineNbr = new DebitRequestV2.Detail.LineNbr();
        lineNbr.setValue(value);
        return lineNbr;
    }

    private DebitRequestV2.Detail.Branch deserializeBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Detail.Branch branch = new DebitRequestV2.Detail.Branch();
        branch.setValue(value);
        return branch;
    }

    private DebitRequestV2.Detail.ChartofAccount1 deserializeChartofAccount1(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Detail.ChartofAccount1 chartofAccount1 = new DebitRequestV2.Detail.ChartofAccount1();
        chartofAccount1.setValue(value);
        return chartofAccount1;
    }

    private DebitRequestV2.Detail.ChartofAccount2 deserializeChartofAccount2(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Detail.ChartofAccount2 chartofAccount2 = new DebitRequestV2.Detail.ChartofAccount2();
        chartofAccount2.setValue(value);
        return chartofAccount2;
    }

    private DebitRequestV2.Detail.Subaccount deserializeSubaccount(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Detail.Subaccount subaccount = new DebitRequestV2.Detail.Subaccount();
        subaccount.setValue(value);
        return subaccount;
    }

    private DebitRequestV2.Detail.TransactionDescription deserializeTransactionDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Detail.TransactionDescription transactionDescription = new DebitRequestV2.Detail.TransactionDescription();
        transactionDescription.setValue(value);
        return transactionDescription;
    }

    private DebitRequestV2.Detail.Qty deserializeQty(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        DebitRequestV2.Detail.Qty qty = new DebitRequestV2.Detail.Qty();
        qty.setValue(value);
        return qty;
    }

    private DebitRequestV2.Detail.UnitPrice deserializeUnitPrice(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        DebitRequestV2.Detail.UnitPrice unitPrice = new DebitRequestV2.Detail.UnitPrice();
        unitPrice.setValue(value);
        return unitPrice;
    }

    private DebitRequestV2.Detail.DiscountAmount deserializeDiscountAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        DebitRequestV2.Detail.DiscountAmount discountAmount = new DebitRequestV2.Detail.DiscountAmount();
        discountAmount.setValue(value);
        return discountAmount;
    }

    private DebitRequestV2.Detail.EntityName deserializeEntityName(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Detail.EntityName entityName = new DebitRequestV2.Detail.EntityName();
        entityName.setValue(value);
        return entityName;
    }

    private DebitRequestV2.Detail.EntityNumber deserializeEntityNumber(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Detail.EntityNumber entityNumber = new DebitRequestV2.Detail.EntityNumber();
        entityNumber.setValue(value);
        return entityNumber;
    }

    private DebitRequestV2.Detail.EntityType deserializeEntityType(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Detail.EntityType entityType = new DebitRequestV2.Detail.EntityType();
        entityType.setValue(value);
        return entityType;
    }

    private DebitRequestV2.Detail.DepositID deserializeDepositID(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Detail.DepositID depositID = new DebitRequestV2.Detail.DepositID();
        depositID.setValue(value);
        return depositID;
    }

    private DebitRequestV2.Detail.DepositTask deserializeDepositTask(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        DebitRequestV2.Detail.DepositTask depositTask = new DebitRequestV2.Detail.DepositTask();
        depositTask.setValue(value);
        return depositTask;
    }
}
