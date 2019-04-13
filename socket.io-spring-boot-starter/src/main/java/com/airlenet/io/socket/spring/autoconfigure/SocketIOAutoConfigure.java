package com.airlenet.io.socket.spring.autoconfigure;

import com.airlenet.io.socket.server.transport.websocket.WebsocketIOServlet;
import com.airlenet.io.socket.server.transport.websocket.WebsocketTransportConnection;
import com.airlenet.io.socket.spring.DefaultWebsocketIOServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableConfigurationProperties(SocketIOProperties.class)
public class SocketIOAutoConfigure {

//    @ConditionalOnBean(TomcatServletWebServerFactory.class)
    @ConditionalOnProperty(name = "spring.socket.io.serverEndpointExporter", havingValue = "true", matchIfMissing = true)
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @ConditionalOnMissingBean
    @Bean
    public WebsocketTransportConnection websocketTransportConnection() {
        return new WebsocketTransportConnection();
    }

    @Bean
    @ConditionalOnMissingBean
    public WebsocketIOServlet websocketIOServlet() {
        return new DefaultWebsocketIOServlet();
    }

    @Bean
    public ServletRegistrationBean getServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(websocketIOServlet());
        registration.setName(DefaultWebsocketIOServlet.class.getName());
        registration.addUrlMappings("/socket.io/*");
        return registration;
    }
}
