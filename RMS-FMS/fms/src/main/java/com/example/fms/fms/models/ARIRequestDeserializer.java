package com.example.fms.fms.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ARIRequestDeserializer extends JsonDeserializer<ARIRequest> {

    @Override
    public ARIRequest deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        ARIRequest ariRequest = new ARIRequest();

        ariRequest.setType(deserializeType(node.get("Type")));
        ariRequest.setLinkBranch(deserializeLinkBranch(node.get("LinkBranch")));
        ariRequest.setAmount(deserializeAmount(node.get("Amount")));
        ariRequest.setCustomer(deserializeCustomer(node.get("CustomerID")));
        ariRequest.setCustomerOrder(deserializeCustomerOrder(node.get("CustomerOrder")));
        ariRequest.setDate(deserializeDate(node.get("Date")));
        ariRequest.setInv_dt(deserializeInvoiceDate(node.get("InvoiceDate")));
        ariRequest.setDescription(deserializeDescription(node.get("Description")));
        ariRequest.setCustom(deserializeCustom(node.get("custom")));
        ariRequest.setDetails(deserializeDetails(node.get("Details")));

        return ariRequest;
    }

    private ARIRequest.Type deserializeType(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Type type = new ARIRequest.Type();
        type.setValue(node.get("value").asText());
        return type;
    }

    private ARIRequest.LinkBranch deserializeLinkBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.LinkBranch linkBranch = new ARIRequest.LinkBranch();
        linkBranch.setValue(node.get("value").asText());
        return linkBranch;
    }

    private ARIRequest.Amount deserializeAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Amount amount = new ARIRequest.Amount();
        amount.setValue(node.get("value").asInt());
        return amount;
    }

    private ARIRequest.CustomerID deserializeCustomer(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.CustomerID customer = new ARIRequest.CustomerID();
        customer.setValue(node.get("value").asText());
        return customer;
    }

    private ARIRequest.CustomerOrder deserializeCustomerOrder(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.CustomerOrder customerOrder = new ARIRequest.CustomerOrder();
        customerOrder.setValue(node.get("value").asText());
        return customerOrder;
    }

    private ARIRequest.Date deserializeDate(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Date date = new ARIRequest.Date();
        date.setValue(node.get("value").asText());
        return date;
    }

    private ARIRequest.InvoiceDate deserializeInvoiceDate(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.InvoiceDate date = new ARIRequest.InvoiceDate();
        date.setValue(node.get("value").asText());
        return date;
    }

    private ARIRequest.Description deserializeDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Description description = new ARIRequest.Description();
        description.setValue(node.get("value").asText());
        return description;
    }

    private ARIRequest.Custom deserializeCustom(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Custom custom = new ARIRequest.Custom();
        JsonNode currentDocumentNode = node.get("CurrentDocument");
        if (currentDocumentNode != null) {
            ARIRequest.Custom.CurrentDocument currentDocument = new ARIRequest.Custom.CurrentDocument();
            JsonNode attributeSysnameNode = currentDocumentNode.get("AttributeSYSNAME");
            if (attributeSysnameNode != null) {
                ARIRequest.Custom.CurrentDocument.AttributeSYSNAME attributeSYSNAME = new ARIRequest.Custom.CurrentDocument.AttributeSYSNAME();
                attributeSYSNAME.setType(attributeSysnameNode.get("type").asText());
                attributeSYSNAME.setValue(attributeSysnameNode.get("value").asText());
                currentDocument.setAttributeSYSNAME(attributeSYSNAME);
            }
            custom.setCurrentDocument(currentDocument);
        }
        return custom;
    }

    private List<ARIRequest.Detail> deserializeDetails(JsonNode node) {
        if (node == null || !node.isArray()) {
            return null;
        }
        List<ARIRequest.Detail> details = new ArrayList<>();
        for (JsonNode detailNode : node) {
            ARIRequest.Detail detail = new ARIRequest.Detail();
            detail.setLineNbr(deserializeLineNbr(detailNode.get("LineNbr")));
            detail.setChartofAccount1(deserializeChartofAccount1(detailNode.get("ChartofAccount1")));
            detail.setChartofAccount2(deserializeChartofAccount2(detailNode.get("ChartofAccount2")));
            detail.setBranch(deserializeBranch(detailNode.get("Branch")));
            detail.setQty(deserializeQty(detailNode.get("Qty")));
            detail.setSubaccount(deserializeSubaccount(detailNode.get("Subaccount")));
            detail.setTransactionDescription(deserializeTransactionDescription(detailNode.get("TransactionDescription")));
            detail.setUnitPrice(deserializeUnitPrice(detailNode.get("UnitPrice")));
            detail.setReceiptNumber(deserializeReceiptNumber(detailNode.get("ReceiptNumber")));
            detail.setPayeeInfo(deserializePayeeInfo(detailNode.get("PayeeInfo")));
            detail.setEntityName(deserializeEntityName(detailNode.get("EntityName")));
            detail.setEntityNumber(deserializeEntityNumber(detailNode.get("EntityNumber")));
            detail.setEntityType(deserializeEntityType(detailNode.get("EntityType")));
            detail.setItemAmount(deserializeItemAmount(detailNode.get("ItemAmount")));
            detail.setPaymentMode(deserializePaymentMode(detailNode.get("PaymentMode")));
            detail.setItemTaxAmount(deserializeItemTaxAmount(detailNode.get("ItemTaxAmount")));
            detail.setDiscountAmount(deserializeDiscountAmount(detailNode.get("DiscountAmount")));
            detail.setDepositID(deserializeDepositID(detailNode.get("DepositID")));
            detail.setDepositTask(deserializeDepositTask(detailNode.get("DepositTask")));
            // Set additional fields similarly
            details.add(detail);
        }
        return details;
    }

    private ARIRequest.Detail.LineNbr deserializeLineNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.LineNbr lineNbr = new ARIRequest.Detail.LineNbr();
        lineNbr.setValue(node.get("value").asInt());
        return lineNbr;
    }

    private ARIRequest.Detail.PaymentMode deserializePaymentMode(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.PaymentMode paymentMode = new ARIRequest.Detail.PaymentMode();
        paymentMode.setValue(node.get("value").asText());
        return paymentMode;
    }

    private ARIRequest.Detail.ItemTaxAmount deserializeItemTaxAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.ItemTaxAmount itemTaxAmount = new ARIRequest.Detail.ItemTaxAmount();
        itemTaxAmount.setValue(node.get("value").asText());
        return itemTaxAmount;
    }

    private ARIRequest.Detail.ItemAmount deserializeItemAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.ItemAmount lineNbr = new ARIRequest.Detail.ItemAmount();
        lineNbr.setValue(node.get("value").asDouble());
        return lineNbr;
    }

    private ARIRequest.Detail.ChartofAccount1 deserializeChartofAccount1(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.ChartofAccount1 chartofAccount1 = new ARIRequest.Detail.ChartofAccount1();
        chartofAccount1.setValue(node.get("value").asText());
        return chartofAccount1;
    }

    private ARIRequest.Detail.ChartofAccount2 deserializeChartofAccount2(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.ChartofAccount2 chartofAccount2 = new ARIRequest.Detail.ChartofAccount2();
        chartofAccount2.setValue(node.get("value").asText());
        return chartofAccount2;
    }

    private ARIRequest.Detail.Branch deserializeBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.Branch branch = new ARIRequest.Detail.Branch();
        branch.setValue(node.get("value").asText());
        return branch;
    }

    private ARIRequest.Detail.Qty deserializeQty(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.Qty qty = new ARIRequest.Detail.Qty();
        qty.setValue(node.get("value").asInt());
        return qty;
    }

    private ARIRequest.Detail.Subaccount deserializeSubaccount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.Subaccount subaccount = new ARIRequest.Detail.Subaccount();
        subaccount.setValue(node.get("value").asText());
        return subaccount;
    }

    private ARIRequest.Detail.TransactionDescription deserializeTransactionDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.TransactionDescription transactionDescription = new ARIRequest.Detail.TransactionDescription();
        transactionDescription.setValue(node.get("value").asText());
        return transactionDescription;
    }

    private ARIRequest.Detail.UnitPrice deserializeUnitPrice(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.UnitPrice unitPrice = new ARIRequest.Detail.UnitPrice();
        unitPrice.setValue(node.get("value").asInt()); // Assuming UnitPrice is an int based on provided JSON
        return unitPrice;
    }

    // private ARIRequest.Detail.ReceiptNumber deserializeReceiptNumber(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     ARIRequest.Detail.ReceiptNumber receiptNumber = new ARIRequest.Detail.ReceiptNumber();
    //     System.out.println(receiptNumber.getValue());
    //     receiptNumber.setValue(node.get("value").asText());
    //     return receiptNumber;
    // }

    private ARIRequest.Detail.ReceiptNumber deserializeReceiptNumber(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.ReceiptNumber receiptNumber = new ARIRequest.Detail.ReceiptNumber();
        receiptNumber.setValue(node.get("value").asText());
        return receiptNumber;
    }

    private ARIRequest.Detail.PayeeInfo deserializePayeeInfo(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.PayeeInfo payeeInfo = new ARIRequest.Detail.PayeeInfo();
        payeeInfo.setValue(node.get("value").asText());
        return payeeInfo;
    }

    private ARIRequest.Detail.EntityName deserializeEntityName(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.EntityName entityName = new ARIRequest.Detail.EntityName();
        entityName.setValue(node.get("value").asText());
        return entityName;
    }

    private ARIRequest.Detail.EntityNumber deserializeEntityNumber(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.EntityNumber entityNumber = new ARIRequest.Detail.EntityNumber();
        entityNumber.setValue(node.get("value").asText());
        return entityNumber;
    }

    private ARIRequest.Detail.EntityType deserializeEntityType(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.EntityType entityType = new ARIRequest.Detail.EntityType();
        entityType.setValue(node.get("value").asText());
        return entityType;
    }

    private ARIRequest.Detail.DiscountAmount deserializeDiscountAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.DiscountAmount discountAmount = new ARIRequest.Detail.DiscountAmount();
        discountAmount.setValue(node.get("value").asDouble());
        return discountAmount;
    }

    private ARIRequest.Detail.DepositID deserializeDepositID(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.DepositID depositID = new ARIRequest.Detail.DepositID();
        depositID.setValue(node.get("value").asText());
        return depositID;
    }

    private ARIRequest.Detail.DepositTask deserializeDepositTask(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequest.Detail.DepositTask depositTask = new ARIRequest.Detail.DepositTask();
        depositTask.setValue(node.get("value").asText());
        return depositTask;
    }
    
}
