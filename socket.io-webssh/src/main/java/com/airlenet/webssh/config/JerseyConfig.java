package com.airlenet.webssh.config;

import com.airlenet.webssh.controller.DeviceController;
import com.airlenet.webssh.controller.RttyController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(DeviceController.class);
        register(RttyController.class);
    }
}
