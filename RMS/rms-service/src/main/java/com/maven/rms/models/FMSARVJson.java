package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSARVJson {

    @JsonProperty("entity")
    private Entity entity;

    @JsonProperty("custom")
    private Custom custom;


    @Getter
    @Setter
    public static class Entity {
        @JsonProperty("ReferenceNbr")
        private GenericValue<String> referenceNbr;
        @JsonProperty("Type")
        private GenericValue<String> type;
        @JsonProperty("Hold")
        private GenericValue<String> hold;

       
    }


    @Getter
    @Setter
    public static class Custom {
        @JsonProperty("Document")
        private Document document;

        
    }

    @Getter
    @Setter
    public static class Document {
        @JsonProperty("UsrVoidReason")
        private UsrVoidReason usrVoidReason;

        
    }


    @Getter
    @Setter
    public static class UsrVoidReason {
        @JsonProperty("type")
        private String type;
        @JsonProperty("value")
        private String value;

        
    }
}