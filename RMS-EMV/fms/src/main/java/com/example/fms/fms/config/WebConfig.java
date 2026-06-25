// package com.example.fms.fms.config;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.core.io.Resource;
// import org.springframework.util.FileCopyUtils;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// import java.io.InputStreamReader;
// import java.nio.charset.StandardCharsets;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;

// @Configuration
// public class WebConfig implements WebMvcConfigurer {

//     // @Override
//     // public void addCorsMappings(CorsRegistry registry) {
//     //     List<String> allowedOrigins = getAllowedOrigins();

//     //     registry.addMapping("/**")
//     //             .allowedOrigins(allowedOrigins.toArray(new String[0])) // Dynamic origins
//     //             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//     //             .allowedHeaders("*")
//     //             .allowCredentials(true);
//     // }

//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/**")
//                 .allowedOrigins("https://172.188.90.132:8443") // Adjust this to your frontend URL
//                 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Ensure OPTIONS is allowed
//                 .allowedHeaders("Authorization", "Content-Type") // Explicitly allow required headers
//                 .allowCredentials(true); // Allow credentials (cookies, authentication headers)
//     }

//     private List<String> getAllowedOrigins() {
//         try {
//             Resource resource = new ClassPathResource("allow-origin.txt");
//             byte[] data = FileCopyUtils.copyToByteArray(resource.getInputStream());

//             String content = new String(data, StandardCharsets.UTF_8);
//             System.out.println("content: " + content);
//             return Arrays.asList(content.split("\\r?\\n")); // ✅ Convert file content to List
//         } catch (Exception e) {
//             e.printStackTrace();
//             return Collections.singletonList("https://172.188.90.132:8443/rmsbo"); // ✅ Works in Java 8+
//         }
//     }
// }




package com.example.fms.fms.config;

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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
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

            @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Match all endpoints
                .allowedOrigins("https://localhost:8080") // Angular's dev server origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // Allow cookies if needed
    }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                // RMSLogger.info("Initializing CORS Configuration");
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
                                "Cache-Control"));
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

                // RMSLogger.info(corsConfigStr);

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
                                // RMSLogger.info("WebConfig is start working ");

                                // Log the Origin header from the request
                                String originHeader = request.getHeader("Origin");
                                // RMSLogger.info("Received request from Origin: " + originHeader);

                                // Construct the full URL
                                StringBuffer requestURL = request.getRequestURL();
                                String queryString = request.getQueryString();
                                String fullUrl = queryString == null ? requestURL.toString()
                                                : requestURL.append('?').append(queryString).toString();

                                // RMSLogger.info("Full request URL: " + fullUrl);

                                // Continue with the filter chain
                                super.doFilterInternal(request, response, filterChain);

                                // After CORS filter processing, log the CORS response headers
                                String acAllowOrigin = response.getHeader("Access-Control-Allow-Origin");

                        }
                });
                bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // Make sure the filter has high precedence
                return bean;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HandlerInterceptorImpl());
        }

}
