package com.maven.rms.security;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSDateTime;
import org.opensaml.core.xml.schema.XSInteger;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.XSURI;
import org.opensaml.core.xml.schema.impl.XSAnyImpl;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Response;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.OpenSamlAuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.OpenSamlAuthenticationProvider.ResponseToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationRequestContext;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationRequestFactory;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationRequestFilter;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.DefaultSaml2AuthenticationRequestContextResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestContextResolver;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestRepository;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsUtils;

import com.hazelcast.core.HazelcastInstance;
import com.maven.rms.config.HazelcastSessionRegistry;
import com.maven.rms.config.RMSProperties;
import com.maven.rms.config.SessionCarryData;
import com.maven.rms.config.SessionCarryStore;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRole;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.SSM4UAPI;
import com.maven.rms.services.UAMService;
import com.maven.rms.utils.DebugInformation;
import com.maven.rms.utils.HazelcastSessionUtil;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity(debug = false)
@Slf4j
public class SAMLSecurityConfig extends WebSecurityConfigurerAdapter {
	// private static final Logger logger =
	// LoggerFactory.getLogger(SAMLSecurityConfig.class);

	@Autowired
	private RMSProperties rmsProperties;
	@Autowired
	private AuthService authSvc;
	@Autowired
	private UAMService uamSvc;
	@Autowired
	private RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;
	@Autowired
	private SSM4UAPI ssmApi;
    @Autowired
    private SessionCarryStore sCStore;
    @Autowired
    private ClusterSessionService cSS;

	@Value("${rms.application.backPortalURL}")
	private String angularOnlinePortalURL;
	@Value("${rms.application.publicPortalURL}")
	private String angularPublicPortalURL;
	@Value("${angular.login.entry.url}")
	private String loginUrlEntryPoint;
	@Value("${server.servlet.session.cookie.name}")
	private String cookieName;
	@Value("${saml.attribute.name.ssm4uUserRefNo}")
	private String ssm4uUserRefNoAttributeName;
	@Value("${saml.attribute.name.email}")
	private String emailAttributeName;
	@Value("${idp.email.attribute.key}") 
	private String emailAttributeKey;
	
    private final HazelcastInstance hazelcastInstance;

    public SAMLSecurityConfig(HazelcastInstance hazelcastInstance) {
    		//, ClusterSessionService clusterSessionService) {
        this.hazelcastInstance = hazelcastInstance;
        //this.clusterSessionService = clusterSessionService;
    }
    
	@Bean
	public RequestCache requestCache() {
		return new HttpSessionRequestCache();
	}
	
