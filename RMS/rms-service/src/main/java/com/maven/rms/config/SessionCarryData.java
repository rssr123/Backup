package com.maven.rms.config;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionCarryData implements Serializable {

    private final String nonce; // same as RelayState
    private final String redirectUrl;

    public SessionCarryData(String nonce, String redirectUrl) {
        this.nonce = nonce;
        this.redirectUrl = redirectUrl;
    }

}
