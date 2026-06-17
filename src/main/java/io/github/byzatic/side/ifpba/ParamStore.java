package io.github.byzatic.side.ifpba;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ParamStore {

    private final ConcurrentHashMap<String, JsonElement> params = new ConcurrentHashMap<>();

    public void replaceAllFields(JsonObject root) {
        if (root == null) {
            throw new IllegalArgumentException("MQTT payload must be JSON object");
        }

        ConcurrentHashMap<String, JsonElement> newParams = new ConcurrentHashMap<>();

        root.entrySet().forEach(entry ->
                newParams.put(entry.getKey(), entry.getValue())
        );

        params.clear();
        params.putAll(newParams);
    }

    public List<String> getKeys() {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        return keys;
    }

    public Optional<JsonElement> getValue(String key) {
        return Optional.ofNullable(params.get(key));
    }
}