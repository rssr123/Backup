package com.example.fms.fms.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.fms.fms.models.ARCRequest.CustomerClass;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ARCRequestDeserializer extends JsonDeserializer<ARCRequest>{

    @Override
    public ARCRequest deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        ARCRequest arcRequest = new ARCRequest();

        arcRequest.setCustomerClass(deserializeCustomerClass(node.get("CustomerClass")));
        arcRequest.setCustom(deserializeCustom(node.get("custom")));
        

        return arcRequest;
    }

    private ARCRequest.CustomerClass deserializeCustomerClass(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARCRequest.CustomerClass customerClass = new ARCRequest.CustomerClass();
        customerClass.setValue(node.get("value").asText());
        return customerClass;
    }

    private ARCRequest.Custom deserializeCustom(JsonNode node) {
        if (node == null) {
            return null;
        }
        ARCRequest.Custom custom = new ARCRequest.Custom();
        JsonNode currentCustomerNode = node.get("CurrentCustomer");
        if (currentCustomerNode != null) {
            ARCRequest.Custom.CurrentCustomer currentCustomer = new ARCRequest.Custom.CurrentCustomer();
            JsonNode attributeSysnameNode = currentCustomerNode.get("UsrIdentityNbr");
            if (attributeSysnameNode != null) {
                ARCRequest.Custom.CurrentCustomer.UsrIdentityNbr usrIdentityNbr = new ARCRequest.Custom.CurrentCustomer.UsrIdentityNbr();
                usrIdentityNbr.setType(attributeSysnameNode.get("type").asText());
                usrIdentityNbr.setValue(attributeSysnameNode.get("value").asText());
                currentCustomer.setUsrIdentityNbr(usrIdentityNbr);
            }
            custom.setCurrentDocument(currentCustomer);
        }
        return custom;
    }

    
}
