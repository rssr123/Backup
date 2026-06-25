package com.maven.rms.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.AuthToken;
import com.maven.rms.models.RMSUser;
import com.maven.rms.security.ClusterSessionService;
import com.maven.rms.services.AuthService;

import org.springframework.security.core.Authentication;

@RestController
public class AuthController {
	@Autowired
	private AuthService authSvc;
	@Autowired
    private ClusterSessionService css;
	
	@Value("${idp.email.attribute.key}") 
	String emailAttributeKey;

    @GetMapping("/api/auth/details")
    public ResponseEntity<?> authDetails(HttpServletRequest request) {
        // try {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && !(auth instanceof AnonymousAuthenticationToken) && authSvc.isAuthenticated(request)) {
            // User is authenticated
            Map<String, Object> response = new HashMap<>();
            RMSUser u = authSvc.getCurrentUser();
            response.put("username", u.getSsm4uuserrefno());
            response.put("name", u.getNm());
            response.put("email", u.getEmail());
            response.put("authenticated", auth.isAuthenticated());
            // You can include other user details here as needed
            return ResponseEntity.ok(response);
        } else {
            // User is not authenticated
            Map<String, String> response = new HashMap<>();
            response.put("status", "UNAUTHORIZED");
            return ResponseEntity.ok(response);
        }
        // } catch (Exception e) {
        // // Log the exception (real logging framework should be used instead of
        // // printStackTrace)
        // e.printStackTrace();
        // // Unexpected condition or server error
        // Map<String, String> response = new HashMap<>();
        // response.put("error", "Internal Server Error");
        // return
        // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        // }
    }

    @GetMapping("/api/auth/isinternaluser")
    public ResponseEntity<?> isInternalUser(HttpServletRequest request) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	Map<String, String> response = new HashMap<>();
    	if(auth != null && !(auth instanceof AnonymousAuthenticationToken))
    		response.put("flag", authSvc.isInternalUser() == 0 ? "false" : "true");
    	else
    		response.put("flag", "false");
        return ResponseEntity.ok(response);
    }    
    
    @GetMapping("/clusters")
    public ResponseEntity<?> getClusterInformatin(HttpServletRequest request) {
        return ResponseEntity.ok(css.checkCluster());
    }
    
    @GetMapping("sessions")
    public ResponseEntity<?> getClusterSessionInformatin(HttpServletRequest request) {
        return ResponseEntity.ok(css.printAllSessions());
    }
    /*
    @GetMapping("/api/auth/GetName")
    public String GetLoginUserName(HttpServletRequest request) {
        return authSvc.getLoginUserName();
    }
    
    @GetMapping("/api/auth/osessions")
    public ResponseEntity<?> depreceatedGetSessionIds(HttpServletRequest request) {
	    List<String> ids = new ArrayList<String>();	    
	    List<Object> principals = sR.getAllPrincipals();
        System.out.println("Principle size: " + principals.size());
        List<SessionInformation> sessionInformation = sR.getAllSessions(principals, false);
        System.out.println("Session size: " + sessionInformation.size());
		System.out.println("Get list of session ids:");
		for(SessionInformation ses : sessionInformation) {
			System.out.println(ses.getSessionId());
			ids.add(ses.getSessionId());
		}
        return ResponseEntity.ok(ids);
    }
    
    @GetMapping("/api/auth/killsession")
    public ResponseEntity<?> testkillsession(HttpServletRequest request) {
	    
	    List<HttpSession> activeS = sC.getActiveSessions();
        System.out.println("ActiveSession size: " + activeS.size());
    	System.out.println("Get list of ActSession ids:");
    	for(HttpSession ses : activeS) {
    		System.out.println(ses.getId());
    	}
    	String targetId = activeS.get(0).getId();
    	System.out.println("Killing " + targetId);
    	activeS.get(0).invalidate();
        return ResponseEntity.ok(targetId);
    }
    */
    /*
    @GetMapping("/api/auth/principal")
    public ResponseEntity<?> getprincipal(HttpServletRequest request) {
	    List<String> ids = new ArrayList<String>();

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    DefaultSaml2AuthenticatedPrincipal princ = ((DefaultSaml2AuthenticatedPrincipal) auth.getPrincipal());
	    for(Map.Entry<String, List<Object>> m : princ.getAttributes().entrySet())
	    	System.out.println(m.getKey() + ": " + m.toString());
	    System.out.println(princ.getRelyingPartyRegistrationId());
        
        return ResponseEntity.ok(ids);
    }
    
    @GetMapping("/api/auth/sessions")
    public ResponseEntity<?> getSessionIds(HttpServletRequest request) {
	    List<String> ids = new ArrayList<String>();

	    List<HttpSession> activeS = sC.getActiveSessions();
    	for(HttpSession ses : activeS) {
    		try {
    			List<String> attributes = new ArrayList<String>();
    			Collections.list(ses.getAttributeNames()).forEach
    				(a -> {
    					if(ses.getAttribute(a) instanceof AuthToken) {
    						AuthToken t = (AuthToken) ses.getAttribute(a);
    						attributes.add("\t" +  "Token: " + t.getToken());
    						attributes.add("\t" +  "Fingerprint: " + t.getClientFingerprint());
    						attributes.add("\t" +  "Expiry: " + t.getExpiryEpochMs());
    						attributes.add("\t" +  "Username: " + t.getUsername());
    						attributes.add("\t" +  "IP: " + t.getIp());
    						attributes.add("\t" +  "UA: " + t.getUa());
    					}
    					else
    						attributes.add("\t" + a + ": " + ses.getAttribute(a));});
    			ids.add(ses.getId() + "\nAttributes: "  + "\n" + String.join("\n", attributes));
    		}catch (IllegalStateException e) {
    			continue;
    		}
    	}
    	//activeS.stream().forEach(i -> System.out.println(i));
    	
        return ResponseEntity.ok(ids);
    }
    /*
    @GetMapping("/api/auth/servlet")
    public ResponseEntity<?> showInfoServlet(HttpServletRequest request) {
    	 List<String> info = new ArrayList<String>();
    	 
    	//System.out.println("----------");
    	HttpSession s = request.getSession(false);
    	if(s != null)
    		info.add("JSessionId: " + s.getId());
    	info.add("SSL ID: " + request.getAttribute("javax.servlet.request.ssl_session_id"));
    	//System.out.println("----------");
    	info.add("GetRemoteAddr:" + request.getRemoteAddr()); 
    	info.add("Header attributes:");
    	Enumeration<String> httpHeaderAttributes = request.getHeaderNames();
    	while (httpHeaderAttributes.hasMoreElements()) {
            String element = httpHeaderAttributes.nextElement();
            info.add("    " + element + ": " + request.getHeader(element));
        }
    	info.add("Servlet attributes:");
    	Enumeration<String> httpAttributes = request.getAttributeNames();
    	while (httpAttributes.hasMoreElements()) {
            String element = httpAttributes.nextElement();
            info.add("    " + element + ": " + request.getAttribute(element));
        }
        return ResponseEntity.ok(info);
    }
    */

}
