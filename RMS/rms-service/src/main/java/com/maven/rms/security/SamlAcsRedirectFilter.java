package com.maven.rms.security;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.util.Log;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SamlAcsRedirectFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, java.io.IOException {

        String path = request.getRequestURI();
    	//log.warn("Hit SamlAcsRedirectFilter outside check!! Path:" + path);

        if (path.startsWith("/login/saml2/sso/ssm") && request.getParameter("SAMLResponse") == null) {
        	//log.warn("Hit SamlAcsRedirectFilter inside check 1!!");
        	System.out.println("[SamlAcsRedirectFilter] " + request.getSession().getId());
            response.sendRedirect("/home");
            return;
        }
        filterChain.doFilter(request, response);
    }
}