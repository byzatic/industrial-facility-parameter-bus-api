package io.github.byzatic.side.ifpba;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ParamController.class)
@Import(ParamStore.class)
class ParamControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParamStore store;

    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        JsonObject json = gson.fromJson("""
                {
                  "A": "text",
                  "B": "text2",
                  "some_key": "text3"
                }
                """, JsonObject.class);

        store.replaceAllFields(json);
    }

    @Test
    void shouldReturnKeysFromApi() throws Exception {
        mockMvc.perform(get("/api/v1/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", contains("A", "B", "some_key")));
    }

    @Test
    void shouldReturnParamFromApi() throws Exception {
        mockMvc.perform(get("/api/v1/getparam/A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("A"))
                .andExpect(jsonPath("$.value").value("text"));
    }

    @Test
    void shouldReturn404ForUnknownParam() throws Exception {
        mockMvc.perform(get("/api/v1/getparam/unknown"))
                .andExpect(status().isNotFound());
    }
}