    @Bean
    public SessionRegistry sessionRegistry() {
        return new HazelcastSessionRegistry(hazelcastInstance);
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		try {

	        http.sessionManagement().sessionFixation().none()
	        	.maximumSessions(1).maxSessionsPreventsLogin(false)
	        	.sessionRegistry(sessionRegistry());
	        
			DefaultRelyingPartyRegistrationResolver relyingPartyRegistrationResolver = new DefaultRelyingPartyRegistrationResolver(
					this.relyingPartyRegistrationRepository);
			log.info("Initializing SAML Security Configuration...");

			// Create metadata filter with error handling
			Saml2MetadataFilter metadataFilter = new Saml2MetadataFilter(
					(Converter<HttpServletRequest, RelyingPartyRegistration>) relyingPartyRegistrationResolver,
					new OpenSamlMetadataResolver());

			// Create authentication filter with error handling
			Saml2WebSsoAuthenticationFilter authFilter = new Saml2WebSsoAuthenticationFilter(
					relyingPartyRegistrationRepository);
			Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest> authRepo = new SerializableSaml2AuthnRequestRepository();
			Saml2WebSsoAuthenticationRequestFilter samlFilter =
					saml2WebSsoAuthenticationRequestFilter(relyingPartyRegistrationRepository, 
							saml2AuthenticationRequestFactory());
			samlFilter.setAuthenticationRequestRepository(authRepo);
			
			// Configure authentication provider with enhanced error handling
			OpenSamlAuthenticationProvider authenticationProvider = new OpenSamlAuthenticationProvider();
			authenticationProvider
					.setAssertionValidator(OpenSamlAuthenticationProvider.createDefaultAssertionValidator());
			authenticationProvider.setResponseAuthenticationConverter(responseAuthenticationConverter());

			// Configure port mapping for proper URL handling
			PortMapperImpl portMapper = new PortMapperImpl();
			portMapper.setPortMappings(Collections.singletonMap(rmsProperties.getSAMLSecurityConfigPort(),
					rmsProperties.getSAMLSecurityConfigPort()));
			PortResolverImpl portResolver = new PortResolverImpl();
			portResolver.setPortMapper(portMapper);

			// Configure authentication entry point
			LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint(loginUrlEntryPoint);
			entryPoint.setPortMapper(portMapper);
			entryPoint.setPortResolver(portResolver);

			// Configure failure handler for better error handling
			SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
			failureHandler.setDefaultFailureUrl(loginUrlEntryPoint + "?error=true");
			// Main HTTP security configuration
			http.csrf(csrf -> csrf.disable())
					.authorizeRequests(authz -> authz
							.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
							.antMatchers(
									"/api/**",
									"/loginInfo",
									"/saml2/service-provider-metadata/**",
									"/saml/**",
									"/logout/**",
									"/actuator/**",
									//"/notifications",
									"/notifications/**",
									"/notifications**",
									"/error/**",
									"/login**",
									"/login/**")
							.permitAll()
							.anyRequest().authenticated())
					//.addFilterBefore(saml2WebSsoAuthenticationRequestFilter(relyingPartyRegistrationRepository, 
					//		saml2AuthenticationRequestFactory() ), UsernamePasswordAuthenticationFilter.class)
					.saml2Login(saml2Login -> saml2Login
							.relyingPartyRegistrationRepository(relyingPartyRegistrationRepository)
							.authenticationManager(new ProviderManager(authenticationProvider))
							.successHandler(new CustomAuthenticationSuccessHandler(angularOnlinePortalURL, angularPublicPortalURL, sCStore, cSS))
							.failureHandler(failureHandler)
							.loginPage(loginUrlEntryPoint)) //untested
					.exceptionHandling(exceptions -> exceptions
							.authenticationEntryPoint(entryPoint)
							.accessDeniedHandler((request, response, accessDeniedException) -> {
								log.warn("[SAMLSecurityConfig] Access denied for user: {} attempting to access URL: {}",
										request.getRemoteUser(), request.getRequestURL());
								response.sendRedirect(loginUrlEntryPoint + "?error=access_denied");
							}))
					.logout(logout -> logout
							.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
							.addLogoutHandler((request, response, authentication) -> {
								try {
									log.info("[SAMLSecurityConfig] {} initiated logout.",
											(authentication != null ? authentication.getName() : "anonymous"));
									authSvc.updateUserSessionId(null);
									try {request.getSession().invalidate();}
									catch(IllegalStateException e) {}
									// Just clear the security context, don't redirect here
									SecurityContextHolder.clearContext();
								} catch (Exception e) {
									log.error("[SAMLSecurityConfig] Exception during logout process for user: {}",
											(authentication != null ? authentication.getName() : "anonymous"), e);
								}
							})
							.logoutSuccessHandler((request, response, authentication) -> {
								try {
									log.warn("[SAMLSecurityConfig] {} successfully logout. [" 
												+ HazelcastSessionUtil.encodeToCookieSessionId(request.getRequestedSessionId()) + "]",
											(authentication != null ? authentication.getName() : "anonymous"), 
												new DebugInformation(String.join("\n", AuthService.debugServletInformation(request))));
									response.sendRedirect("/saml2/logout");
								} catch (IOException e) {
									log.error("[SAMLSecurityConfig] Exception in logout success handler for user: {}",
											(authentication != null ? authentication.getName() : "anonymous"), e);
								}
							})
							.invalidateHttpSession(true)
							.clearAuthentication(true)
							.deleteCookies(cookieName, "JSESSIONID"));

			// Add filters in correct order
			http.addFilterBefore(authFilter, BasicAuthenticationFilter.class);
			http.addFilterBefore(new SamlAcsRedirectFilter(), Saml2WebSsoAuthenticationFilter.class);
			http.addFilterBefore(metadataFilter, Saml2WebSsoAuthenticationFilter.class);
			//http.addFilterBefore(new ExtendsOncePerRequestFilter(requestCache(), loginUrlEntryPoint), Saml2WebSsoAuthenticationRequestFilter.class);
			http.addFilterBefore(new FrontFilter(loginUrlEntryPoint), Saml2WebSsoAuthenticationRequestFilter.class);
	        http.addFilterBefore(samlFilter, Saml2WebSsoAuthenticationRequestFilter.class);
	        //http.addFilterAfter(new AuthTokenValidationFilter(tkSvc), Saml2WebSsoAuthenticationFilter.class);
	        
			log.info("[SAMLSecurityConfig] SAML Security Configuration completed successfully");
		} catch (Exception e) {
			log.error("[SAMLSecurityConfig] Failed to configure SAML Security Configuration", e);
			throw e;
		}
	}
    
