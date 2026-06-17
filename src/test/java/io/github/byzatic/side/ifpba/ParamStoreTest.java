package io.github.byzatic.side.ifpba;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParamStoreTest {

    private final Gson gson = new Gson();

    @Test
    void shouldStoreAndReturnKeys() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "B": "text2",
                  "A": "text1",
                  "some_key": "text3"
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        List<String> keys = store.getKeys();

        assertEquals(List.of("A", "B", "some_key"), keys);
    }

    @Test
    void shouldReturnValueByKey() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "A": "text"
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        var value = store.getValue("A");

        assertTrue(value.isPresent());
        assertEquals("text", value.get().getAsString());
    }

    @Test
    void shouldReturnEmptyForUnknownKey() {
        ParamStore store = new ParamStore();

        JsonObject json = gson.fromJson("""
                {
                  "A": "text"
                }
                """, JsonObject.class);

        store.replaceAllFields(json);

        assertTrue(store.getValue("unknown").isEmpty());
    }

    @Test
    void shouldReplaceOldFields() {
        ParamStore store = new ParamStore();

        JsonObject first = gson.fromJson("""
                {
                  "A": "text"
                }
                """, JsonObject.class);

        JsonObject second = gson.fromJson("""
                {
                  "B": "new_text"
                }
                """, JsonObject.class);

        store.replaceAllFields(first);
        store.replaceAllFields(second);

        assertTrue(store.getValue("A").isEmpty());
        assertTrue(store.getValue("B").isPresent());
        assertEquals(List.of("B"), store.getKeys());
    }

    @Test
    void shouldThrowExceptionForNullJson() {
        ParamStore store = new ParamStore();

        assertThrows(IllegalArgumentException.class, () ->
                store.replaceAllFields(null)
        );
    }
}