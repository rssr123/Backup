package com.maven.rms.models;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ARCRequest {
    @JsonProperty("CustomerClass")
    private ValueWrapper<String> customerClass;

    @JsonProperty("custom")
    private Custom custom;

    public ValueWrapper<String> getCustomerClass() {
        return customerClass;
    }

    public void setCustomerClass(ValueWrapper<String> customerClass) {
        this.customerClass = customerClass;
    }

    public Custom getCustom() {
        return custom;
    }

    public void setCustom(Custom custom) {
        this.custom = custom;
    }

    public static class ValueWrapper<T> {
        @JsonProperty("value")
        private T value;

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    public static class Custom {
        @JsonProperty("CurrentCustomer")
        private CurrentCustomer currentCustomer;

        public CurrentCustomer getCurrentCustomer() {
            return currentCustomer;
        }

        public void setCurrentCustomer(CurrentCustomer currentCustomer) {
            this.currentCustomer = currentCustomer;
        }

        public static class CurrentCustomer {
            @JsonProperty("UsrIdentityNbr")
            private Attribute usrIdentityNbr;

            public Attribute getUsrIdentityNbr() {
                return usrIdentityNbr;
            }

            public void setUsrIdentityNbr(Attribute usrIdentityNbr) {
                this.usrIdentityNbr = usrIdentityNbr;
            }
        }

        public static class Attribute {
            @JsonProperty("type")
            private String type;

            @JsonProperty("value")
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