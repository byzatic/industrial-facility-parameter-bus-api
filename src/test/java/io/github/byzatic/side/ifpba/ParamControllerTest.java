package io.github.byzatic.side.ifpba;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParamControllerTest {

    private final Gson gson = new Gson();

    @Test
    void shouldReturnKeys() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "B": "text2",
                  "A": "text1"
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        ParamController controller = new ParamController(store);

        List<String> result = controller.list();

        assertEquals(List.of("A", "B"), result);
    }

    @Test
    void shouldReturnParamByKey() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "A": "text"
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        ParamController controller = new ParamController(store);

        ResponseEntity<?> response = controller.getParam("A");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertEquals("A", body.get("key"));
        assertEquals("text", body.get("value"));
    }

    @Test
    void shouldReturn404ForUnknownKey() {
        ParamStore store = new ParamStore();
        ParamController controller = new ParamController(store);

        ResponseEntity<?> response = controller.getParam("unknown");

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}