package com.maven.rms.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRole;
import com.maven.rms.services.AuthService;

@CrossOrigin
@RestController
@RequestMapping("/")
@Slf4j
public class LoginController implements ErrorController{
	// private static final Logger logger =
	// LoggerFactory.getLogger(LoginController.class);

	/*
	 * For Future header authentication
	 * 
	 * @Autowired
	 * private HttpServletRequest request;
	 */

	//@Autowired
	//private EmailService emailer;
	@Autowired
	private AuthService authSvc;

	@Value("${spring.test.mail.username}")
	private String testEmail;

	@Value("${idp.email.attribute.key}")
	private String emailAttributeKey;

	public LoginController() {
		log.info("LoginController services is started");
	}

	@RequestMapping("/loginInfo")
	public Map<String, String> check(HttpServletRequest request) {
		Map<String, String> model = new HashMap<String, String>();
		/*
		 * Authentication authentication =
		 * SecurityContextHolder.getContext().getAuthentication();
		 * StringBuilder roles = new StringBuilder();
		 * 
		 * authentication.getAuthorities().stream()
		 * .map(GrantedAuthority::getAuthority)
		 * .forEach(roles::append);
		 * 
		 * roles = roles.replace(0, 5, "");
		 * 
		 * while(roles.indexOf("ROLE_") > 0)
		 * roles = roles.replace(roles.indexOf("ROLE_"), roles.indexOf("ROLE_") + 5,
		 * ",");
		 * 
		 * model.put("username", authentication.getName());
		 * model.put("roles", roles.toString());
		 */
		
		if(authSvc.isAuthenticated(request)) {
            RMSUser u = authSvc.getCurrentUser();
        	StringBuilder roles = new StringBuilder();
        	if(u != null) {
	            u.getRoles().stream().filter((RMSUserRole role) -> role.getStatus().equals("A"))
		    		.map(RMSUserRole::getRole).forEach(role -> roles.append(role.getRoleNmEn() + ","));            
	            model.put("username", u.getSsm4uuserrefno());
	            model.put("name", u.getNm());
	            model.put("email", u.getEmail());
				model.put("roles", roles.deleteCharAt(roles.length()-1).toString());
        	}
        	else {
        		model.put("username", "Anonymous");
	            model.put("name", "Unknown_User");
	            model.put("email", "-1");
				model.put("roles", "ANONYMOUS");
        	}
        		
		}
		return model;
	}
	/*
	@RequestMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("Logout here>????");
		log.info("Logout initiated for user: {}", 
				(authentication != null ? authentication.getName() : "anonymous"));
		
		try {
			// Clear Spring Security context
			if (authentication != null) {
				SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
				logoutHandler.logout(request, response, authentication);
			}
			
			// Clear security context
			SecurityContextHolder.clearContext();
			
			// Invalidate session
			if (request.getSession(false) != null) {
				request.getSession().invalidate();
			}
			
			log.info("Application logout completed successfully");
			return ResponseEntity.ok("Logout Successfully!");
			
		} catch (Exception e) {
			log.error("Error during logout process", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error during logout");
		}
	}
	*/
    @RequestMapping("/error")
    public void handleError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getUserPrincipal() != null) {
            String referer = request.getHeader("Referer");
            response.sendRedirect(referer != null ? referer : "/");
        }
    }
	/*
	@RequestMapping(value = "/userInfo")
	public ResponseEntity<?> test(@AuthenticationPrincipal Saml2AuthenticatedPrincipal sPrincipal, Principal principal,
			Authentication auth) {
		StringBuilder info = new StringBuilder();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.forEach(info::append);
		// Set<String> roles = authorities.stream().map(r ->
		// r.getAuthority()).collect(Collectors.toSet());

		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			// Not Working
			// UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			// System.out.println("User has authorities: " + userDetails.getAuthorities());

			// NULL auth if user is anon
			info.append("\nCurrent User (principal): " + principal.getName());
			info.append("\nCurrent User (auth): " + auth.getName());

			info.append("\n\nFrom injection");
			info.append("\nPrincipal class: " + principal.getClass().toString());
			Saml2Authentication tmp = (Saml2Authentication) principal;
			info.append("\nPrincipal getPrincipal(from injection: " + tmp.getPrincipal().getClass().toString());
			info.append("\nAuth class: " + auth.getClass().toString());
			tmp = (Saml2Authentication) auth;
			info.append("\nPrincipal getPrincipal(from injection: " + tmp.getPrincipal().getClass().toString());

			if (principal == auth)
				info.append("\nAuth and Principal Injection are the same!");

			info.append("\n\nSaml2AuthenticatedPrincipal stuff");
			sPrincipal.getAttributes().forEach((key, value) -> {
				info.append("\nKey: " + key);
				for (int i = 0; i < value.size(); i++)
					info.append("\nVal " + Integer.toString(i) + ": " + value.get(i));
			});

			// System.out.println("User dets: " + user.getEmail() + " " + user.getNm() + " "
			// + user.getSsm4uuserrefno());

			// UserDetails user = uds.loadUserByUsername(auth.getName());
			// user.getAuthorities().stream().map(GrantedAuthority::getAuthority).forEach(System.out::println);

			/*
			 * //Used to update authorities on the fly:
			 * RMSUser useri =
			 * userRepository.findUserByEmail(auth.getName()).orElseGet(null);
			 * List<GrantedAuthority> authorities = useri.getRoles().stream()
			 * .map((Role role) -> new SimpleGrantedAuthority(role.getRoleNmEn()))
			 * .collect(Collectors.toList());
			 * Saml2Authentication samlAuthentication =
			 * (Saml2Authentication)SecurityContextHolder.getContext().getAuthentication();
			 * Saml2Authentication newSamlAuth = new Saml2Authentication(
			 * (DefaultSaml2AuthenticatedPrincipal)samlAuthentication.getPrincipal(),
			 * samlAuthentication.getSaml2Response(), authorities);
			 * newSamlAuth.setDetails(authentication.getDetails());
			 * SecurityContextHolder.getContext().setAuthentication(newSamlAuth);
			 */
		/*}

		info.append("\n\nCurrent User (Authentication): " + authentication.getName());
		if (authentication.getDetails() != null)
			if (!authentication.getDetails().equals(null))
				info.append("\nextra info:" + authentication.getDetails().toString());

		Object princip = authentication.getPrincipal();

		if (princip instanceof UserDetails)
			info.append("\n\nIs Userdetails: " + ((UserDetails) princip).getUsername());
		else if (princip instanceof DefaultSaml2AuthenticatedPrincipal) {
			DefaultSaml2AuthenticatedPrincipal pr = (DefaultSaml2AuthenticatedPrincipal) princip;
			Map<String, List<Object>> attr = pr.getAttributes();
			info.append("\n\nAttributes:");
			attr.forEach((key, value) -> info.append("\n" + key + ":" + value));
		} else
			info.append("\n\nPrinciple class(from context): " + princip.getClass().toString());

		String email = (String) ((DefaultSaml2AuthenticatedPrincipal) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal())
				.getAttribute(emailAttributeKey).get(0);
		info.append("Extracted email! It is: " + email);
		return ResponseEntity.ok(info);
		// return ResponseEntity.internalServerError().build();
	}

	@RequestMapping("/testEmail")
	public ResponseEntity<?> emailTest() {
		emailer.saveEmailDets(new Email("Test", testEmail, "", "", "Test Email", "This is a test email", null));
		return APIResponse.SuccessResponse("Test email to " + testEmail + " generated. Awaiting scheduler to send.");
	}
	*/
}