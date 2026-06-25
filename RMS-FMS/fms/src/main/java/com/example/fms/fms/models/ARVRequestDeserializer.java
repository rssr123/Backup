package com.example.fms.fms.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class ARVRequestDeserializer extends JsonDeserializer<ARVRequest> {

    @Override
    public ARVRequest deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        ARVRequest arvRequest = new ARVRequest();
        arvRequest.setEntity(deserializeEntity(node.get("entity")));
        arvRequest.setCustom(deserializeCustom(node.get("custom")));

        return arvRequest;
    }

    private ARVRequest.Custom deserializeCustom(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARVRequest.Custom custom = new ARVRequest.Custom();
        custom.setDocument(deserializeDocument(node.get("Document")));
        return custom;
    }

    private ARVRequest.Custom.Document deserializeDocument(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARVRequest.Custom.Document document = new ARVRequest.Custom.Document();
        document.setUsrVoidReason(deserializeUsrVoidReason(node.get("UsrVoidReason")));
        return document;
    }

    private ARVRequest.Custom.Document.UsrVoidReason deserializeUsrVoidReason(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARVRequest.Custom.Document.UsrVoidReason usrVoidReason = new ARVRequest.Custom.Document.UsrVoidReason();
        usrVoidReason.setType(node.get("type").asText());
        usrVoidReason.setValue(node.get("value").asText());
        return usrVoidReason;
    }

    private ARVRequest.Entity deserializeEntity(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARVRequest.Entity entity = new ARVRequest.Entity();
        entity.setReferenceNbr(deserializeReferenceNbr(node.get("ReferenceNbr")));
        entity.setType(deserializeTypeCharges(node.get("Type")));
        entity.setHold(deserializeHoldCharges(node.get("Hold")));
        return entity;
    }

    private ARVRequest.Entity.ReferenceNbr deserializeReferenceNbr(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARVRequest.Entity.ReferenceNbr referenceNbr = new ARVRequest.Entity.ReferenceNbr();
        referenceNbr.setValue(node.get("value").asText());
        return referenceNbr;
    }

    private ARVRequest.Entity.Type deserializeTypeCharges(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARVRequest.Entity.Type type = new ARVRequest.Entity.Type();
        type.setValue(node.get("value").asText());
        return type;
    }

    private ARVRequest.Entity.Hold deserializeHoldCharges(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARVRequest.Entity.Hold hold = new ARVRequest.Entity.Hold();
        hold.setValue(node.get("value").asText());
        return hold;
    }
}