	@Bean
	public Saml2AuthenticationRequestFactory saml2AuthenticationRequestFactory() {
	    return new ForceAuthRequestFactory();
	}
	
	@Bean
	public Saml2WebSsoAuthenticationRequestFilter saml2WebSsoAuthenticationRequestFilter(
	        RelyingPartyRegistrationRepository relyingPartyRegistrationRepository,
	        Saml2AuthenticationRequestFactory factory) {
	
	    Saml2WebSsoAuthenticationRequestFilter filter =
	            new Saml2WebSsoAuthenticationRequestFilter(
	            		saml2AuthenticationRequestContextResolver(relyingPartyRegistrationRepository)
	            		, factory);

	    //filter.setAuthenticationRequestFactory(factory);
	    return filter;
	}
	
	@Bean
	public Saml2AuthenticationRequestContextResolver customSaml2AuthenticationRequestContextResolver(
	        RelyingPartyRegistrationResolver registrations) {

	    DefaultSaml2AuthenticationRequestContextResolver delegate =
	            new DefaultSaml2AuthenticationRequestContextResolver(registrations);
	    return (request) -> {
	        Saml2AuthenticationRequestContext ctx = delegate.resolve(request);
	        if (ctx == null) {
	            return null;
	        }
	        boolean force = true;
	        String nonce = "";
	        String redirectUrl = null;
	        HttpSession session = request.getSession(false);
	        if(session != null) {
	        	String rFlag = (String) session.getAttribute("relayState");
	        	nonce = (String) session.getAttribute("nonce");
	        	redirectUrl = (String) session.getAttribute("redirectUrl");
	        	force = rFlag != null ? ("strict".equalsIgnoreCase(rFlag)? true : false) : false; //invert flags if lax
	        }
	        String existingRelay = ctx.getRelayState();
	        String relay = existingRelay == null ? null : existingRelay;

	        // Encode flag in relayState. choose a compact format:
	        String extra = force ? "forceAuthn=true" : null;
	        String newRelay = (relay == null || relay.isEmpty())
	                ? extra
	                : (extra == null ? relay : (relay + "|" + extra));
	        newRelay = newRelay + nonce;

	        SessionCarryData dto = new SessionCarryData(nonce, redirectUrl);
	        sCStore.save(newRelay, dto);
	        
	        // build a new context with the modified relayState
	        return Saml2AuthenticationRequestContext.builder()
	                .relyingPartyRegistration(ctx.getRelyingPartyRegistration())
	                .issuer(ctx.getIssuer())
	                .assertionConsumerServiceUrl(ctx.getAssertionConsumerServiceUrl())
	                .relayState(newRelay)
	                .build();
	    };
	}
	
	@Bean
	public RelyingPartyRegistrationResolver relyingPartyRegistrationResolver(
	        RelyingPartyRegistrationRepository repo) {
	    return new DefaultRelyingPartyRegistrationResolver(repo);
	}
	
    @Bean
    public Saml2AuthenticationRequestContextResolver saml2AuthenticationRequestContextResolver(RelyingPartyRegistrationRepository repo) {
        return customSaml2AuthenticationRequestContextResolver(relyingPartyRegistrationResolver(repo));
    }
    
