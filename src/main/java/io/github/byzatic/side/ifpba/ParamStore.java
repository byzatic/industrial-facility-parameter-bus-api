package io.github.byzatic.side.ifpba;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ParamStore {

    private final ConcurrentHashMap<String, JsonObject> devices = new ConcurrentHashMap<>();

    public void replaceAllFields(JsonObject root) {
        if (root == null) {
            throw new IllegalArgumentException("MQTT payload must be JSON object");
        }

        ConcurrentHashMap<String, JsonObject> newDevices = new ConcurrentHashMap<>();

        root.entrySet().forEach(entry -> {
            if (entry.getValue() != null && entry.getValue().isJsonObject()) {
                newDevices.put(entry.getKey(), entry.getValue().getAsJsonObject());
            }
        });

        devices.clear();
        devices.putAll(newDevices);
    }

    public List<String> getDeviceNames() {
        List<String> result = new ArrayList<>(devices.keySet());
        Collections.sort(result);
        return result;
    }

    public Optional<JsonObject> getDevice(String deviceId) {
        return Optional.ofNullable(devices.get(deviceId));
    }

    public List<String> getRegisterNames(String deviceId) {
        JsonObject device = devices.get(deviceId);
        if (device == null) {
            return List.of();
        }

        List<String> result = new ArrayList<>(device.keySet());
        Collections.sort(result);
        return result;
    }

    public Optional<JsonElement> getRegisterValue(String deviceId, String registerId) {
        JsonObject device = devices.get(deviceId);
        if (device == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(device.get(registerId));
    }
}