package com.maven.rms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.maven.rms.services.NotificationService;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${rms.application.backPortalURL}")
    private String backPortalURL;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Extract base URL from backPortalURL and create allowed origins
        String[] allowedOrigins = createAllowedOrigins(backPortalURL);

        registry.addHandler(notificationService(), "/notifications")
                .setAllowedOrigins(allowedOrigins)
                .withSockJS();
    }

    private String[] createAllowedOrigins(String backPortalURL) {
        List<String> origins = new ArrayList<>();

        try {
            URL url = new URL(backPortalURL);
            String baseOrigin = url.getProtocol() + "://" + url.getHost();
            if (url.getPort() != -1) {
                baseOrigin += ":" + url.getPort();
            }

            // Add both the base origin and the full URL
            origins.add(baseOrigin); // https://rmsdev.ssm4u.com.my
            origins.add(backPortalURL); // https://rmsdev.ssm4u.com.my/rmsbo

        } catch (Exception e) {
            // Fallback: just use the original URL
            origins.add(backPortalURL);
        }

        return origins.toArray(new String[0]);
    }

    @Bean
    public NotificationService notificationService() {
        return new NotificationService();
    }
}