package com.example.nonreldataproject.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "opcua")
public class OpcProperties {

    private String username;
    private String password;
    private String endPointUrl;
    private Long requestTimeout;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEndPointUrl() {
        return endPointUrl;
    }

    public void setEndPointUrl(String endPointUrl) {
        this.endPointUrl = endPointUrl;
    }

    public Long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}