	@Bean
	public RelyingPartyRegistrationRepository relyingPartyRegistrations(
			@Value("${spring.security.saml2.relyingparty.registration.ssm.entity.id}") String entityId,
			@Value("${spring.security.saml2.relyingparty.registration.ssm.assertion.consumer.service.location}") String aCSL,
			@Value("${spring.security.saml2.relyingparty.registration.ssm.identityprovider.metadata-uri}") String idpMetaURI,
			@Value("${spring.security.saml2.id}") String regId) throws Exception {

		try {
			log.info("[SAMLSecurityConfig] Configuring SAML Relying Party Registration with entityId: {}", entityId);
			log.debug("[SAMLSecurityConfig] SAML Configuration - ACS Location: {}, IDP Metadata URI: {}, Registration ID: {}",
					aCSL, idpMetaURI, regId);

			RelyingPartyRegistration registration = RelyingPartyRegistrations
					.fromMetadataLocation(idpMetaURI)
					.entityId(entityId)
					.assertionConsumerServiceLocation(aCSL)
					.registrationId(regId)
					.build();
			
			log.info("[SAMLSecurityConfig] SAML Relying Party Registration configured successfully for entity: {}", entityId);
			return new InMemoryRelyingPartyRegistrationRepository(registration);

		} catch (Exception e) {
			log.error("[SAMLSecurityConfig] Failed to configure SAML Relying Party Registration for entity: {} with IDP URI: {}",
					entityId, idpMetaURI, e);
			throw new RuntimeException(
					"[SAMLSecurityConfig] Failed to initialize SAML configuration. Please check IDP connectivity and configuration.", e);
		}
	}

	public Converter<ResponseToken, Saml2Authentication> responseAuthenticationConverter() {
		return (responseToken) -> {
			try {
				Saml2AuthenticationToken token = responseToken.getToken();
				Response response = responseToken.getResponse();
				Assertion assertion = CollectionUtils.firstElement(response.getAssertions());

				if (assertion == null) {
					log.error("[SAMLSecurityConfig] SAML Response validation failed: No assertion found in response");
					throw new RuntimeException("Invalid SAML response: No assertion found");
				}

				String nameIdVal = assertion.getSubject().getNameID().getValue();
				log.info("[SAMLSecurityConfig] SAML Authentication - Processing NameID: {}", nameIdVal);

				String ssm4uId = null;
				String email = nameIdVal.contains("@") ? nameIdVal : null;

				StringBuilder attributeLogger = new StringBuilder();
				List<GrantedAuthority> authorities = new ArrayList<>();

				// Process SAML attributes
				for (AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
					for (Attribute attribute : attributeStatement.getAttributes()) {
						List<XMLObject> attributeValues = attribute.getAttributeValues();
						if (!attributeValues.isEmpty()) {
							XMLObject aVal = (attributeValues.get(0));
							String attrString = aVal == null ? null
									: aVal instanceof XSString ? ((XSString) aVal).getValue()
											: aVal instanceof XSAnyImpl ? ((XSAnyImpl) aVal).getTextContent()
													: aVal.toString();
							String attr = "SAML ATTRIBUTES - " + attribute.getName() + ": " + attrString;
							log.debug("[SAMLSecurityConfig] SAML Attribute received: {} = {}", attribute.getName(), attrString);
							attributeLogger.append(attributeLogger.length() > 0 ? "\n" : "").append(attr);

							if (attribute.getName().equals(ssm4uUserRefNoAttributeName))
								ssm4uId = attrString;
							else if (attribute.getName().equals(emailAttributeName))
								email = attrString;
						}
					}
				}

				// User processing with enhanced error handling
				RMSUser user = null;
				try {
					user = uamSvc.returnUserRepo().findRMSUserByEmail(email == null ? nameIdVal : email).orElse(null);

					if (user == null || user.getIsInternalUser() == 0 || user.getRoles().size() == 0) {
						// Enhanced user verification process
						user = processUserVerification(user, email, nameIdVal, ssm4uId, attributeLogger.toString());
					}

					// Validate and update SSM4U ID if necessary
					if (user != null && ssm4uId != null && !user.getSsm4uuserrefno().equals(ssm4uId)) {
						log.warn("[SAMLSecurityConfig] SSM4U ID mismatch detected - IDP: {}, DB: {}. Updating database record",
								ssm4uId, user.getSsm4uuserrefno());
						uamSvc.sp_updatessm4uuserrefno(ssm4uId, email == null ? nameIdVal : email);
						user = uamSvc.returnUserRepo().findRMSUserByEmail(email == null ? nameIdVal : email)
								.orElse(null);
					}

					// Assign default roles if user has no roles
					if (user != null && user.getRoles().size() == 0) {
						uamSvc.getRoles(new HashSet<>(Arrays.asList("GENERAL_USER"))).forEach(user::addRole);
						user.setIsInternalUser(0);
						user.setDtModified(LocalDateTime.now());
						String insertStatus = uamSvc.persistData(user);
						if (!insertStatus.equals("Successfully saved user."))
							log.error("[SAMLSecurityConfig] Failed to assign default roles to user: {}", user.getEmail());
					}

					// Determine user authorities
					if (user == null) {
						log.warn("[SAMLSecurityConfig] SAML Authentication failed - user not found or could not be created for NameID: {}",
								nameIdVal);
						authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
					} else if (!user.getStatus().equals("A")) {
						log.warn("[SAMLSecurityConfig] SAML Authentication failed - user status is inactive: {} for user: {}",
								user.getStatus(), user.getEmail());
						authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
					} else {
						authorities = user.getRoles().stream()
								.filter((RMSUserRole role) -> role.getStatus().equals("A"))
								.map((RMSUserRole role) -> new SimpleGrantedAuthority(
										"ROLE_" + role.getRole().getRoleNmEn().toUpperCase()))
								.collect(Collectors.toList());
						log.info("[SAMLSecurityConfig] SAML Authentication successful for user: {} with roles: {}",
								user.getEmail(), authorities.stream().map(GrantedAuthority::getAuthority)
										.collect(Collectors.joining(", ")));
					}

				} catch (Exception e) {
					log.error("[SAMLSecurityConfig] Exception during SAML user processing for NameID: {}", nameIdVal, e);
					authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
				}

				// Build authentication result
				Map<String, List<Object>> attributes = getAssertionAttributes(assertion);
				String registrationId = responseToken.getToken().getRelyingPartyRegistration().getRegistrationId();
				attributes.put("registrationId", Collections.singletonList(registrationId));

				DefaultSaml2AuthenticatedPrincipal principal = new DefaultSaml2AuthenticatedPrincipal(
						ssm4uId == null ? nameIdVal : ssm4uId, attributes);

				log.info("[SAMLSecurityConfig] SAML Authentication completed successfully for user: {}",
						(ssm4uId == null ? nameIdVal : ssm4uId));
				
				
				return new Saml2Authentication(principal, token.getSaml2Response(), authorities);

			} catch (Exception e) {
				log.error("[SAMLSecurityConfig] Critical error in SAML response processing", e);
				throw new RuntimeException("SAML authentication failed", e);
			}
		};
	}
	
