package io.github.byzatic.side.ifpba;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ParamController {

    private final ParamStore paramStore;

    public ParamController(ParamStore paramStore) {
        this.paramStore = paramStore;
    }

    @GetMapping("/list-devices")
    public ResponseEntity<ResultResponse<List<String>>> listDevices() {
        return ResponseEntity.ok(new ResultResponse<>(paramStore.getDeviceNames()));
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<ResultResponse<List<RegisterResponse>>> getDevice(
            @PathVariable String deviceId
    ) {
        return paramStore.getDevice(deviceId)
                .map(device -> ResponseEntity.ok(new ResultResponse<>(toRegisters(device))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/device/{deviceId}/list-registers")
    public ResponseEntity<ResultResponse<List<String>>> listRegisters(
            @PathVariable String deviceId
    ) {
        if (paramStore.getDevice(deviceId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new ResultResponse<>(paramStore.getRegisterNames(deviceId))
        );
    }

    @GetMapping("/device/{deviceId}/register/{registerId}")
    public ResponseEntity<RegisterResponse> getRegister(
            @PathVariable String deviceId,
            @PathVariable String registerId
    ) {
        return paramStore.getRegisterValue(deviceId, registerId)
                .map(value -> ResponseEntity.ok(
                        new RegisterResponse(registerId, convertJsonElement(value))
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private List<RegisterResponse> toRegisters(JsonObject device) {
        return device.entrySet()
                .stream()
                .map(entry -> new RegisterResponse(
                        entry.getKey(),
                        convertJsonElement(entry.getValue())
                ))
                .toList();
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

    public record ResultResponse<T>(
            T result
    ) {
    }

    public record RegisterResponse(
            String register_name,
            Object register_value
    ) {
    }
}