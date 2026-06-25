package com.maven.rms.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdamanAPIToken {
    private Header header;
    private String data;

    @Getter
    @Setter
    public static class Header {
        private String requestTimestamp;
        private String responseTimestamp;
        private String statusCode;
        private String message;
    }

    @Getter
    @Setter
    public static class Data {
        @SerializedName("token_type")
        private String tokenType;
        @SerializedName("access_token")
        private String accessToken;
        private String scope;
        @SerializedName("expires_in")
        private int expiresIn;
        @SerializedName("consented_on")
        private long consentedOn;
    }
}
