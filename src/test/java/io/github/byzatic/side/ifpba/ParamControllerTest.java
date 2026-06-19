package io.github.byzatic.side.ifpba;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParamControllerTest {

    private final Gson gson = new Gson();

    @Test
    void shouldReturnDeviceNames() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "smh4": {
                    "level1": 4.3125
                  },
                  "delta": {
                    "gss_osmos_status": 1.0
                  }
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        ParamController controller = new ParamController(store);

        ResponseEntity<ParamController.ResultResponse<List<String>>> response =
                controller.listDevices();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(List.of("delta", "smh4"), response.getBody().result());
    }

    @Test
    void shouldReturnDeviceRegisters() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "smh4": {
                    "gss_operating_min_time": 1857.0,
                    "vodeko_operating_min_time": 108.0
                  }
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        ParamController controller = new ParamController(store);

        ResponseEntity<ParamController.ResultResponse<List<ParamController.RegisterResponse>>> response =
                controller.getDevice("smh4");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        List<ParamController.RegisterResponse> result = response.getBody().result();

        assertEquals(2, result.size());
        assertEquals("gss_operating_min_time", result.get(0).register_name());
        assertEquals(1857.0, ((Number) result.get(0).register_value()).doubleValue());
    }

    @Test
    void shouldReturnRegisterNames() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "smh4": {
                    "B": 2.0,
                    "A": 1.0
                  }
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        ParamController controller = new ParamController(store);

        ResponseEntity<ParamController.ResultResponse<List<String>>> response =
                controller.listRegisters("smh4");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(List.of("A", "B"), response.getBody().result());
    }

    @Test
    void shouldReturnRegisterByName() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "smh4": {
                    "gss_rahod_vchas3": 63.305335998535156
                  }
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        ParamController controller = new ParamController(store);

        ResponseEntity<ParamController.RegisterResponse> response =
                controller.getRegister("smh4", "gss_rahod_vchas3");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("gss_rahod_vchas3", response.getBody().register_name());
        assertEquals(63.305335998535156, ((Number) response.getBody().register_value()).doubleValue());
    }

    @Test
    void shouldReturn404ForUnknownDevice() {
        ParamStore store = new ParamStore();
        ParamController controller = new ParamController(store);

        ResponseEntity<?> response = controller.getDevice("unknown");

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void shouldReturn404ForUnknownRegister() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "smh4": {
                    "level1": 4.3125
                  }
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        ParamController controller = new ParamController(store);

        ResponseEntity<?> response = controller.getRegister("smh4", "unknown");

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}