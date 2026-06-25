package com.maven.rms.config;

import java.util.Properties;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@Slf4j
public class EmailConfiguration {
	// private static final Logger logger = LoggerFactory.getLogger(EmailConfiguration.class);
	
    @Value("${spring.mail.host}")
    private String mailServerHost;

    @Value("${spring.mail.port}")
    private Integer mailServerPort;

    @Value("${spring.mail.username}")
    private String mailServerUsername;

    @Value("${spring.mail.password}")
    private String mailServerPassword;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String mailServerAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String mailServerStartTls;

    @Value("${spring.mail.properties.mail.protocol}")
    private String mailProto;

    @Value("${spring.mail.properties.mail.debug}")
    private String mailDebug;
    
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        mailSender.setHost(mailServerHost);
        mailSender.setPort(mailServerPort);
        
        mailSender.setUsername(mailServerUsername);
        mailSender.setPassword(mailServerPassword);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailProto);
        props.put("mail.smtp.auth", mailServerAuth);
        props.put("mail.smtp.starttls.enable", mailServerStartTls);
        props.put("mail.debug", mailDebug);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        log.info("JavaMailSender bean started. Target server: " + mailServerHost);
        
        return mailSender;
    }
    
    @Bean
    public SimpleMailMessage templateSimpleMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("This is the test email template for your email:\n%s\n");
        return message;
    }
    
    @Bean
    public ResourceBundleMessageSource emailMessageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("mailMessages");
        return messageSource;
    }

}