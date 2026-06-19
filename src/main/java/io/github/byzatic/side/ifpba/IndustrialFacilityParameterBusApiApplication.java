package io.github.byzatic.side.ifpba;

import io.github.byzatic.side.ifpba.storage.MqttProperties;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MqttProperties.class)
public class IndustrialFacilityParameterBusApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(IndustrialFacilityParameterBusApiApplication.class, args);
    }
}