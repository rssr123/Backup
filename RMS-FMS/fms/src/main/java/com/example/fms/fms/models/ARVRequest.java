package com.example.fms.fms.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ARVRequest {
    @JsonProperty("entity")
    private Entity entity;
    @JsonProperty("custom")
    private Custom custom;

    // Getters and Setters

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Custom getCustom() {
        return custom;
    }

    public void setCustom(Custom custom) {
        this.custom = custom;
    }

    /////
    public static class Entity {
        @JsonProperty("ReferenceNbr")
        private ReferenceNbr referenceNbr;
        @JsonProperty("Type")
        private Type type;
        @JsonProperty("Hold")
        private Hold hold;

        public ReferenceNbr getReferenceNbr() {
            return referenceNbr;
        }

        public void setReferenceNbr(ReferenceNbr referenceNbr) {
            this.referenceNbr = referenceNbr;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public Hold getHold() {
            return hold;
        }

        public void setHold(Hold hold) {
            this.hold = hold;
        }

        public static class ReferenceNbr {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class Type {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class Hold {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

    }

    public static class Custom {
        @JsonProperty("Document")
        private Document document;

        public Document getDocument() {
            return document;
        }

        public void setDocument(Document document) {
            this.document = document;
        }

        public static class Document {
            @JsonProperty("UsrVoidReason")
            private UsrVoidReason usrVoidReason;

            public UsrVoidReason getUsrVoidReason() {
                return usrVoidReason;
            }

            public void setUsrVoidReason(UsrVoidReason usrVoidReason) {
                this.usrVoidReason = usrVoidReason;
            }

            public static class UsrVoidReason {
                private String type;
                private String value;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }
    }

}
