package com.maven.rms.security;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.maven.rms.config.SessionCarryData;
import com.maven.rms.config.SessionCarryStore;
import com.maven.rms.models.AuthToken;
import com.maven.rms.models.RMSUser;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.AuthTokenService;
import com.maven.rms.utils.DebugInformation;
import com.maven.rms.utils.HazelcastSessionUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	//private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    private final ClusterSessionService cSS;
    private final Duration TTL = Duration.ofMinutes(480);
    private final String cookieName = "X-GSON-STATISTICS";
	//private final Boolean singleSessionOnly = true;
    private RequestCache requestCache = new HttpSessionRequestCache();
    private final SessionCarryStore sCStore;

    
	private AuthTokenService tkSvc;
	private AuthService authSvc;
    private String defaultUrl;
    private String defaultUrl2;
	
    public CustomAuthenticationSuccessHandler(String angularOnlinePortalURL, 
    			String angularPublicPortalURL, 
    			SessionCarryStore sCStore,
    			ClusterSessionService cSS) {
    	this.defaultUrl = angularOnlinePortalURL;
    	this.defaultUrl2 = angularPublicPortalURL;
    	this.sCStore = sCStore;
    	this.cSS = cSS;
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {

    	String ua = request.getHeader("User-Agent");
    	String ip = request.getHeader("x-forwarded-for") != null ? request.getHeader("x-forwarded-for") : null;

        ServletContext servletContext = request.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        if(authSvc == null)
            authSvc = webApplicationContext.getBean(AuthService.class);
        
        if(tkSvc == null)
            tkSvc = webApplicationContext.getBean(AuthTokenService.class);
        
        if (authentication instanceof Saml2Authentication) {
        	Saml2Authentication samlAuth = (Saml2Authentication) authentication;
        	if (samlAuth.getPrincipal() instanceof DefaultSaml2AuthenticatedPrincipal) {
        		DefaultSaml2AuthenticatedPrincipal sap = (DefaultSaml2AuthenticatedPrincipal) samlAuth.getPrincipal();
        		Map<String, List<Object>> attrs = sap.getAttributes();
        	    SerializableSamlPrincipal principal = new SerializableSamlPrincipal(
        	        sap.getName(), attrs);
        	    if(sap.getRelyingPartyRegistrationId() != null)
        	    	principal.setRegistrationId(sap.getRelyingPartyRegistrationId());
        	    
        	    SerializableSamlAuthentication serializableAuth =
        	        new SerializableSamlAuthentication(principal, samlAuth.getAuthorities(), true);

        	    SecurityContext context = SecurityContextHolder.createEmptyContext();
        	    context.setAuthentication(serializableAuth);
        	    SecurityContextHolder.setContext(context);
		
		        //HttpSession s = request.getSession(true);
		        HttpSession s = request.getSession(false);
		        if(s == null) {
		    		log.warn("[CustomAuthenticationSuccessHandler] Session was null, please debug!\nUser: " + authentication.getName(), 
		    				new DebugInformation(String.join("\n", AuthService.debugServletInformation(request))));
		        	s = request.getSession(true);
		        }
		        else
		        	try { s.getId(); } catch (IllegalStateException err) {
		        		log.warn("[CustomAuthenticationSuccessHandler] Session attributes cannot be accessed, please debug!\nUser: " + authentication.getName(), 
		        				new DebugInformation(String.join("\n", AuthService.debugServletInformation(request))));
		        		s = request.getSession(true);
		        	}
		        if(ip == null)
		        	ip = s.getAttribute("x-forwarded-for") != null ? (String)s.getAttribute("x-forwarded-for")
		        													: request.getRemoteAddr();
		        
		    	RMSUser currentUser;
		    	try {
		    		currentUser = authSvc.getCurrentUser(authentication.getName());
		    	}catch(Exception e) {
		    		currentUser = new RMSUser();
		    		currentUser.setSsm4uuserrefno(authentication.getName());
		    		log.warn("[CustomAuthenticationSuccessHandler] Cannot get RMSUser obj for {}, check table.", authentication.getName(), e);
		    	}
		    	
		        String jSessionId = s.getId();
		        String browserId = HazelcastSessionUtil.encodeToCookieSessionId(jSessionId);
		       	
		    	String redirectUrl = request.getParameter("redirectUrl");
		        if(redirectUrl == null)
		        	redirectUrl = (String)s.getAttribute("redirectUrl");
		
		       	String nonce = (String) s.getAttribute("nonce");
		    	if((nonce == null || nonce.isEmpty()) && (redirectUrl != null && redirectUrl.contains("gson="))) {
		    		nonce = redirectUrl.split("gson=")[1];
		    		if(nonce.contains("&"))
		    			nonce = nonce.split("&")[0];
		    	}
		    	
				String relayState = request.getParameter("RelayState");
				if (relayState != null && sCStore != null) {
					SessionCarryData dto = sCStore.remove(relayState);
		            if (dto != null) {
		            	redirectUrl = dto.getRedirectUrl();
		                nonce = dto.getNonce();
		            }
				}
		    	
		        String returnUrl = determineReturnUrl(requestCache.getRequest(request, null), redirectUrl
		        										, currentUser.getSsm4uuserrefno(), authSvc.isInternalUser());	
		
		    	AuthToken token = tkSvc.createToken(currentUser.getSsm4uuserrefno(), TTL, jSessionId, nonce, ua, ip);
		        Cookie cookie = new Cookie(cookieName, token.getToken());
		        cookie.setHttpOnly(true);
		        cookie.setSecure(true);
		        cookie.setPath("/");
		        cookie.setMaxAge((int) TTL.getSeconds());
		        response.addCookie(cookie);
		        
				try {  
					s.setAttribute("USER", principal.getName());
					s.setAttribute("SEC_AUTH", serializableAuth);
					s.setAttribute(token.getToken(), token);
		        	s.setAttribute("nonce", nonce);
				}catch(IllegalStateException e) { 
					s = request.getSession(true);
					log.warn("[CustomAuthenticationSuccessHandler] Cannot get session? Recreating and reinjecting parameters...\n"
							+ "Nonce: " + nonce + "\nRedirectUrl: " + redirectUrl + "\nUser: " + currentUser.getSsm4uuserrefno()
							+ "\nFingerPrint: " + token.getClientFingerprint() + "\nIP: " + ip +"\nOld JSesssionId: " + browserId
							+ "\nNew JSessionId: " + HazelcastSessionUtil.encodeToCookieSessionId(s.getId()));
					jSessionId = s.getId();
					browserId = HazelcastSessionUtil.encodeToCookieSessionId(jSessionId);
		        	s.setAttribute("nonce", nonce);
		        	s.setAttribute("redirectUrl", redirectUrl);
					s.setAttribute("USER", principal.getName());
					s.setAttribute("SEC_AUTH", serializableAuth);
					s.setAttribute(token.getToken(), token);
				}
				
				cSS.expireOtherSessions(authentication.getName(), jSessionId);
		    	
		    	List<String> stacktrace = new ArrayList<String>();
		    	stacktrace.add("RedirectURL: " + redirectUrl); 
		    	stacktrace.add("URI: " + request.getRequestURI());
		    	stacktrace.add("AuthObj: " + authentication.getName());
		    	stacktrace.add("IP: " + ip); 
		    	stacktrace.add("UA: " + ua); 
		    	stacktrace.add("Nonce: " + nonce);
		    	stacktrace.add("Fingerprint generated: " + token.getClientFingerprint());
		    	stacktrace.add("Token: " + token.getToken());
		    	if(!DebugInformation.suppressStacktrace) {
		    		stacktrace.add("GetRemoteAddr:" + request.getRemoteAddr()); 
			    	stacktrace.add("Request class: " + request.getClass().getName());
			    	stacktrace.add("Request hash: " + System.identityHashCode(request));
			    	stacktrace.add("===================");
			    	stacktrace.add("Current HttpServletRequest Information:");
			    	stacktrace.addAll(AuthService.debugServletInformation(request));
		    	}
		    	log.warn("[CustomAuthenticationSuccessHandler] " 
		    			+ currentUser.getSsm4uuserrefno() + " successfully login. [" + browserId + "]"
		    			  , new DebugInformation(String.join("\n", stacktrace)));
		    	
		        try {
					response.sendRedirect(returnUrl);
				} catch (IOException e) {
					//System.out.println("[CustomAuthenticationSuccessHandler::onAuthenticationSuccess]>> Redirect Error: " + e.getMessage());
					log.error("[CustomAuthenticationSuccessHandler::onAuthenticationSuccess]>> Redirect Error", e);
				}

        	}
        }
    }

    private String determineReturnUrl(SavedRequest savedRequest, String redirectUrl, String username, int isInternalUser) {
        // Default URL if the parameter is not provided or in case of an error
        // Retrieve the original request using RequestCache
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl().contains("redirectUrl=") ?
            		savedRequest.getRedirectUrl().split("redirectUrl=")[1] : defaultUrl;
            if(targetUrl.contains("&"))
            	targetUrl = targetUrl.split("&")[0];
            //System.out.println("Redirecting to the original URL (requestCache): " + targetUrl);
            log.debug("Redirecting " + username + " to the original URL (requestCache): " + targetUrl);
            if(targetUrl.contains("/payment-page?pr="))
            	return targetUrl;
            if(targetUrl.contains("/loading-home-page"))
            	return targetUrl;
            return isInternalUser > 0 ? (targetUrl.contains(defaultUrl) ? defaultUrl : defaultUrl2) 
            								: defaultUrl2;//targetUrl : defaultUrl2;
        }
        /*
        // Fallback to the specified redirectUrl parameter if needed
        if (redirectUrlParam != null && !redirectUrlParam.isEmpty()) {
        	//System.out.println("Redirecting to the provided URL (param): " + redirectUrlParam);
        	log.debug("Redirecting " + username + " to the provided URL (param): " + redirectUrlParam);
            return isInternalUser > 0 ? sanitizeUrl(redirectUrlParam) : defaultUrl2;
        }
        */
        // If all methods fail, reference the URL that was saved in the session attribute
        if(redirectUrl != null && redirectUrl.length() > 0) {
        	if(redirectUrl.contains("redirectUrl="))
        		redirectUrl = redirectUrl.split("redirectUrl=")[1];
            if(redirectUrl.contains("&"))
            	redirectUrl = redirectUrl.split("&")[0];
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
            	//System.out.println("Redirecting to the provided URL (session): " + redirectUrlParam);
            	log.debug("Redirecting " + username + " to the provided URL (session): " + redirectUrl);
                if(redirectUrl.contains("/payment-page?pr="))
                	return redirectUrl;
                if(redirectUrl.contains("/loading-home-page"))
                	return redirectUrl;
                return isInternalUser > 0 ? (redirectUrl.contains(defaultUrl) ? defaultUrl : defaultUrl2)  
                		: defaultUrl2; //sanitizeUrl(redirectUrl) : defaultUrl2;
            }
        }
        log.debug("Can't get Saved URL for user  " + username + ", returning " + defaultUrl);
        //System.out.println("Can't get Saved URL, returning " + defaultUrl);
         
        
        return isInternalUser > 0 ? defaultUrl : defaultUrl2;
    }    
}
