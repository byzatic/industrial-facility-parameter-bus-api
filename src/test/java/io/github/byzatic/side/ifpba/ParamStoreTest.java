package io.github.byzatic.side.ifpba;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.byzatic.side.ifpba.storage.ParamStore;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ParamStoreTest {

    private final Gson gson = new Gson();

    @Test
    void shouldStoreAndReturnDeviceNames() {
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

        assertEquals(List.of("delta", "smh4"), store.getDeviceNames());
    }

    @Test
    void shouldReturnDeviceById() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "smh4": {
                    "level1": 4.3125
                  }
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        Optional<JsonObject> device = store.getDevice("smh4");

        assertTrue(device.isPresent());
        assertEquals(4.3125, device.get().get("level1").getAsDouble());
    }

    @Test
    void shouldReturnEmptyForUnknownDevice() {
        ParamStore store = new ParamStore();

        assertTrue(store.getDevice("unknown").isEmpty());
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

        assertEquals(List.of("A", "B"), store.getRegisterNames("smh4"));
    }

    @Test
    void shouldReturnEmptyRegisterNamesForUnknownDevice() {
        ParamStore store = new ParamStore();

        assertEquals(List.of(), store.getRegisterNames("unknown"));
    }

    @Test
    void shouldReturnRegisterValue() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "smh4": {
                    "gss_rahod_vchas3": 63.305335998535156
                  }
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        Optional<JsonElement> value =
                store.getRegisterValue("smh4", "gss_rahod_vchas3");

        assertTrue(value.isPresent());
        assertEquals(63.305335998535156, value.get().getAsDouble());
    }

    @Test
    void shouldReturnEmptyForUnknownRegister() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "smh4": {
                    "level1": 4.3125
                  }
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        assertTrue(store.getRegisterValue("smh4", "unknown").isEmpty());
    }

    @Test
    void shouldReplaceOldDevicesOnNewPayload() {
        ParamStore store = new ParamStore();

        JsonObject firstJson = gson.fromJson("""
                {
                  "smh4": {
                    "level1": 4.3125
                  }
                }
                """, JsonObject.class);

        JsonObject secondJson = gson.fromJson("""
                {
                  "delta": {
                    "gss_osmos_status": 1.0
                  }
                }
                """, JsonObject.class);

        store.replaceAllFields(firstJson);
        store.replaceAllFields(secondJson);

        assertTrue(store.getDevice("smh4").isEmpty());
        assertTrue(store.getDevice("delta").isPresent());
        assertEquals(List.of("delta"), store.getDeviceNames());
    }

    @Test
    void shouldIgnoreNonObjectRootFields() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "smh4": {
                    "level1": 4.3125
                  },
                  "bad_field": 123.0
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        assertEquals(List.of("smh4"), store.getDeviceNames());
        assertTrue(store.getDevice("bad_field").isEmpty());
    }

    @Test
    void shouldThrowExceptionForNullPayload() {
        ParamStore store = new ParamStore();

        assertThrows(
                IllegalArgumentException.class,
                () -> store.replaceAllFields(null)
        );
    }
}