// com.fooddet.config.FastapiProps
package com.fooddet.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "app.fastapi")
public class FastapiProps {
    private String baseUrl = "http://localhost:8000";
    private int connectTimeoutMs = 4000;
    private int readTimeoutMs = 30000;
}
