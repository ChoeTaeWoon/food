// com.fooddet.config.FastapiProps.java
package com.fooddet.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "fastapi")
public class FastapiProps {
    private String baseUrl;
    private int connectTimeoutMs = 4000;
    private int readTimeoutMs = 30000;
}
