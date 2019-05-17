package com.airlenet.webssh.config;

import org.crsh.spring.SpringBootstrap;
import org.crsh.spring.SpringWebBootstrap;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.annotation.WebListener;
import java.util.EventListener;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(WebsshProperties.class)
@ServletComponentScan("com.airlenet.webssh")
@ComponentScan("com.airlenet.webssh")
@EnableCaching
public class ApplicationConfig {

    @Bean
    SpringBootstrap springBootstrap() {
        SpringWebBootstrap springBootstrap = new SpringWebBootstrap();
        springBootstrap.setCmdMountPointConfig("war:/WEB-INF/crash/commands/");
        springBootstrap.setConfMountPointConfig("war:/WEB-INF/crash/");
        Properties properties = new Properties();
//         <!-- VFS configuration -->
        properties.put("crash.vfs.refresh_period", 1);
//         <!-- SSH configuration -->
        properties.put("crash.ssh.port", 2000);
//<!-- Optional SSH timeouts -->
        properties.put("crash.ssh.auth_timeout", 300000);
        properties.put("crash.ssh.idle_timeout", 300000);
//         <!-- Telnet configuration -->
        properties.put("crash.telnet.port", 5000);
//        <!-- Authentication configuration -->
        properties.put("crash.auth", "simple");
        properties.put("crash.auth.simple.username", "admin");
        properties.put("crash.auth.simple.password", "admin");
        springBootstrap.setConfig(properties);
        return springBootstrap;
    }
//    @Bean
//    public ServletListenerRegistrationBean<EventListener> getDemoListener(){
//        ServletListenerRegistrationBean<EventListener> registrationBean = new ServletListenerRegistrationBean<>();
//        registrationBean.setListener(new ContextLoaderListener());
//        //registrationBean.setOrder(1);
//        return registrationBean;
//    }
}
