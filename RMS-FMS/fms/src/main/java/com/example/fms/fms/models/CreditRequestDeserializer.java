package com.example.fms.fms.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreditRequestDeserializer extends JsonDeserializer<CreditRequest> {

    @Override
    public CreditRequest deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        // Deserialize basic fields
        CreditRequest.Type type = deserializeType(node.get("Type"));
        CreditRequest.LinkBranch linkBranch = deserializeLinkBranch(node.get("LinkBranch"));
        CreditRequest.Amount amount = deserializeAmount(node.get("Amount"));
        CreditRequest.CustomerID customer = deserializeCustomerID(node.get("CustomerID"));
        CreditRequest.CustomerOrder customerOrder = deserializeCustomerOrder(node.get("CustomerOrder"));
        CreditRequest.Project project = deserializeProject(node.get("Project"));
        CreditRequest.Date date = deserializeDate(node.get("Date"));
        CreditRequest.Description description = deserializeDescription(node.get("Description"));
        CreditRequest.Hold hold = deserializeHold(node.get("Hold"));
        CreditRequest.Custom custom = deserializeCustom(node.get("custom"));

        // Deserialize lists
        List<CreditRequest.Detail> details = deserializeDetails(node.get("Details"));
        List<CreditRequest.ApplicationsCreditMemo> applicationsCreditMemo = deserializeApplicationsCreditMemo(node.get("ApplicationsCreditMemo"));

        // Create and populate the CreditRequest object
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setType(type);
        creditRequest.setLinkBranch(linkBranch);
        creditRequest.setAmount(amount);
        creditRequest.setCustomerID(customer);
        creditRequest.setCustomerOrder(customerOrder);
        creditRequest.setProject(project);
        creditRequest.setDate(date);
        creditRequest.setDescription(description);
        creditRequest.setHold(hold);
        creditRequest.setCustom(custom);
        creditRequest.setDetails(details);
        creditRequest.setApplicationsCreditMemo(applicationsCreditMemo);

        return creditRequest;
    }

    private CreditRequest.Type deserializeType(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Type type = new CreditRequest.Type();
        type.setValue(value);
        return type;
    }

    private CreditRequest.LinkBranch deserializeLinkBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.LinkBranch linkBranch = new CreditRequest.LinkBranch();
        linkBranch.setValue(value);
        return linkBranch;
    }

    private CreditRequest.Amount deserializeAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        int value = node.get("value").asInt();
        CreditRequest.Amount amount = new CreditRequest.Amount();
        amount.setValue(value);
        return amount;
    }

    private CreditRequest.CustomerID deserializeCustomerID(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.CustomerID customer = new CreditRequest.CustomerID();
        customer.setValue(value);
        return customer;
    }

    private CreditRequest.CustomerOrder deserializeCustomerOrder(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.CustomerOrder customerOrder = new CreditRequest.CustomerOrder();
        customerOrder.setValue(value);
        return customerOrder;
    }

    private CreditRequest.Project deserializeProject(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Project project = new CreditRequest.Project();
        project.setValue(value);
        return project;
    }

    private CreditRequest.Date deserializeDate(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Date date = new CreditRequest.Date();
        date.setValue(value);
        return date;
    }

    private CreditRequest.Description deserializeDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Description description = new CreditRequest.Description();
        description.setValue(value);
        return description;
    }

    private CreditRequest.Hold deserializeHold(JsonNode node) {
        if (node == null) {
            return null;
        }
        boolean value = node.get("value").asBoolean();
        CreditRequest.Hold hold = new CreditRequest.Hold();
        hold.setValue(value);
        return hold;
    }

    private CreditRequest.Custom deserializeCustom(JsonNode node) {
        if (node == null) {
            return null;
        }
        CreditRequest.Custom custom = new CreditRequest.Custom();
        JsonNode currentDocumentNode = node.get("CurrentDocument");
        if (currentDocumentNode != null) {
            CreditRequest.Custom.CurrentDocument currentDocument = new CreditRequest.Custom.CurrentDocument();
            JsonNode attributeSYSNAMENode = currentDocumentNode.get("AttributeSYSNAME");
            if (attributeSYSNAMENode != null) {
                CreditRequest.Custom.CurrentDocument.AttributeSYSNAME attributeSYSNAME = new CreditRequest.Custom.CurrentDocument.AttributeSYSNAME();
                attributeSYSNAME.setType(attributeSYSNAMENode.get("type").asText());
                attributeSYSNAME.setValue(attributeSYSNAMENode.get("value").asText());
                currentDocument.setAttributeSYSNAME(attributeSYSNAME);
            }
            custom.setCurrentDocument(currentDocument);
        }
        return custom;
    }

    private List<CreditRequest.Detail> deserializeDetails(JsonNode node) {
        if (node == null || !node.isArray()) {
            return new ArrayList<>();
        }
        List<CreditRequest.Detail> details = new ArrayList<>();
        for (JsonNode detailNode : node) {
            CreditRequest.Detail detail = new CreditRequest.Detail();
            detail.setAccount(deserializeAccount(detailNode.get("Account")));
            detail.setChartofAccount1(deserializeChartofAccount1(detailNode.get("ChartofAccount1")));
            detail.setChartofAccount2(deserializeChartofAccount2(detailNode.get("ChartofAccount2")));
            detail.setBranch(deserializeBranch(detailNode.get("Branch")));
            detail.setLineNbr(deserializeLineNbr(detailNode.get("LineNbr")));
            detail.setQty(deserializeQty(detailNode.get("Qty")));
            detail.setSubaccount(deserializeSubaccount(detailNode.get("Subaccount")));
            detail.setTransactionDescription(deserializeTransactionDescription(detailNode.get("TransactionDescription")));
            detail.setUnitPrice(deserializeUnitPrice(detailNode.get("UnitPrice")));
            detail.setDepositID(deserializeDepositID(detailNode.get("DepositID")));
            detail.setDepositTask(deserializeDepositTask(detailNode.get("DepositTask")));
            detail.setEntityName(deserializeEntityName(detailNode.get("EntityName")));
            detail.setEntityNumber(deserializeEntityNumber(detailNode.get("EntityNumber")));
            detail.setEntityType(deserializeEntityType(detailNode.get("EntityType")));
            details.add(detail);
        }
        return details;
    }

    private List<CreditRequest.ApplicationsCreditMemo> deserializeApplicationsCreditMemo(JsonNode node) {
        if (node == null || !node.isArray()) {
            return new ArrayList<>();
        }
        List<CreditRequest.ApplicationsCreditMemo> applicationsCreditMemo = new ArrayList<>();
        for (JsonNode applicationNode : node) {
            CreditRequest.ApplicationsCreditMemo application = new CreditRequest.ApplicationsCreditMemo();
            application.setDocType(deserializeDocType(applicationNode.get("DocType")));
            application.setReferenceNbr(deserializeReferenceNbr(applicationNode.get("ReferenceNbr")));
            application.setAmountPaid(deserializeAmountPaid(applicationNode.get("AmountPaid")));
            applicationsCreditMemo.add(application);
        }
        return applicationsCreditMemo;
    }

    private CreditRequest.Detail.LineNbr deserializeLineNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        int value = node.get("value").asInt();
        CreditRequest.Detail.LineNbr lineNbr = new CreditRequest.Detail.LineNbr();
        lineNbr.setValue(value);
        return lineNbr;
    }

    private CreditRequest.Detail.DepositID deserializeDepositID(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Detail.DepositID depositID = new CreditRequest.Detail.DepositID();
        depositID.setValue(value);
        return depositID;
    }

    private CreditRequest.Detail.DepositTask deserializeDepositTask(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Detail.DepositTask depositTask = new CreditRequest.Detail.DepositTask();
        depositTask.setValue(value);
        return depositTask;
    }

    private CreditRequest.Detail.Branch deserializeBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Detail.Branch branch = new CreditRequest.Detail.Branch();
        branch.setValue(value);
        return branch;
    }

    private CreditRequest.Detail.Account deserializeAccount(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Detail.Account account = new CreditRequest.Detail.Account();
        account.setValue(value);
        return account;
    }

    private CreditRequest.Detail.ChartofAccount1 deserializeChartofAccount1(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Detail.ChartofAccount1 chartofAccount1 = new CreditRequest.Detail.ChartofAccount1();
        chartofAccount1.setValue(value);
        return chartofAccount1;
    }

    private CreditRequest.Detail.ChartofAccount2 deserializeChartofAccount2(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Detail.ChartofAccount2 chartofAccount2 = new CreditRequest.Detail.ChartofAccount2();
        chartofAccount2.setValue(value);
        return chartofAccount2;
    }

    private CreditRequest.Detail.Subaccount deserializeSubaccount(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Detail.Subaccount subaccount = new CreditRequest.Detail.Subaccount();
        subaccount.setValue(value);
        return subaccount;
    }

    private CreditRequest.Detail.TransactionDescription deserializeTransactionDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Detail.TransactionDescription transactionDescription = new CreditRequest.Detail.TransactionDescription();
        transactionDescription.setValue(value);
        return transactionDescription;
    }

    private CreditRequest.Detail.Qty deserializeQty(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        CreditRequest.Detail.Qty qty = new CreditRequest.Detail.Qty();
        qty.setValue(value);
        return qty;
    }

    private CreditRequest.Detail.UnitPrice deserializeUnitPrice(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        CreditRequest.Detail.UnitPrice unitPrice = new CreditRequest.Detail.UnitPrice();
        unitPrice.setValue(value);
        return unitPrice;
    }

    private CreditRequest.Detail.EntityName deserializeEntityName(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Detail.EntityName entityName = new CreditRequest.Detail.EntityName();
        entityName.setValue(value);
        return entityName;
    }

    private CreditRequest.Detail.EntityNumber deserializeEntityNumber(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Detail.EntityNumber entityNumber = new CreditRequest.Detail.EntityNumber();
        entityNumber.setValue(value);
        return entityNumber;
    }

    private CreditRequest.Detail.EntityType deserializeEntityType(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.Detail.EntityType entityType = new CreditRequest.Detail.EntityType();
        entityType.setValue(value);
        return entityType;
    }

    private CreditRequest.ApplicationsCreditMemo.ReferenceNbr deserializeReferenceNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.ApplicationsCreditMemo.ReferenceNbr referenceNbr = new CreditRequest.ApplicationsCreditMemo.ReferenceNbr();
        referenceNbr.setValue(value);
        return referenceNbr;
    }

    private CreditRequest.ApplicationsCreditMemo.AmountPaid deserializeAmountPaid(JsonNode node) {
        if (node == null) {
            return null;
        }
        double value = node.get("value").asDouble();
        CreditRequest.ApplicationsCreditMemo.AmountPaid amountPaid = new CreditRequest.ApplicationsCreditMemo.AmountPaid();
        amountPaid.setValue(value);
        return amountPaid;
    }

    private CreditRequest.ApplicationsCreditMemo.DocType deserializeDocType(JsonNode node) {
        if (node == null) {
            return null;
        }
        String value = node.get("value").asText();
        CreditRequest.ApplicationsCreditMemo.DocType docType = new CreditRequest.ApplicationsCreditMemo.DocType();
        docType.setValue(value);
        return docType;
    }
}
