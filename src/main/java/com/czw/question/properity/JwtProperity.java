package com.czw.question.properity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "czw.jwt")
@Data
public class JwtProperity {
    private String SecretKey;
    private Long Ttl;
    private String TokenName;
}
