package com.maven.rms.security;


import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.security.impl.SAMLMetadataSignatureSigningParametersResolver;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureSigningParametersResolver;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.crypto.XMLSigningUtil;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.springframework.security.saml2.Saml2Exception;
import org.springframework.security.saml2.core.Saml2ParameterNames;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.*;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.maven.rms.services.Saml2Utils;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom AuthenticationRequestFactory that injects ForceAuthn="true"
 * into the SAML AuthnRequest when required.
 *
 * Works with Spring Security 5.6.8.
 */
public class ForceAuthRequestFactory extends OpenSamlAuthenticationRequestFactory {

    @Override
    public Saml2PostAuthenticationRequest createPostAuthenticationRequest(Saml2AuthenticationRequestContext context) {
        Saml2PostAuthenticationRequest base = super.createPostAuthenticationRequest(context);

        // Decode -> Modify -> Encode
        String xml = Saml2Utils.base64Decode(base.getSamlRequest());
        if (shouldForce(context)) {
            xml = Saml2Utils.addForceAuthn(xml);
        }
        String newEncoded = Saml2Utils.base64Encode(xml);

        // Rebuild using builder available in 5.6.8
        return Saml2PostAuthenticationRequest
                .withAuthenticationRequestContext(context)
                .samlRequest(newEncoded)
                .relayState(base.getRelayState())
                .authenticationRequestUri(base.getAuthenticationRequestUri())
                .build();
    }

    @Override
    public Saml2RedirectAuthenticationRequest createRedirectAuthenticationRequest(Saml2AuthenticationRequestContext context) {
        Saml2RedirectAuthenticationRequest base = super.createRedirectAuthenticationRequest(context);

        String xml = Saml2Utils.inflate( base.getSamlRequest());
        if (shouldForce(context)) {
            xml = Saml2Utils.addForceAuthn(xml);
        }
        String newEncoded = Saml2Utils.deflateAndBase64(xml);

		RelyingPartyRegistration registration = context.getRelyingPartyRegistration();
		Saml2RedirectAuthenticationRequest.Builder result = Saml2RedirectAuthenticationRequest
				.withAuthenticationRequestContext(context);
		result.samlRequest(newEncoded).relayState(context.getRelayState());
		if (registration.getAssertingPartyDetails().getWantAuthnRequestsSigned()) {
			QueryParametersPartial partial = sign(registration)
					.param(Saml2ParameterNames.SAML_REQUEST, newEncoded);
			if (StringUtils.hasText(context.getRelayState())) {
				partial.param(Saml2ParameterNames.RELAY_STATE, context.getRelayState());
			}
			Map<String, String> parameters = partial.parameters();
			return result.sigAlg(parameters.get(Saml2ParameterNames.SIG_ALG))
					.signature(parameters.get(Saml2ParameterNames.SIGNATURE)).build();
		}
        // Hack: overwrite field via reflection (no builder in 5.6.8)
        /*try {
            setField(base, "samlRequest", newEncoded);
        } catch (Exception e) {
            throw new IllegalStateException("Could not inject ForceAuthn for Redirect binding", e);
        }
         */
		return result.build();
    }

    /**
     * Decide whether ForceAuthn should be applied.
     * Adjust this logic to your application needs.
     */
    private boolean shouldForce(Saml2AuthenticationRequestContext context) {
        // Example: only forceAuthn when a flag is present in RelayState
        return context.getRelayState() != null && context.getRelayState().contains("forceAuthn");
    }

    /** Reflection helper for Redirect binding */
    private static void setField(Object target, String field, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }
    
	static class QueryParametersPartial {

		final RelyingPartyRegistration registration;

		final Map<String, String> components = new LinkedHashMap<>();

		QueryParametersPartial(RelyingPartyRegistration registration) {
			this.registration = registration;
		}

		QueryParametersPartial param(String key, String value) {
			this.components.put(key, value);
			return this;
		}

