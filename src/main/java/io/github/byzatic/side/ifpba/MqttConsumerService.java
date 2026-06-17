package io.github.byzatic.side.ifpba;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class MqttConsumerService {

    private final MqttProperties properties;
    private final ParamStore paramStore;
    private final Gson gson = new Gson();

    private MqttClient client;

    public MqttConsumerService(
            MqttProperties properties,
            ParamStore paramStore
    ) {
        this.properties = properties;
        this.paramStore = paramStore;
    }

    @PostConstruct
    public void start() throws MqttException {
        client = new MqttClient(
                properties.brokerUrl(),
                properties.clientId()
        );

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(30);

        if (properties.username() != null && !properties.username().isBlank()) {
            options.setUserName(properties.username());
        }

        if (properties.password() != null && !properties.password().isBlank()) {
            options.setPassword(properties.password().toCharArray());
        }

        client.setCallback(new MqttCallbackExtended() {

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                try {
                    client.subscribe(properties.topic(), 1);
                    System.out.println("Subscribed to MQTT topic: " + properties.topic());
                } catch (MqttException e) {
                    System.err.println("MQTT subscribe failed: " + e.getMessage());
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                System.err.println("MQTT connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                try {
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8);

                    JsonElement element = gson.fromJson(payload, JsonElement.class);

                    if (element == null || !element.isJsonObject()) {
                        System.err.println("Ignored MQTT message: payload is not JSON object");
                        return;
                    }

                    JsonObject root = element.getAsJsonObject();
                    paramStore.replaceAllFields(root);

                } catch (JsonSyntaxException e) {
                    System.err.println("Invalid MQTT JSON: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("MQTT message handling failed: " + e.getMessage());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

        client.connect(options);
    }

    @PreDestroy
    public void stop() throws MqttException {
        if (client != null) {
            if (client.isConnected()) {
                client.disconnect();
            }
            client.close();
        }
    }
}