	private RMSUser processUserVerification(RMSUser user, String email, String nameIdVal, String ssm4uId,
			String attributeLogger) {
		try {
			log.info("[SAMLSecurityConfig] Processing user verification for email: {}", (email == null ? nameIdVal : email));

			okhttp3.Response resp = ssmApi.getUserProfile(user, email == null ? nameIdVal : email, ssm4uId);
			String dataBody = "";
			String name = null;
			String userStatus = null;
			String userType = null;

			if (resp != null) {
				try {
					dataBody = resp.body().string().replace("\"", "'").replace("\\", "");

					if (!dataBody.contains("userStatus")) {
						log.error("[SAMLSecurityConfig] Invalid getUserProfile API response - missing userStatus field. Response: {}",
								dataBody);
						log.debug("[SAMLSecurityConfig] IDP Attributes for failed response: {}", attributeLogger);
						throw new IllegalArgumentException(
								"[SAMLSecurityConfig] Invalid API response - missing required fields: " + dataBody);
					}

					// Parse response data
					if (dataBody.contains("name':'"))
						name = dataBody.split("name':'")[1].split("'")[0];
					if (dataBody.contains("userStatus':'"))
						userStatus = dataBody.split("userStatus':'")[1].split("'")[0];
					if (dataBody.contains("userType':'"))
						userType = dataBody.split("userType':'")[1].split("'")[0];

					// Update IDs from API response if available
					if (dataBody.contains("ssm4uUserRefNo':'")) {
						String ssm4uIdTmp = dataBody.split("ssm4uUserRefNo':'")[1].split("'")[0];
						ssm4uId = ssm4uIdTmp == null ? ssm4uId : ssm4uIdTmp;
					}
					if (dataBody.contains("email':'")) {
						String emailTmp = dataBody.split("email':'")[1].split("'")[0];
						email = emailTmp == null ? email : emailTmp;
					}

					log.debug("[SAMLSecurityConfig] API Response parsed - Name: {}, Status: {}, Type: {}", name, userStatus, userType);

				} catch (Exception e) {
					log.error("[SAMLSecurityConfig] Exception parsing getUserProfile API response", e);
				}
			} else {
				log.warn("[SAMLSecurityConfig] No response received from getUserProfile API for user: {}",
						(email == null ? nameIdVal : email));
			}

			// Update existing user or create new user
			if (user != null) {
				String oldName = user.getNm();
				boolean updated = false;

				if (userStatus != null && !userStatus.equals(user.getUserVerificationStatus())) {
					user.setUserVerificationStatus(userStatus);
					updated = true;
				}
				if (userType != null) {
					int newInternalFlag = userType.equals("I") ? 1 : 0;
					if (user.getIsInternalUser() != newInternalFlag) {
						user.setIsInternalUser(newInternalFlag);
						updated = true;
					}
				}
				if (name != null && !oldName.equals(name)) {
					user.setNm(name);
					updated = true;
				}

				if (updated) {
					user.setDtModified(LocalDateTime.now());
					String result = uamSvc.persistData(user);
					log.info("[SAMLSecurityConfig] User profile updated successfully: {}", result);
				}

			} else if (user == null) {
				if (ssm4uId == null || ssm4uId.equals("") || email == null || email.equals("")) {
					log.error("[SAMLSecurityConfig] Insufficient information to create new user - Name: {}, "
							+ "SSM4U ID: {}, Email: {}, Status: {}, Type: {}",
							name, ssm4uId, email, userStatus, userType);
					log.debug("IDP Attributes: {}", attributeLogger);
				} else {
					user = new RMSUser(ssm4uId, name != null ? name : ssm4uId, email, "A", "system",
							userType == null ? 0 : userType.equals("I") ? 1 : 0);
					user.setUserVerificationStatus(userStatus);
					String insertStatus = uamSvc.sp_createAccount(user, new HashSet<>(Arrays.asList("GENERAL_USER")));
					log.info("[SAMLSecurityConfig] New user creation result: {} for email: {}", insertStatus, email);

					if (!insertStatus.equals("Successfully saved user."))
						log.error("[SAMLSecurityConfig] Failed to create new user despite having correct parameters for email: {}", email);
				}
			}

			return user;

		} catch (Exception e) {
			log.error("[SAMLSecurityConfig] Exception in user verification process for user: {}", (email == null ? nameIdVal : email), e);
			return user;
		}
	}

