package com.airlenet.io.socket.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.socket.io")
public class SocketIOProperties {

    private boolean serverEndpointExporter;
}
