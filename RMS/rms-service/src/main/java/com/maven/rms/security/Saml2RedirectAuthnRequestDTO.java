package com.maven.rms.security;

import java.io.Serializable;

public class Saml2RedirectAuthnRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String samlRequest;
    private String sigAlg;
    private String signature;
    private String relayState;
    private String authenticationRequestUri;

    public Saml2RedirectAuthnRequestDTO() {}

    public Saml2RedirectAuthnRequestDTO(String samlRequest, String sigAlg,
                                        String signature, String relayState,
                                        String authenticationRequestUri) {
        this.samlRequest = samlRequest;
        this.sigAlg = sigAlg;
        this.signature = signature;
        this.relayState = relayState;
        this.authenticationRequestUri = authenticationRequestUri;
    }

    public String getSamlRequest() { return samlRequest; }
    public String getSigAlg() { return sigAlg; }
    public String getSignature() { return signature; }
    public String getRelayState() { return relayState; }
    public String getAuthenticationRequestUri() { return authenticationRequestUri; }
}
