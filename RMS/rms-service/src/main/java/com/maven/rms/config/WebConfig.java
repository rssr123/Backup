package com.maven.rms.config;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.maven.rms.exceptionhandler.HandlerInterceptorImpl;
import com.maven.rms.services.AuthService;
import com.maven.rms.utils.RMSLogger;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

        @Autowired
        private RMSProperties rmsProperties;

        // @Override
        // public void addCorsMappings(CorsRegistry registry) {
        // RMSLogger.info("WebMvcConfigurer is initalized");
        // RMSLogger.info("CORS Address:" +
        // Arrays.toString(rmsProperties.getAllowOrigin()));

        // registry.addMapping("/**")
        // .allowedOrigins(rmsProperties.getAllowOrigin())
        // .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        // .allowedHeaders("Content-Type", "Authorization")
        // .exposedHeaders("Location")
        // .allowCredentials(false);
        // }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                RMSLogger.info("Initializing CORS Configuration");
                // RMSLogger.info("CORS Address:" +
                // Arrays.toString(rmsProperties.getAllowOrigin()));

                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList(rmsProperties.getAllowOrigin()));
                configuration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD"));
                configuration.setAllowedHeaders(Arrays.asList(
                                "Content-Type",
                                "Authorization",
                                "X-Requested-With",
                                "Accept",
                                "Origin",
                                "Access-Control-Request-Method",
                                "Access-Control-Request-Headers",
                                "Cache-Control",
                                "X-GSON-STATISTICS"));
                configuration.setAllowCredentials(true);
                configuration.setExposedHeaders(Arrays.asList(
                                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
                configuration.setMaxAge(0L);

                String corsConfigStr = String.format(
                                "CORS Configuration:\n- Allowed Origins: %s\n- Allowed Methods: %s\n- Allowed Headers: %s\n- Allow Credentials: %s\n- Exposed Headers:  %s\n"
                                                + //
                                                "- Max Age : %s",
                                configuration.getAllowedOrigins(),
                                configuration.getAllowedMethods(),
                                configuration.getAllowedHeaders(),
                                configuration.getAllowCredentials(),
                                configuration.getExposedHeaders(),
                                configuration.getMaxAge());

                RMSLogger.info(corsConfigStr);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public FilterRegistrationBean<CorsFilter> loggingCorsFilter() {
                FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>();
                bean.setFilter(new CorsFilter(corsConfigurationSource()) {
                        @Override
                        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain)
                                        throws ServletException, IOException {
                                RMSLogger.info("WebConfig is start working ");

                                // Log the Origin header from the request
                                String originHeader = request.getHeader("Origin");
                                RMSLogger.info("Received request from Origin: " + originHeader);

                                // Construct the full URL
                                StringBuffer requestURL = request.getRequestURL();
                                String queryString = request.getQueryString();
                                String fullUrl = queryString == null ? requestURL.toString()
                                                : requestURL.append('?').append(queryString).toString();

                                RMSLogger.info("Full request URL: " + fullUrl);
                                // System.out.println("[WebConfig.class] > Full request URL: " + fullUrl);
                                // Continue with the filter chain
                                super.doFilterInternal(request, response, filterChain);

                                // After CORS filter processing, log the CORS response headers
                                String acAllowOrigin = response.getHeader("Access-Control-Allow-Origin");
                                int statusCode = response.getStatus();

                                // Log when CORS is rejected - check if origin should be allowed but CORS
                                // headers are missing
                                if (originHeader != null && !originHeader.trim().isEmpty() && acAllowOrigin == null) {

                                        // Check if this origin is NOT in the allowed list, then log it
                                        String[] allowedOrigins = rmsProperties.getAllowOrigin();
                                        boolean shouldBeAllowed = Arrays.asList(allowedOrigins).contains(originHeader);

                                        if (!shouldBeAllowed) {
                                                log.error("UNAUTHORIZED ORIGIN DETECTED - Origin: " + originHeader +
                                                                ", Status: " + statusCode + ", URL: "
                                                                + safeGetUrl(request)
                                                                + ", Origin not in allowed list"
                                                                + "\n" + "Allowed Origins: "
                                                                + Arrays.toString(allowedOrigins)
                                                                + "\n"
                                                                + String.join("\n",
                                                                                AuthService.debugServletInformation(
                                                                                                request))
                                                                + "\n=====");
                                        }
                                }
                                // else
                                // System.out.println("SHOW what's in Access-Control-Allow-Origin header: " +
                                // acAllowOrigin);

                        }
                });
                bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // Make sure the filter has high precedence
                return bean;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HandlerInterceptorImpl());
        }

        private String safeGetString(String value, String defaultValue) {
                return (value != null && !value.trim().isEmpty()) ? value.trim() : defaultValue;
        }

        private String safeGetUrl(HttpServletRequest request) {
                try {
                        StringBuffer requestURL = request.getRequestURL();
                        if (requestURL == null)
                                return "Unknown URL";

                        String queryString = request.getQueryString();
                        return (queryString != null && !queryString.trim().isEmpty())
                                        ? requestURL.append('?').append(queryString).toString()
                                        : requestURL.toString();
                } catch (Exception e) {
                        return "Unknown URL";
                }
        }

}
