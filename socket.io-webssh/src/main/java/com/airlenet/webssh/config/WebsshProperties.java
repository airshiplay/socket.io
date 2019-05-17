package com.airlenet.webssh.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spring.webssh")
@Data
public class WebsshProperties {
    private String publicKey;
    private String privateKey;
}