		Map<String, String> parameters() {
			SignatureSigningParameters parameters = resolveSigningParameters(this.registration);
			Credential credential = parameters.getSigningCredential();
			String algorithmUri = parameters.getSignatureAlgorithm();
			this.components.put(Saml2ParameterNames.SIG_ALG, algorithmUri);
			UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
			for (Map.Entry<String, String> component : this.components.entrySet()) {
				builder.queryParam(component.getKey(),
						UriUtils.encode(component.getValue(), StandardCharsets.ISO_8859_1));
			}
			String queryString = builder.build(true).toString().substring(1);
			try {
				byte[] rawSignature = XMLSigningUtil.signWithURI(credential, algorithmUri,
						queryString.getBytes(StandardCharsets.UTF_8));
				String b64Signature = Saml2Utils.samlEncode(rawSignature);
				this.components.put(Saml2ParameterNames.SIGNATURE, b64Signature);
			}
			catch (SecurityException ex) {
				throw new Saml2Exception(ex);
			}
			return this.components;
		}

	}
	
	private static SignatureSigningParameters resolveSigningParameters(
			RelyingPartyRegistration relyingPartyRegistration) {
		List<Credential> credentials = resolveSigningCredentials(relyingPartyRegistration);
		List<String> algorithms = relyingPartyRegistration.getAssertingPartyDetails().getSigningAlgorithms();
		List<String> digests = Collections.singletonList(SignatureConstants.ALGO_ID_DIGEST_SHA256);
		String canonicalization = SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS;
		SignatureSigningParametersResolver resolver = new SAMLMetadataSignatureSigningParametersResolver();
		CriteriaSet criteria = new CriteriaSet();
		BasicSignatureSigningConfiguration signingConfiguration = new BasicSignatureSigningConfiguration();
		signingConfiguration.setSigningCredentials(credentials);
		signingConfiguration.setSignatureAlgorithms(algorithms);
		signingConfiguration.setSignatureReferenceDigestMethods(digests);
		signingConfiguration.setSignatureCanonicalizationAlgorithm(canonicalization);
		signingConfiguration.setKeyInfoGeneratorManager(buildSignatureKeyInfoGeneratorManager());
		criteria.add(new SignatureSigningConfigurationCriterion(signingConfiguration));
		try {
			SignatureSigningParameters parameters = resolver.resolveSingle(criteria);
			Assert.notNull(parameters, "Failed to resolve any signing credential");
			return parameters;
		}
		catch (Exception ex) {
			throw new Saml2Exception(ex);
		}
	}

	private static List<Credential> resolveSigningCredentials(RelyingPartyRegistration relyingPartyRegistration) {
		List<Credential> credentials = new ArrayList<>();
		for (Saml2X509Credential x509Credential : relyingPartyRegistration.getSigningX509Credentials()) {
			X509Certificate certificate = x509Credential.getCertificate();
			PrivateKey privateKey = x509Credential.getPrivateKey();
			BasicCredential credential = CredentialSupport.getSimpleCredential(certificate, privateKey);
			credential.setEntityId(relyingPartyRegistration.getEntityId());
			credential.setUsageType(UsageType.SIGNING);
			credentials.add(credential);
		}
		return credentials;
	}
	
	private static NamedKeyInfoGeneratorManager buildSignatureKeyInfoGeneratorManager() {
		final NamedKeyInfoGeneratorManager namedManager = new NamedKeyInfoGeneratorManager();

		namedManager.setUseDefaultManager(true);
		final KeyInfoGeneratorManager defaultManager = namedManager.getDefaultManager();

		// Generator for X509Credentials
		final X509KeyInfoGeneratorFactory x509Factory = new X509KeyInfoGeneratorFactory();
		x509Factory.setEmitEntityCertificate(true);
		x509Factory.setEmitEntityCertificateChain(true);

		defaultManager.registerFactory(x509Factory);

		return namedManager;
	}
	
	static QueryParametersPartial sign(RelyingPartyRegistration registration) {
		return new QueryParametersPartial(registration);
	}

}