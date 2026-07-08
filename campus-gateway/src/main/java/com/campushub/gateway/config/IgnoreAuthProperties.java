package com.campushub.gateway.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Gateway paths that do not require JWT authentication.
 */
@Data
@Component
@ConfigurationProperties(prefix = "campus.gateway.ignore-auth")
public class IgnoreAuthProperties {
    private List<String> paths = new ArrayList<>();
}
