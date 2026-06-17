package io.github.byzatic.side.ifpba;

import com.google.gson.JsonElement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ParamController {

    private final ParamStore paramStore;

    public ParamController(ParamStore paramStore) {
        this.paramStore = paramStore;
    }

    @GetMapping("/list")
    public List<String> list() {
        return paramStore.getKeys();
    }

    @GetMapping("/getparam/{keyName}")
    public ResponseEntity<?> getParam(@PathVariable String keyName) {
        return paramStore.getValue(keyName)
                .<ResponseEntity<?>>map(value -> ResponseEntity.ok(Map.of(
                        "key", keyName,
                        "value", convertJsonElement(value)
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Object convertJsonElement(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        if (element.isJsonPrimitive()) {
            var primitive = element.getAsJsonPrimitive();

            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }

            if (primitive.isNumber()) {
                return primitive.getAsNumber();
            }

            return primitive.getAsString();
        }

        return element.toString();
    }
}