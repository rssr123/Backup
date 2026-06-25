package com.maven.rms.services;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.maven.rms.models.AuthToken;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRole;
import com.maven.rms.models.RmsApiAuth;
import com.maven.rms.security.ClusterSessionService;
import com.maven.rms.utils.DebugInformation;
import com.maven.rms.utils.HazelcastSessionUtil;
import com.maven.rms.utils.SystemStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {
	
	@Autowired
	private AuthTokenService tkSvc;
	
	@Autowired
	private ClusterSessionService cSS;
	
	@Value("${rms.application.backPortalURL}")
	private String backPortalUrl;
	@Value("${server.servlet.session.cookie.name}")
	private String cookieName;
	
    private final UAMService uamService;
    private final RmsApiAuthService apiAuthService;
    private String emailAttributeKey;
    
    private Boolean isSecurityAggressive = true; //Will kill all sessions including the attacked user

    public AuthService(@Value("${idp.email.attribute.key}") String emailAttributeKey,
    		UAMService uamService, RmsApiAuthService apiAuthService) {
        this.uamService = uamService;
        this.apiAuthService = apiAuthService;
        this.emailAttributeKey = emailAttributeKey;
    }

    public boolean isAuthenticated(HttpServletRequest request) {
    	if(request == null) {
            log.warn("[AuthService] Impossible scenario! No HTTPServletRequest injected "
            		+ "into Auth function! \nUser: " + getLoginUserName() );
    		return false;
    	}
        
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
        	//System.out.println("Detected user is logged in");
        	RMSUser user = getCurrentUser();
        	
        	//Grab from header because we need to verify incoming user is actually owner of session
            String token = extractTokenFromCookie(request, "X-GSON-STATISTICS");
        	String nonce = request.getHeader("x-gson-statistics");
        	String ua = request.getHeader("User-Agent");
        	String ip = request.getHeader("x-forwarded-for") == null ? request.getRemoteAddr() : request.getHeader("x-forwarded-for");
            String storedNonce;
            Object sessionToken;
            String sessionId;
            try { sessionId = request.getSession(false).getId(); }
            catch(IllegalStateException e) { return killCookieAndDeny(); }
            
            Session session = cSS.findSessionById(sessionId).orElse(null);
        	if(session == null) 
                return killCookieAndDeny();
        	try {
        		storedNonce = (String)session.getAttribute("nonce");
        		sessionToken = session.getAttribute(token);
        	} catch (IllegalStateException e) {//kill if no session
                return killCookieAndDeny();
        	} 

            if (token == null) {
        		//System.out.println("bad, invalidate@tokenString");
                log.error("[AuthService] Potential session hijacking attempt detected. No token found from cookie."
                		, new DebugInformation("Victim user (session ID replayed): " + user.getSsm4uuserrefno() 
                		+ "\n" + String.join("\n", debugServletInformation(request))));
                if(isSecurityAggressive)
                    return killCookieAndDeny(session);
            }
            AuthToken t = tkSvc.getToken(sessionToken);
            if (t == null) {
                //System.out.println("bad, invalidate@tokenObj");
                log.error("[AuthService] Potential session hijacking attempt detected. Cannot get Token Obj: " + token 
                		, new DebugInformation("Victim user (session ID replayed): " + user.getSsm4uuserrefno()
                		+ "\n" + String.join("\n", debugServletInformation(request))));
                if(isSecurityAggressive)
                    return killCookieAndDeny(session);
            }
            /*else if(!t.getToken().equals(token)) { //No need, token string is used to extract from session already gurantee child subordinate //is being triggered
            	//System.out.println("bad, invalid@tokenString=" +t.getToken() + " != " + token);
            	log.warn("[AuthService] Potential session hijacking attempt detected. Invalid token for user."
            				, new DebugInformation("Victim user (session ID replayed): " + user.getSsm4uuserrefno()
            				+ " \nReceived: " + token + " \nExpecting: " + t.getToken() + " \nSent Nonce: \t" + nonce + " \nStored Nonce: \t" 
            		        + (storedNonce != null ? storedNonce : "null")  + " \nStored User: " + t.getUsername() 
            		        + " \nStored Fingerprint: " + t.getClientFingerprint()
            		        + "\nStored IP: " + t.getIp() + "\nStored UA: " + t.getUa()
            		        + "\n" + String.join("\n", debugServletInformation(request)))); // add identifier for logging
            	if(isSecurityAggressive) 
                    return killCookieAndDeny(session);
            }*/
            else if (System.currentTimeMillis() > t.getExpiryEpochMs()) {
            	//System.out.println("bad, invalidate@timer");
            	log.warn("[AuthService] Token for " + t.getUsername() + " has expired!",
            			new DebugInformation(" User: " + user.getSsm4uuserrefno() + "\n" 
            					+ String.join("\n", debugServletInformation(request))));
                if(isSecurityAggressive) 
                    return killCookieAndDeny(session);
            }
            else {
                // Validate fingerprint
                String currentFingerprint = AuthToken.fingerprint(nonce, ua, ip, t != null ? t.getUseIp() : true);
                if (!t.getClientFingerprint().equals(currentFingerprint)) {
            		//System.out.println("bad, invalidate@fingerprint");
                    log.error("[AuthService] Potential session hijacking attempt detected. Fingerprint mismatch."
                    		, new DebugInformation("Victim user (session ID replayed): " + user.getSsm4uuserrefno()
                        + "\nStored User:\t" + t.getUsername()  
                        + "\nReceived Fingerprint: \t" + currentFingerprint + "\nStored Fingerprint: \t" + t.getClientFingerprint()
                    	+ "\nReceived Token: \t" + token + "\nStored Token: \t\t" + t.getToken() 
                    	+ "\nReceived Nonce: \t" + nonce + "\nStored Nonce: \t\t" + (storedNonce != null ? storedNonce : "null")  
                    	+ "\nReceived IP: \t" + ip + "\nStored IP: \t" + t.getIp() 
                    	+ "\nReceived UA: \t" + ua + "\nStored UA: \t" + t.getUa() 
                    	+ "\n" + String.join("\n", debugServletInformation(request))));
                    if(isSecurityAggressive)
                        return killCookieAndDeny(session);
                }
            }
            if(session.getAttribute("cycle") != null 
            		&& session.getAttribute("cycle") instanceof Boolean 
            		&& (Boolean) session.getAttribute("cycle"))
            	cSS.refreshSessionId(request);

            //If external user tries to access BO
            String referer = request.getHeader("referer");
            if((referer != null && user.getIsInternalUser() < 1) 
            	&& referer.contains(backPortalUrl))
            		return false;
        }

        String authHeader = getAuthorizationHeader(request);
        if (!isBasicAuth(authHeader)) 
            return false;
        
        String credentials = decodeBase64Credentials(authHeader);
        if (credentials == null) 
            return false;

        String[] usernameAndPassword = splitCredentials(credentials);
        if (!isValidCredentialFormat(usernameAndPassword)) 
            return false;
        
        return isUserValid(usernameAndPassword[0], usernameAndPassword[1]);
    }
    
    private boolean killCookieAndDeny() {
    	ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    	HttpServletResponse response = (attr != null ? attr.getResponse() : null);
    	if(response != null) {
	    	Cookie killCookie = new Cookie(cookieName , "");
	    	killCookie.setMaxAge(0);
	    	killCookie.setPath("/");
	    	killCookie.setHttpOnly(true);
	    	killCookie.setSecure(true);
	    	response.addCookie(killCookie);
	    	
	    	//Just to cover both:
	    	Cookie killCookie2 = new Cookie("JSESSIONID" , "");
	    	killCookie2.setMaxAge(0);
	    	killCookie2.setPath("/");
	    	killCookie2.setHttpOnly(true);
	    	killCookie2.setSecure(true);
	    	response.addCookie(killCookie2);
    	}
        return false;
    }
    
    private boolean killCookieAndDeny(Session session) {
    	if(session != null) {
       	    Map<String, Object> set = new HashMap<String, Object>();
       	    set.put("cycle", true);
       	    set.put("lastRotated", null);
    		try {cSS.updateSessionAttributes(session.getId(), set, Arrays.asList("cycleRotated"));
    		}catch(IllegalStateException e) {}
    	}
        return killCookieAndDeny();
    }
    
    private String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    private boolean isBasicAuth(String authHeader) {
        return authHeader != null && authHeader.startsWith("Basic ");
    }

    private String decodeBase64Credentials(String authHeader) {
        //try {
            String base64Credentials = authHeader.substring(6);
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        // } catch (IllegalArgumentException e) {
        //     return null;
        // }
    }

    private String[] splitCredentials(String decodedAuth) {
        return decodedAuth.split(":", 2);
    }

    private boolean isValidCredentialFormat(String[] credentials) {
        return credentials.length == 2 && !credentials[0].isEmpty() && !credentials[1].isEmpty();
    }

    private boolean isUserValid(String nm, String pw) {
        Optional<RmsApiAuth> user = apiAuthService.findByNmAndPwAndStatus(nm, pw, SystemStatus.Active.getMessage());
        return user.isPresent();
    }
    
    public Integer isInternalUser() {
    	RMSUser user = getCurrentUser();
    	if(user != null)
    		return user.getIsInternalUser() != null ? user.getIsInternalUser() : 0; 
    	else 
    		return 0;
    }

    public String getLoginUserName() {
    	RMSUser user = getCurrentUser();
        return (user!= null) ? user.getSsm4uuserrefno() : "Anonymous";
    }
    
    public String getUserEmail() {
    	RMSUser user = getCurrentUser();
        return (user!= null) ? user.getEmail() : "-1";
    }
    
    public String getUserName() {
    	RMSUser user = getCurrentUser();
        return (user!= null) ? user.getNm() : "Unknown_User";
    }
    
    public String getUserRoles() {
    	RMSUser user = getCurrentUser();
    	if(user == null) return "ANONYMOUS";
    	StringBuilder roles = new StringBuilder();
    	//user.getRoles().stream().map(Role::getRoleNmEn).forEach(role -> roles.append(role + ","));
    	user.getRoles().stream()
    		.filter((RMSUserRole role) -> role.getStatus().equals("A"))
    		.map(RMSUserRole::getRole)
    		.forEach(role -> roles.append(role.getRoleNmEn() + ","));
    	roles.deleteCharAt(roles.length()-1);
		return roles.toString();
    }
    
    public RMSUser getCurrentUser() {
    	String nm = "";

    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && !(auth instanceof AnonymousAuthenticationToken))
        	nm = (String)((DefaultSaml2AuthenticatedPrincipal) auth.getPrincipal())
            		.getAttribute(emailAttributeKey).get(0); // auth will return email to me only
        else 
        	return null;
        return uamService.findUserByEmail(nm).orElse(
        		uamService.findUserByUsername(nm).orElse(null));
    }
    public String getClientIP(HttpServletRequest request) {

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getHeader("Forwarded");
        }
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public RMSUser getCurrentUser(String identifier) {
        return uamService.findUserByEmail(identifier).orElse(
        		uamService.findUserByUsername(identifier).orElse(getCurrentUser()));
    }    
    
    public String getUserLatesttToken() {
    	RMSUser u = getCurrentUser();
    	return u != null ? u.getSessionId() : null;
    }

    public void updateUserSessionId(String sessionId) {
    	RMSUser user = getCurrentUser();
    	if(user != null) {
	    	user.setSessionId(sessionId);
	    	uamService.persistData(user);
    	}
    }

    private String extractTokenFromCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
    
    public static List<String> debugServletInformation(HttpServletRequest request){
    	//if(DebugInformation.suppressStacktrace)
    		//return Collections.emptyList();
        List<String> stacktrace = new ArrayList<String>();
        stacktrace.add("RequestedSessionId: " + (request.getRequestedSessionId() != null ? 
        		HazelcastSessionUtil.encodeToCookieSessionId(request.getRequestedSessionId()) : "null"));
        stacktrace.add("ContextPath: " + request.getContextPath());
        stacktrace.add("Scheme: " + request.getScheme());
        stacktrace.add("QueryString: " + request.getQueryString());
        stacktrace.add("RemoteHost: " + request.getRemoteHost());
        stacktrace.add("AuthType: " + request.getAuthType());
        stacktrace.add("LocalAddr: " + request.getLocalAddr());
        stacktrace.add("LocalName: " + request.getLocalName());
        stacktrace.add("Method: " + request.getMethod());
        stacktrace.add("ServerName: " + request.getServerName());
        stacktrace.add("ServletPath: " + request.getServletPath());
        stacktrace.add("RequestURI:" + request.getRequestURI());
        stacktrace.add("RemoteAddr:" + request.getRemoteAddr()); 
        stacktrace.add("RelayState: " + (String) request.getParameter("RelayState"));
        stacktrace.add("Parameter attributes:");
        Collections.list(request.getParameterNames()).forEach(p -> {
    		if(request.getParameter(p) instanceof String)
    			stacktrace.add("\t" + p + ": " 
    					+ (p.equals("SAMLResponse") 
    					? "...long string..." : request.getParameter(p)));
    		else
    			stacktrace.add("\t" + p + ": unknown java obj");
    	});
        stacktrace.add("Header attributes:");
    	Collections.list(request.getHeaderNames()).forEach(h -> {
            if(request.getHeader(h) instanceof String)
            	stacktrace.add("\t" + h + ": " + request.getHeader(h));
            else
            	stacktrace.add("\t" + h + ": java obj");
        });
    	stacktrace.add("Servlet attributes:");
    	Collections.list(request.getAttributeNames()).forEach(a -> {
            if(request.getAttribute(a) instanceof String)
            	stacktrace.add("    " + a + ": " + request.getAttribute(a));
            else
            	stacktrace.add("\t" + a + ": java obj");
        });
    	return stacktrace;
    }
}
