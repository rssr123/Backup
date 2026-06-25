package com.maven.rms.models.payload.responses;

import java.util.List;

public class UpdateRefundResponse {
     private Header header;
    private List<Data> data;

    // Getters and Setters
    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Header {
        private String request_ts;
        private String response_ts;
        private String status_cd;
        private String message;

        // Getters and Setters
        public String getRequest_ts() {
            return request_ts;
        }

        public void setRequest_ts(String request_ts) {
            this.request_ts = request_ts;
        }

        public String getResponse_ts() {
            return response_ts;
        }

        public void setResponse_ts(String response_ts) {
            this.response_ts = response_ts;
        }

        public String getStatus_cd() {
            return status_cd;
        }

        public void setStatus_cd(String status_cd) {
            this.status_cd = status_cd;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Data {
      
    }
}
