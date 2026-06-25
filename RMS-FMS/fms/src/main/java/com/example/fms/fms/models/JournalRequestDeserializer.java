package com.example.fms.fms.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JournalRequestDeserializer extends JsonDeserializer<JournalRequest> {

    @Override
    public JournalRequest deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        JournalRequest journalRequest = new JournalRequest();

        journalRequest.setBranchID(deserializeBranchID(node.get("BranchID")));
        journalRequest.setDescription(deserializeDescription(node.get("Description")));
        journalRequest.setLedgerID(deserializeLedgerID(node.get("LedgerID")));
        journalRequest.setModule(deserializeModule(node.get("Module")));
        journalRequest.setTransactionDate(deserializeTransactionDate(node.get("TransactionDate")));
        journalRequest.setCustom(deserializeCustom(node.get("custom")));
        journalRequest.setDetails(deserializeDetails(node.get("Details")));

        return journalRequest;
    }

    private JournalRequest.BranchID deserializeBranchID(JsonNode node) {
        if (node == null) {
            return null;
        }
        JournalRequest.BranchID branchID = new JournalRequest.BranchID();
        branchID.setValue(node.get("value").asText());
        return branchID;
    }

    private JournalRequest.Description deserializeDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        JournalRequest.Description description = new JournalRequest.Description();
        description.setValue(node.get("value").asText());
        return description;
    }

    private JournalRequest.LedgerID deserializeLedgerID(JsonNode node) {
        if (node == null) {
            return null;
        }
        JournalRequest.LedgerID ledgerID = new JournalRequest.LedgerID();
        ledgerID.setValue(node.get("value").asText());
        return ledgerID;
    }

    private JournalRequest.Module deserializeModule(JsonNode node) {
        if (node == null) {
            return null;
        }
        JournalRequest.Module module = new JournalRequest.Module();
        module.setValue(node.get("value").asText());
        return module;
    }

    private JournalRequest.TransactionDate deserializeTransactionDate(JsonNode node) {
        if (node == null) {
            return null;
        }
        JournalRequest.TransactionDate transactionDate = new JournalRequest.TransactionDate();
        transactionDate.setValue(node.get("value").asText());
        return transactionDate;
    }

    // private JournalRequest.Custom deserializeCustom(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     JournalRequest.Custom custom = new JournalRequest.Custom();
    //     custom.setCurrentDocument(deserializeCurrentDocument(node.get("CurrentDocument")));
    //     custom.setAttributeSYSNAME(deserializeAttributeSYSNAME(node.get("AttributeSYSNAME")));
    //     return custom;
    // }

    // private JournalRequest.Custom.CurrentDocument deserializeCurrentDocument(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     JournalRequest.Custom.CurrentDocument currentDocument = new JournalRequest.Custom.CurrentDocument();
    //     currentDocument.setAttributeExtRefNbr(deserializeAttributeExtRefNbr(node.get("AttributeExtRefNbr")));
    //     return currentDocument;
    // }

    // private JournalRequest.Custom.CurrentDocument.AttributeExtRefNbr deserializeAttributeExtRefNbr(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     JournalRequest.Custom.CurrentDocument.AttributeExtRefNbr attributeExtRefNbr = new JournalRequest.Custom.CurrentDocument.AttributeExtRefNbr();
    //     attributeExtRefNbr.setType(node.get("type").asText());
    //     attributeExtRefNbr.setValue(node.get("value").asText());
    //     return attributeExtRefNbr;
    // }

    // private JournalRequest.Custom.AttributeSYSNAME deserializeAttributeSYSNAME(JsonNode node) {
    //     if (node == null) {
    //         return null;
    //     }
    //     JournalRequest.Custom.AttributeSYSNAME attributeSYSNAME = new JournalRequest.Custom.AttributeSYSNAME();
    //     attributeSYSNAME.setType(node.get("type").asText());
    //     attributeSYSNAME.setValue(node.get("value").asText());
    //     return attributeSYSNAME;
    // }

    private JournalRequest.Custom deserializeCustom(JsonNode node) {
        if (node == null) return null;
        JournalRequest.Custom custom = new JournalRequest.Custom();
        custom.setBatchModule(deserializeBatchModule(node.get("BatchModule")));
        return custom;
    }
    
    private JournalRequest.Custom.BatchModule deserializeBatchModule(JsonNode node) {
        if (node == null) return null;
        JournalRequest.Custom.BatchModule batchModule = new JournalRequest.Custom.BatchModule();
        batchModule.setAttributeEXTREFNBR(deserializeAttributeEXTREFNBR(node.get("AttributeEXTREFNBR")));
        batchModule.setAttributeSYSNAME(deserializeAttributeSYSNAME(node.get("AttributeSYSNAME")));
        return batchModule;
    }
    
    private JournalRequest.Custom.BatchModule.AttributeEXTREFNBR deserializeAttributeEXTREFNBR(JsonNode node) {
        if (node == null) return null;
        JournalRequest.Custom.BatchModule.AttributeEXTREFNBR extRefNbr = new JournalRequest.Custom.BatchModule.AttributeEXTREFNBR();
        extRefNbr.setType(node.get("type").asText());
        extRefNbr.setValue(node.get("value").asText());
        return extRefNbr;
    }
    
    private JournalRequest.Custom.BatchModule.AttributeSYSNAME deserializeAttributeSYSNAME(JsonNode node) {
        if (node == null) return null;
        JournalRequest.Custom.BatchModule.AttributeSYSNAME sysName = new JournalRequest.Custom.BatchModule.AttributeSYSNAME();
        sysName.setType(node.get("type").asText());
        sysName.setValue(node.get("value").asText());
        return sysName;
    }
    
    private List<JournalRequest.Detail> deserializeDetails(JsonNode node) {
        List<JournalRequest.Detail> details = new ArrayList<>();
        if (node == null || !node.isArray()) {
            return details;
        }
        for (JsonNode detailNode : node) {
            JournalRequest.Detail detail = new JournalRequest.Detail();
            detail.setAccount(deserializeAccount(detailNode.get("Account")));
            detail.setBranchID(deserializeBranchID(detailNode.get("BranchID")));
            detail.setCreditAmount(deserializeCreditAmount(detailNode.get("CreditAmount")));
            detail.setDebitAmount(deserializeDebitAmount(detailNode.get("DebitAmount")));
            detail.setSubaccount(deserializeSubaccount(detailNode.get("Subaccount")));
            detail.setTransactionDescription(deserializeTransactionDescription(detailNode.get("TransactionDescription")));
            details.add(detail);
        }
        return details;
    }

    private JournalRequest.Detail.Account deserializeAccount(JsonNode node) {
        if (node == null) {
            return null;
        }
        JournalRequest.Detail.Account account = new JournalRequest.Detail.Account();
        account.setValue(node.get("value").asText());
        return account;
    }

    private JournalRequest.Detail.CreditAmount deserializeCreditAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        JournalRequest.Detail.CreditAmount creditAmount = new JournalRequest.Detail.CreditAmount();
        creditAmount.setValue(node.get("value").asDouble());
        return creditAmount;
    }

    private JournalRequest.Detail.DebitAmount deserializeDebitAmount(JsonNode node) {
        if (node == null) {
            return null;
        }
        JournalRequest.Detail.DebitAmount debitAmount = new JournalRequest.Detail.DebitAmount();
        debitAmount.setValue(node.get("value").asDouble());
        return debitAmount;
    }

    private JournalRequest.Detail.Subaccount deserializeSubaccount(JsonNode node) {
        if (node == null) {
            return null;
        }
        JournalRequest.Detail.Subaccount subaccount = new JournalRequest.Detail.Subaccount();
        subaccount.setValue(node.get("value").asText());
        return subaccount;
    }

    private JournalRequest.Detail.TransactionDescription deserializeTransactionDescription(JsonNode node) {
        if (node == null) {
            return null;
        }
        JournalRequest.Detail.TransactionDescription transactionDescription = new JournalRequest.Detail.TransactionDescription();
        transactionDescription.setValue(node.get("value").asText());
        return transactionDescription;
    }
}
