package com.airlenet.io.socket.spring.autoconfigure;

import com.airlenet.io.socket.server.transport.websocket.WebsocketIOServlet;
import com.airlenet.io.socket.server.transport.websocket.WebsocketTransportConnection;
import com.airlenet.io.socket.spring.DefaultWebsocketIOServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

@Configuration
@EnableConfigurationProperties(SocketIOProperties.class)
public class SocketIOAutoConfigure {

    //    @ConditionalOnBean(TomcatServletWebServerFactory.class)
    @ConditionalOnProperty(name = "spring.socket.io.serverEndpointExporter", havingValue = "true", matchIfMissing = true)
    @ConditionalOnBean(value = {ServletWebServerFactory.class})
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
    public ServletRegistrationBean getServletRegistrationBean(SocketIOProperties properties) {
        ServletRegistrationBean registration = new ServletRegistrationBean(websocketIOServlet());
        registration.setName(DefaultWebsocketIOServlet.class.getName());
        registration.addUrlMappings("/socket.io/*");
        return registration;
    }

//    @Bean
//    public ServerEndpointRegistration websocketEndpoint() {
//        return new ServerEndpointRegistration("/socket.io/", websocketTransportConnection());
//    }
}
