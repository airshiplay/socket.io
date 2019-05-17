package com.airlenet.io.socket.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.socket.io")
public class SocketIOProperties {

    private boolean serverEndpointExporter;
    private String urlPath = "/socket.io/";

    public boolean isServerEndpointExporter() {
        return serverEndpointExporter;
    }

    public void setServerEndpointExporter(boolean serverEndpointExporter) {
        this.serverEndpointExporter = serverEndpointExporter;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }
}