	private static Map<String, List<Object>> getAssertionAttributes(Assertion assertion) {
		Map<String, List<Object>> attributeMap = new LinkedHashMap<>();
		for (AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
			for (Attribute attribute : attributeStatement.getAttributes()) {
				List<Object> attributeValues = new ArrayList<>();
				for (XMLObject xmlObject : attribute.getAttributeValues()) {
					Object attributeValue = getXmlObjectValue(xmlObject);
					if (attributeValue != null) {
						attributeValues.add(attributeValue);
					}
				}
				attributeMap.put(attribute.getName(), attributeValues);
			}
		}
		return attributeMap;
	}

	private static Object getXmlObjectValue(XMLObject xmlObject) {
		if (xmlObject instanceof XSAny)
			return ((XSAny) xmlObject).getTextContent();

		if (xmlObject instanceof XSString)
			return ((XSString) xmlObject).getValue();

		if (xmlObject instanceof XSInteger)
			return ((XSInteger) xmlObject).getValue();

		if (xmlObject instanceof XSURI)
			return ((XSURI) xmlObject).getValue();

		if (xmlObject instanceof XSBoolean) {
			XSBooleanValue xsBooleanValue = ((XSBoolean) xmlObject).getValue();
			return (xsBooleanValue != null) ? xsBooleanValue.getValue() : null;
		}

		if (xmlObject instanceof XSDateTime) {
			org.joda.time.DateTime dateTime = ((XSDateTime) xmlObject).getValue();
			return (dateTime != null) ? Instant.ofEpochMilli(dateTime.getMillis()) : null;
		}

		return null;
	}
}
