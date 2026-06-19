package io.github.byzatic.side.ifpba.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mqtt")
public record MqttProperties(
        String brokerUrl,
        String clientId,
        String topic,
        String username,
        String password
) {
}