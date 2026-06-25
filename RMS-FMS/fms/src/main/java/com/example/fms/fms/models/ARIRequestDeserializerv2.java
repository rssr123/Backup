package com.example.fms.fms.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ARIRequestDeserializerv2 extends JsonDeserializer<ARIRequestv2> {

    @Override
    public ARIRequestv2 deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        ARIRequestv2 ariRequest = new ARIRequestv2();

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

    private ARIRequestv2.Type deserializeType(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Type type = new ARIRequestv2.Type();
        type.setValue(node.get("value").asText());
        return type;
    }

    private ARIRequestv2.LinkBranch deserializeLinkBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.LinkBranch linkBranch = new ARIRequestv2.LinkBranch();
        linkBranch.setValue(node.get("value").asText());
        return linkBranch;
    }

    private ARIRequestv2.Amount deserializeAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Amount amount = new ARIRequestv2.Amount();
        amount.setValue(node.get("value").asInt());
        return amount;
    }

    private ARIRequestv2.CustomerID deserializeCustomer(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.CustomerID customer = new ARIRequestv2.CustomerID();
        customer.setValue(node.get("value").asText());
        return customer;
    }

    private ARIRequestv2.CustomerOrder deserializeCustomerOrder(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.CustomerOrder customerOrder = new ARIRequestv2.CustomerOrder();
        customerOrder.setValue(node.get("value").asText());
        return customerOrder;
    }

    private ARIRequestv2.Date deserializeDate(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Date date = new ARIRequestv2.Date();
        date.setValue(node.get("value").asText());
        return date;
    }

    private ARIRequestv2.InvoiceDate deserializeInvoiceDate(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.InvoiceDate date = new ARIRequestv2.InvoiceDate();
        date.setValue(node.get("value").asText());
        return date;
    }

    private ARIRequestv2.Description deserializeDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Description description = new ARIRequestv2.Description();
        description.setValue(node.get("value").asText());
        return description;
    }

    private ARIRequestv2.Custom deserializeCustom(JsonNode node) {
        if (node == null) {
            return null;
        }
        
        ARIRequestv2.Custom custom = new ARIRequestv2.Custom();
        JsonNode currentDocumentNode = node.get("CurrentDocument");
        if (currentDocumentNode != null) {
            ARIRequestv2.Custom.CurrentDocument currentDocument = new ARIRequestv2.Custom.CurrentDocument();
            JsonNode attributeSysnameNode = currentDocumentNode.get("AttributeSYSNAME");
            if (attributeSysnameNode != null) {
                ARIRequestv2.Custom.CurrentDocument.AttributeSYSNAME attributeSYSNAME = new ARIRequestv2.Custom.CurrentDocument.AttributeSYSNAME();
                attributeSYSNAME.setType(attributeSysnameNode.get("type").asText());
                attributeSYSNAME.setValue(attributeSysnameNode.get("value").asText());
                currentDocument.setAttributeSYSNAME(attributeSYSNAME);
            }

            JsonNode attributeGenPDFNode = currentDocumentNode.get("AttributeGENPDF");
            if (attributeGenPDFNode != null) {
                ARIRequestv2.Custom.CurrentDocument.AttributeGENPDF attributeGENPDF = new ARIRequestv2.Custom.CurrentDocument.AttributeGENPDF();
                attributeGENPDF.setType(attributeGenPDFNode.get("type").asText());
                attributeGENPDF.setValue(attributeGenPDFNode.get("value").asBoolean());
                currentDocument.setAttributeGENPDF(attributeGENPDF);
            }

            custom.setCurrentDocument(currentDocument);
        }
        return custom;
    }

    private List<ARIRequestv2.Detail> deserializeDetails(JsonNode node) {
        if (node == null || !node.isArray()) {
            return null;
        }
        List<ARIRequestv2.Detail> details = new ArrayList<>();
        for (JsonNode detailNode : node) {
            ARIRequestv2.Detail detail = new ARIRequestv2.Detail();
            detail.setLineNbr(deserializeLineNbr(detailNode.get("LineNbr")));
            detail.setChartofAccount1(deserializeChartofAccount1(detailNode.get("ChartofAccount1")));
            detail.setChartofAccount2(deserializeChartofAccount2(detailNode.get("ChartofAccount2")));
            detail.setBranch(deserializeBranch(detailNode.get("Branch")));
            detail.setQty(deserializeQty(detailNode.get("Qty")));
            detail.setSubaccount(deserializeSubaccount(detailNode.get("Subaccount")));
            detail.setTransactionDescription(deserializeTransactionDescription(detailNode.get("TransactionDescription")));
            detail.setUnitPrice(deserializeUnitPrice(detailNode.get("UnitPrice")));
            detail.setDiscountAmount(deserializeDiscountAmount(detailNode.get("DiscountAmount")));
            detail.setDepositID(deserializeDepositID(detailNode.get("DepositID")));
            detail.setDepositTask(deserializeDepositTask(detailNode.get("DepositTask")));
            // detail.setReceiptNumber(deserializeReceiptNumber(detailNode.get("ReceiptNumber")));
            // detail.setPayeeInfo(deserializePayeeInfo(detailNode.get("PayeeInfo")));
            // detail.setEntityName(deserializeEntityName(detailNode.get("EntityName")));
            // detail.setEntityNumber(deserializeEntityNumber(detailNode.get("EntityNumber")));
            // detail.setEntityType(deserializeEntityType(detailNode.get("EntityType")));
            // detail.setItemAmount(deserializeItemAmount(detailNode.get("ItemAmount")));
            // detail.setPaymentMode(deserializePaymentMode(detailNode.get("PaymentMode")));
            // detail.setItemTaxAmount(deserializeItemTaxAmount(detailNode.get("ItemTaxAmount")));

            // Set additional fields similarly
            details.add(detail);
        }
        return details;
    }

    private ARIRequestv2.Detail.LineNbr deserializeLineNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Detail.LineNbr lineNbr = new ARIRequestv2.Detail.LineNbr();
        lineNbr.setValue(node.get("value").asInt());
        return lineNbr;
    }

    // private ARIRequestv2.Detail.PaymentMode deserializePaymentMode(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     ARIRequestv2.Detail.PaymentMode paymentMode = new ARIRequestv2.Detail.PaymentMode();
    //     paymentMode.setValue(node.get("value").asText());
    //     return paymentMode;
    // }

    // private ARIRequestv2.Detail.ItemTaxAmount deserializeItemTaxAmount(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     ARIRequestv2.Detail.ItemTaxAmount itemTaxAmount = new ARIRequestv2.Detail.ItemTaxAmount();
    //     itemTaxAmount.setValue(node.get("value").asText());
    //     return itemTaxAmount;
    // }

    // private ARIRequestv2.Detail.ItemAmount deserializeItemAmount(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     ARIRequestv2.Detail.ItemAmount lineNbr = new ARIRequestv2.Detail.ItemAmount();
    //     lineNbr.setValue(node.get("value").asDouble());
    //     return lineNbr;
    // }

    private ARIRequestv2.Detail.ChartofAccount1 deserializeChartofAccount1(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Detail.ChartofAccount1 chartofAccount1 = new ARIRequestv2.Detail.ChartofAccount1();
        chartofAccount1.setValue(node.get("value").asText());
        return chartofAccount1;
    }

    private ARIRequestv2.Detail.ChartofAccount2 deserializeChartofAccount2(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Detail.ChartofAccount2 chartofAccount2 = new ARIRequestv2.Detail.ChartofAccount2();
        chartofAccount2.setValue(node.get("value").asText());
        return chartofAccount2;
    }

    private ARIRequestv2.Detail.Branch deserializeBranch(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Detail.Branch branch = new ARIRequestv2.Detail.Branch();
        branch.setValue(node.get("value").asText());
        return branch;
    }

    private ARIRequestv2.Detail.Qty deserializeQty(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Detail.Qty qty = new ARIRequestv2.Detail.Qty();
        qty.setValue(node.get("value").asInt());
        return qty;
    }

    private ARIRequestv2.Detail.Subaccount deserializeSubaccount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Detail.Subaccount subaccount = new ARIRequestv2.Detail.Subaccount();
        subaccount.setValue(node.get("value").asText());
        return subaccount;
    }

    private ARIRequestv2.Detail.TransactionDescription deserializeTransactionDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Detail.TransactionDescription transactionDescription = new ARIRequestv2.Detail.TransactionDescription();
        transactionDescription.setValue(node.get("value").asText());
        return transactionDescription;
    }

    private ARIRequestv2.Detail.UnitPrice deserializeUnitPrice(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Detail.UnitPrice unitPrice = new ARIRequestv2.Detail.UnitPrice();
        unitPrice.setValue(node.get("value").asInt()); // Assuming UnitPrice is an int based on provided JSON
        return unitPrice;
    }

    // private ARIRequestv2.Detail.ReceiptNumber deserializeReceiptNumber(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     ARIRequestv2.Detail.ReceiptNumber receiptNumber = new ARIRequestv2.Detail.ReceiptNumber();
    //     System.out.println(receiptNumber.getValue());
    //     receiptNumber.setValue(node.get("value").asText());
    //     return receiptNumber;
    // }

    // private ARIRequestv2.Detail.ReceiptNumber deserializeReceiptNumber(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     ARIRequestv2.Detail.ReceiptNumber receiptNumber = new ARIRequestv2.Detail.ReceiptNumber();
    //     receiptNumber.setValue(node.get("value").asText());
    //     return receiptNumber;
    // }

    // private ARIRequestv2.Detail.PayeeInfo deserializePayeeInfo(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     ARIRequestv2.Detail.PayeeInfo payeeInfo = new ARIRequestv2.Detail.PayeeInfo();
    //     payeeInfo.setValue(node.get("value").asText());
    //     return payeeInfo;
    // }

    // private ARIRequestv2.Detail.EntityName deserializeEntityName(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     ARIRequestv2.Detail.EntityName entityName = new ARIRequestv2.Detail.EntityName();
    //     entityName.setValue(node.get("value").asText());
    //     return entityName;
    // }

    // private ARIRequestv2.Detail.EntityNumber deserializeEntityNumber(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     ARIRequestv2.Detail.EntityNumber entityNumber = new ARIRequestv2.Detail.EntityNumber();
    //     entityNumber.setValue(node.get("value").asText());
    //     return entityNumber;
    // }

    // private ARIRequestv2.Detail.EntityType deserializeEntityType(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     ARIRequestv2.Detail.EntityType entityType = new ARIRequestv2.Detail.EntityType();
    //     entityType.setValue(node.get("value").asText());
    //     return entityType;
    // }

    private ARIRequestv2.Detail.DiscountAmount deserializeDiscountAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Detail.DiscountAmount discountAmount = new ARIRequestv2.Detail.DiscountAmount();
        discountAmount.setValue(node.get("value").asDouble());
        return discountAmount;
    }

    private ARIRequestv2.Detail.DepositID deserializeDepositID(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Detail.DepositID depositID = new ARIRequestv2.Detail.DepositID();
        depositID.setValue(node.get("value").asText());
        return depositID;
    }

    private ARIRequestv2.Detail.DepositTask deserializeDepositTask(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARIRequestv2.Detail.DepositTask depositTask = new ARIRequestv2.Detail.DepositTask();
        depositTask.setValue(node.get("value").asText());
        return depositTask;
    }
    
}
