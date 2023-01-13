package com.isxcode.demo.atlas.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "leo.demo.atlas")
public class AtlasProperties {

    private String host;

    private String username;

    private String password;
}
