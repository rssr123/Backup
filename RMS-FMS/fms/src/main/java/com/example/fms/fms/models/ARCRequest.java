package com.example.fms.fms.models;

public class ARCRequest {

    private CustomerClass customerClass;
    private Custom custom;

    // Getters and Setters
    public CustomerClass getCustomerClass() {
        return customerClass;
    }

    public void setCustomerClass(CustomerClass customerClass) {
        this.customerClass = customerClass;
    }

    // Nested classes for structure mirroring JSON

    public Custom getCustom() {
        return custom;
    }

    public void setCustom(Custom custom) {
        this.custom = custom;
    }

    public static class CustomerClass {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Custom {
        private CurrentCustomer currentCustomer;

        public CurrentCustomer getCurrentCustomer() {
            return currentCustomer;
        }

        public void setCurrentDocument(CurrentCustomer currentCustomer) {
            this.currentCustomer = currentCustomer;
        }

        public static class CurrentCustomer {
            private UsrIdentityNbr usrIdentityNbr;

            public UsrIdentityNbr getUsrIdentityNbr() {
                return usrIdentityNbr;
            }

            public void setUsrIdentityNbr(UsrIdentityNbr usrIdentityNbr) {
                this.usrIdentityNbr = usrIdentityNbr;
            }

            public static class UsrIdentityNbr {
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
