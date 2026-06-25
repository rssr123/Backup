package com.maven.rms.security;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SamlRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String samlRequest;
    private final String relayState;
    private final String authenticationRequestUri;
    private final String binding; // "REDIRECT" or "POST"
    private final String sigAlg;   // only used for Redirect binding
    private final String signature; // only used for Redirect binding

    public SamlRequestDTO(String samlRequest, String relayState,
                          String authenticationRequestUri, String binding,
                          String sigAlg, String signature) {
        this.samlRequest = samlRequest;
        this.relayState = relayState;
        this.authenticationRequestUri = authenticationRequestUri;
        this.binding = binding;
        this.sigAlg = sigAlg;
        this.signature = signature;
    }
}
