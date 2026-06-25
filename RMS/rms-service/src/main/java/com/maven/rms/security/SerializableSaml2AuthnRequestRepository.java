package com.maven.rms.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestRepository;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.Saml2PostAuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.Saml2RedirectAuthenticationRequest;

import java.lang.reflect.Constructor;

public class SerializableSaml2AuthnRequestRepository
	implements Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest> {

    private static final String SESSION_ATTRIBUTE = "SAML_AUTHN_REQUEST";
    
	@Override
	public AbstractSaml2AuthenticationRequest loadAuthenticationRequest(HttpServletRequest request) {
	return (AbstractSaml2AuthenticationRequest) request.getSession()
	        .getAttribute(SESSION_ATTRIBUTE);
	}
	
	@Override
	public void saveAuthenticationRequest(AbstractSaml2AuthenticationRequest request,
	                                  HttpServletRequest httpRequest,
	                                  HttpServletResponse httpResponse) {
		if (request == null) {
		    httpRequest.getSession().removeAttribute(SESSION_ATTRIBUTE);
		} else {
			SamlRequestDTO dto;
			if (request instanceof Saml2RedirectAuthenticationRequest) {
				Saml2RedirectAuthenticationRequest redirect = (Saml2RedirectAuthenticationRequest) request;
	            dto = new SamlRequestDTO(
	            		redirect.getSamlRequest(),
	            		redirect.getRelayState(),
	            		redirect.getAuthenticationRequestUri(),
	                    "REDIRECT",
	                    redirect.getSigAlg(),
	                    redirect.getSignature()
	            );
	        } else if (request instanceof Saml2PostAuthenticationRequest) {
	        	Saml2PostAuthenticationRequest post = (Saml2PostAuthenticationRequest) request;
	            dto = new SamlRequestDTO(
	                    post.getSamlRequest(),
	                    post.getRelayState(),
	                    post.getAuthenticationRequestUri(),
	                    "POST",
	                    null,
	                    null
	            );
	        } else {
	            throw new IllegalArgumentException("Unsupported SAML2 AuthnRequest type: " + request.getClass());
	        }
			httpRequest.getSession().setAttribute(SESSION_ATTRIBUTE, dto);
		}
	}

    @Override
    public AbstractSaml2AuthenticationRequest removeAuthenticationRequest(HttpServletRequest request,
                                                                          HttpServletResponse response) {
        SamlRequestDTO dto = (SamlRequestDTO) sessionAttr(request);
        if (request.getSession(false) != null) {
            request.getSession(false).removeAttribute(SESSION_ATTRIBUTE);
        }
        return dto != null ? reconstructFromDTO(dto) : null;
    }

    private Object sessionAttr(HttpServletRequest request) {
        return (request.getSession(false) == null) ? null : request.getSession(false).getAttribute(SESSION_ATTRIBUTE);
    }

    /**
     * Reconstruct either a Saml2PostAuthenticationRequest or Saml2RedirectAuthenticationRequest
     * by invoking the classes' non-public constructors via reflection.
     */
    private AbstractSaml2AuthenticationRequest reconstructFromDTO(SamlRequestDTO dto) {
        try {
            if ("POST".equalsIgnoreCase(dto.getBinding())) {
                // Saml2PostAuthenticationRequest has ctor: (String samlRequest, String relayState, String authenticationRequestUri)
                Constructor<Saml2PostAuthenticationRequest> ctor =
                        (Constructor<Saml2PostAuthenticationRequest>) Saml2PostAuthenticationRequest.class
                                .getDeclaredConstructor(String.class, String.class, String.class);
                ctor.setAccessible(true);
                return ctor.newInstance(dto.getSamlRequest(), dto.getRelayState(), dto.getAuthenticationRequestUri());
            } else {
                // Saml2RedirectAuthenticationRequest has ctor: (String samlRequest, String sigAlg, String signature, String relayState, String authenticationRequestUri)
                Constructor<Saml2RedirectAuthenticationRequest> ctor =
                        (Constructor<Saml2RedirectAuthenticationRequest>) Saml2RedirectAuthenticationRequest.class
                                .getDeclaredConstructor(String.class, String.class, String.class, String.class, String.class);
                ctor.setAccessible(true);
                return ctor.newInstance(
                        dto.getSamlRequest(),
                        dto.getSigAlg(),
                        dto.getSignature(),
                        dto.getRelayState(),
                        dto.getAuthenticationRequestUri()
                );
            }
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to reconstruct SAML AuthnRequest from DTO", ex);
        }
    }
}

