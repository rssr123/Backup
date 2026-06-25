package com.maven.rms.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.base.Strings;
import com.maven.rms.services.AuthService;
import com.maven.rms.utils.DebugInformation;
import com.maven.rms.utils.HazelcastSessionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FrontFilter extends OncePerRequestFilter {
	
	private final String loginUrlEntryPoint;
	private final Boolean debugLogin = false;
	
	public FrontFilter(@Value("${angular.login.entry.url}") String loginUrlEntryPoint) {
		this.loginUrlEntryPoint = loginUrlEntryPoint;
	}
	
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {
    	
   	
    	if(request.getRequestURI().contains(loginUrlEntryPoint)) {    		
	        String nonce = request.getParameter("gson");
	        String relay = request.getParameter("relayState");
	    	String ip = request.getHeader("x-forwarded-for") == null 
	    			? request.getRemoteAddr() : request.getHeader("x-forwarded-for");

	        if (nonce == null)
	        	nonce = request.getHeader("X-GSON-STATISTICS");
	        
        	HttpSession ses = request.getSession(true);
        	ses.setAttribute("nonce", nonce);
        	ses.setAttribute("x-forwarded-for", ip);
        	ses.setAttribute("redirectUrl", request.getParameter("redirectUrl"));
            if(relay != null && ses.getAttribute("relayState") == null)
            	ses.setAttribute("relayState", relay);
            
            //Exception for payment pages to lax security
            if(request.getParameter("redirectUrl") != null 
            		&& request.getParameter("redirectUrl").contains("/payment-page?pr="))
            	ses.setAttribute("relayState", "lax");
            
            if(debugLogin) {
        		List<String> attr = new ArrayList<String>();
        		Collections.list(ses.getAttributeNames()).forEach(a -> attr.add("    " + a + ": " + ses.getAttribute(a)));
            	log.warn("[FrontFilter] Session created on touching login link (" + request.getRequestURI() +"): [" 
            			+ HazelcastSessionUtil.encodeToCookieSessionId(ses.getId()) + "]\nAttributes:\n"  + String.join("\n", attr),
            			new DebugInformation(String.join("\n", AuthService.debugServletInformation(request))));
            }
            //System.out.println("Login Attributes:");
            //Collections.list(ses.getAttributeNames()).forEach(a -> System.out.println("     " + a + ": " + ses.getAttribute(a)));
	        
    	}
    	else if(request.getRequestURI().contains("/rms_paymentPage")) {
            //Exception for payment pages to lax security
        	String nonce = request.getHeader("X-GSON-STATISTICS");
	    	String ip = request.getHeader("x-forwarded-for") == null 
	    			? request.getRemoteAddr() : request.getHeader("x-forwarded-for");

        	HttpSession ses = request.getSession(true);
        	ses.setAttribute("relayState", "lax");
        	ses.setAttribute("nonce", nonce);
        	ses.setAttribute("x-forwarded-for", ip);
    	}
    	/*
    	else if(request.getRequestURI().contains("testing1234")) {
    		response.sendRedirect("https://localhost:4200/logout");
    		return;
    	}
    	*/
    	else if(debugLogin && request.getRequestURI().contains("/login/saml2/sso")) {
    		HttpSession ses = request.getSession(false);
    		String sesId = null;
    		List<String> attr = new ArrayList<String>();
    		try { 
    			sesId = HazelcastSessionUtil.encodeToCookieSessionId(ses.getId());
    			Collections.list(ses.getAttributeNames()).forEach(a -> attr.add("    " + a + ": " + ses.getAttribute(a)));
    		} catch(IllegalStateException | NullPointerException e) {}
    		
        	log.warn("[FrontFilter] '/login/saml2/sso' (normally return from IDP) was accessed successfully. SessionID: ["
        			+ (sesId == null ? "null]" : sesId + "]\nAttributes:\n" + String.join("\n", attr)),
        			new DebugInformation(String.join("\n", AuthService.debugServletInformation(request))));
    	}
        filterChain.doFilter(request, response);
    }
}