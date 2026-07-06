package com.campushub.gateway.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component("campusHubGatewayProperties")
@ConfigurationProperties(prefix = "campus.gateway")
public class GatewayProperties {
    private List<String> whitelist = new ArrayList<>();
    private int rateLimitPerMinute = 120;
}
