package com.example.fms.fms.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreditRequestDeserializerV2 extends JsonDeserializer<CreditRequestV2> {

    @Override
    public CreditRequestV2 deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        // Deserialize basic fields
        CreditRequestV2.Type type = deserializeType(node.get("Type"));
        CreditRequestV2.CustomerID customer = deserializeCustomerID(node.get("CustomerID"));
        CreditRequestV2.CustomerOrder customerOrder = deserializeCustomerOrder(node.get("CustomerOrder"));
        CreditRequestV2.Date date = deserializeDate(node.get("Date"));
        CreditRequestV2.Description description = deserializeDescription(node.get("Description"));
        CreditRequestV2.LinkBranch linkBranch = deserializeLinkBranch(node.get("LinkBranch"));
        CreditRequestV2.Amount amount = deserializeAmount(node.get("Amount"));
        List<CreditRequestV2.ApplicationsCreditMemo> applicationsCreditMemo = deserializeApplicationsCreditMemo(node.get("ApplicationsCreditMemo"));
        List<CreditRequestV2.Detail> details = deserializeDetails(node.get("Details"));
        CreditRequestV2.Custom custom = deserializeCustom(node.get("custom"));
        // CreditRequestV2.Project project = deserializeProject(node.get("Project"));
        // CreditRequestV2.Hold hold = deserializeHold(node.get("Hold"));
        // Deserialize lists

        // Create and populate the CreditRequestV2 object
        CreditRequestV2 creditRequest = new CreditRequestV2();
        creditRequest.setType(type);
        creditRequest.setCustomerID(customer);
        creditRequest.setCustomerOrder(customerOrder);
        creditRequest.setDate(date);
        creditRequest.setDescription(description);
        creditRequest.setLinkBranch(linkBranch);
        creditRequest.setAmount(amount);
        creditRequest.setApplicationsCreditMemo(applicationsCreditMemo);
        creditRequest.setDetails(details);
        creditRequest.setCustom(custom);
        // creditRequest.setProject(project);
        // creditRequest.setHold(hold);

        return creditRequest;
    }

    private CreditRequestV2.Type deserializeType(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Type type = new CreditRequestV2.Type();
        type.setValue(value);
        return type;
    }

    private CreditRequestV2.LinkBranch deserializeLinkBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.LinkBranch linkBranch = new CreditRequestV2.LinkBranch();
        linkBranch.setValue(value);
        return linkBranch;
    }

    private CreditRequestV2.Amount deserializeAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        int value = node.get("value").asInt();
        CreditRequestV2.Amount amount = new CreditRequestV2.Amount();
        amount.setValue(value);
        return amount;
    }

    private CreditRequestV2.CustomerID deserializeCustomerID(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.CustomerID customer = new CreditRequestV2.CustomerID();
        customer.setValue(value);
        return customer;
    }

    private CreditRequestV2.CustomerOrder deserializeCustomerOrder(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.CustomerOrder customerOrder = new CreditRequestV2.CustomerOrder();
        customerOrder.setValue(value);
        return customerOrder;
    }

    private CreditRequestV2.Project deserializeProject(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Project project = new CreditRequestV2.Project();
        project.setValue(value);
        return project;
    }

    private CreditRequestV2.Date deserializeDate(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Date date = new CreditRequestV2.Date();
        date.setValue(value);
        return date;
    }

    private CreditRequestV2.Description deserializeDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Description description = new CreditRequestV2.Description();
        description.setValue(value);
        return description;
    }

    private CreditRequestV2.Hold deserializeHold(JsonNode node) {
        if (node == null) {
            return null;
        }
        boolean value = node.get("value").asBoolean();
        CreditRequestV2.Hold hold = new CreditRequestV2.Hold();
        hold.setValue(value);
        return hold;
    }

    private CreditRequestV2.Custom deserializeCustom(JsonNode node) {
        if (node == null) {
            return null;
        }
        CreditRequestV2.Custom custom = new CreditRequestV2.Custom();
        JsonNode currentDocumentNode = node.get("CurrentDocument");
        if (currentDocumentNode != null) {
            CreditRequestV2.Custom.CurrentDocument currentDocument = new CreditRequestV2.Custom.CurrentDocument();
            JsonNode attributeSYSNAMENode = currentDocumentNode.get("AttributeSYSNAME");
            JsonNode attributeGENPDF = currentDocumentNode.get("AttributeGENPDF");
            if (attributeSYSNAMENode != null) {
                CreditRequestV2.Custom.CurrentDocument.AttributeSYSNAME attributeSYSNAME = new CreditRequestV2.Custom.CurrentDocument.AttributeSYSNAME();
                attributeSYSNAME.setType(attributeSYSNAMENode.get("type").asText());
                attributeSYSNAME.setValue(attributeSYSNAMENode.get("value").asText());
                currentDocument.setAttributeSYSNAME(attributeSYSNAME);
            }
            if (attributeGENPDF != null) {
                CreditRequestV2.Custom.CurrentDocument.AttributeGENPDF attributeGENPDFObj = new CreditRequestV2.Custom.CurrentDocument.AttributeGENPDF();
                attributeGENPDFObj.setType(attributeGENPDF.get("type").asText());
                attributeGENPDFObj.setValue(attributeGENPDF.get("value").asBoolean());
                currentDocument.setAttributeGENPDF(attributeGENPDFObj);
            }
            custom.setCurrentDocument(currentDocument);
        }
        return custom;
    }

    private List<CreditRequestV2.Detail> deserializeDetails(JsonNode node) {
        if (node == null || !node.isArray()) {
            return new ArrayList<>();
        }
        List<CreditRequestV2.Detail> details = new ArrayList<>();
        for (JsonNode detailNode : node) {
            CreditRequestV2.Detail detail = new CreditRequestV2.Detail();
            // detail.setAccount(deserializeAccount(detailNode.get("Account")));
            detail.setLineNbr(deserializeLineNbr(detailNode.get("LineNbr")));
            detail.setChartofAccount1(deserializeChartofAccount1(detailNode.get("ChartofAccount1")));
            detail.setChartofAccount2(deserializeChartofAccount2(detailNode.get("ChartofAccount2")));
            detail.setBranch(deserializeBranch(detailNode.get("Branch")));
            detail.setDiscountAmount(deserializeDiscountAmount(detailNode.get("DiscountAmount")));
            detail.setQty(deserializeQty(detailNode.get("Qty")));
            detail.setSubaccount(deserializeSubaccount(detailNode.get("Subaccount")));
            detail.setTransactionDescription(deserializeTransactionDescription(detailNode.get("TransactionDescription")));
            detail.setUnitPrice(deserializeUnitPrice(detailNode.get("UnitPrice")));
            detail.setDepositID(deserializeDepositID(detailNode.get("DepositID")));
            detail.setDepositTask(deserializeDepositTask(detailNode.get("DepositTask")));
            // detail.setEntityName(deserializeEntityName(detailNode.get("EntityName")));
            // detail.setEntityNumber(deserializeEntityNumber(detailNode.get("EntityNumber")));
            // detail.setEntityType(deserializeEntityType(detailNode.get("EntityType")));
            
            details.add(detail);
        }
        return details;
    }

    private List<CreditRequestV2.ApplicationsCreditMemo> deserializeApplicationsCreditMemo(JsonNode node) {
        if (node == null || !node.isArray()) {
            return new ArrayList<>();
        }
        List<CreditRequestV2.ApplicationsCreditMemo> applicationsCreditMemo = new ArrayList<>();
        for (JsonNode applicationNode : node) {
            CreditRequestV2.ApplicationsCreditMemo application = new CreditRequestV2.ApplicationsCreditMemo();
            application.setReferenceNbr(deserializeReferenceNbr(applicationNode.get("ReferenceNbr")));
            application.setAmountPaid(deserializeAmountPaid(applicationNode.get("AmountPaid")));
            application.setDocType(deserializeDocType(applicationNode.get("DocType")));
            applicationsCreditMemo.add(application);
        }
        return applicationsCreditMemo;
    }

    private CreditRequestV2.Detail.LineNbr deserializeLineNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        int value = node.get("value").asInt();
        CreditRequestV2.Detail.LineNbr lineNbr = new CreditRequestV2.Detail.LineNbr();
        lineNbr.setValue(value);
        return lineNbr;
    }

    private CreditRequestV2.Detail.DepositID deserializeDepositID(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Detail.DepositID depositID = new CreditRequestV2.Detail.DepositID();
        depositID.setValue(value);
        return depositID;
    }

    private CreditRequestV2.Detail.DepositTask deserializeDepositTask(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Detail.DepositTask depositTask = new CreditRequestV2.Detail.DepositTask();
        depositTask.setValue(value);
        return depositTask;
    }

    private CreditRequestV2.Detail.Branch deserializeBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Detail.Branch branch = new CreditRequestV2.Detail.Branch();
        branch.setValue(value);
        return branch;
    }

    private CreditRequestV2.Detail.DiscountAmount deserializeDiscountAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        CreditRequestV2.Detail.DiscountAmount discountAmount = new CreditRequestV2.Detail.DiscountAmount();
        discountAmount.setValue(value);
        return discountAmount;
    }

    private CreditRequestV2.Detail.ChartofAccount1 deserializeChartofAccount1(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Detail.ChartofAccount1 chartofAccount1 = new CreditRequestV2.Detail.ChartofAccount1();
        chartofAccount1.setValue(value);
        return chartofAccount1;
    }

    private CreditRequestV2.Detail.ChartofAccount2 deserializeChartofAccount2(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Detail.ChartofAccount2 chartofAccount2 = new CreditRequestV2.Detail.ChartofAccount2();
        chartofAccount2.setValue(value);
        return chartofAccount2;
    }

    private CreditRequestV2.Detail.Subaccount deserializeSubaccount(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Detail.Subaccount subaccount = new CreditRequestV2.Detail.Subaccount();
        subaccount.setValue(value);
        return subaccount;
    }

    private CreditRequestV2.Detail.TransactionDescription deserializeTransactionDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Detail.TransactionDescription transactionDescription = new CreditRequestV2.Detail.TransactionDescription();
        transactionDescription.setValue(value);
        return transactionDescription;
    }

    private CreditRequestV2.Detail.Qty deserializeQty(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        CreditRequestV2.Detail.Qty qty = new CreditRequestV2.Detail.Qty();
        qty.setValue(value);
        return qty;
    }

    private CreditRequestV2.Detail.UnitPrice deserializeUnitPrice(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        CreditRequestV2.Detail.UnitPrice unitPrice = new CreditRequestV2.Detail.UnitPrice();
        unitPrice.setValue(value);
        return unitPrice;
    }

    private CreditRequestV2.Detail.EntityName deserializeEntityName(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Detail.EntityName entityName = new CreditRequestV2.Detail.EntityName();
        entityName.setValue(value);
        return entityName;
    }

    private CreditRequestV2.Detail.EntityNumber deserializeEntityNumber(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Detail.EntityNumber entityNumber = new CreditRequestV2.Detail.EntityNumber();
        entityNumber.setValue(value);
        return entityNumber;
    }

    private CreditRequestV2.Detail.EntityType deserializeEntityType(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Detail.EntityType entityType = new CreditRequestV2.Detail.EntityType();
        entityType.setValue(value);
        return entityType;
    }

    private CreditRequestV2.Detail.Account deserializeAccount(JsonNode node) {
        if (node == null || node.get("value") == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.Detail.Account account = new CreditRequestV2.Detail.Account();
        account.setValue(value);
        return account;
    }

    private CreditRequestV2.ApplicationsCreditMemo.ReferenceNbr deserializeReferenceNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.ApplicationsCreditMemo.ReferenceNbr referenceNbr = new CreditRequestV2.ApplicationsCreditMemo.ReferenceNbr();
        referenceNbr.setValue(value);
        return referenceNbr;
    }

    private CreditRequestV2.ApplicationsCreditMemo.AmountPaid deserializeAmountPaid(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        CreditRequestV2.ApplicationsCreditMemo.AmountPaid amountPaid = new CreditRequestV2.ApplicationsCreditMemo.AmountPaid();
        amountPaid.setValue(value);
        return amountPaid;
    }

    private CreditRequestV2.ApplicationsCreditMemo.DocType deserializeDocType(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequestV2.ApplicationsCreditMemo.DocType docType = new CreditRequestV2.ApplicationsCreditMemo.DocType();
        docType.setValue(value);
        return docType;
    }
}
