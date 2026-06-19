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
import static org.hamcrest.Matchers.hasSize;
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
                  "smh4": {
                    "gss_operating_min_time": 1857.0,
                    "vodeko_operating_min_time": 108.0,
                    "level1": 4.3125,
                    "gss_rahod_vchas3": 63.305335998535156
                  },
                  "delta": {
                    "gss_osmos_status": 1.0
                  },
                  "matrix": {
                    "aks_osmos_status": 1.0
                  }
                }
                """, JsonObject.class);

        store.replaceAllFields(json);
    }

    @Test
    void shouldReturnDevicesFromApi() throws Exception {
        mockMvc.perform(get("/api/v1/list-devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", contains("delta", "matrix", "smh4")));
    }

    @Test
    void shouldReturnDeviceRegistersFromApi() throws Exception {
        mockMvc.perform(get("/api/v1/device/smh4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(4)))
                .andExpect(jsonPath("$.result[0].register_name").value("gss_operating_min_time"))
                .andExpect(jsonPath("$.result[0].register_value").value(1857.0))
                .andExpect(jsonPath("$.result[1].register_name").value("vodeko_operating_min_time"))
                .andExpect(jsonPath("$.result[1].register_value").value(108.0));
    }

    @Test
    void shouldReturnRegisterNamesFromApi() throws Exception {
        mockMvc.perform(get("/api/v1/device/smh4/list-registers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", contains(
                        "gss_operating_min_time",
                        "gss_rahod_vchas3",
                        "level1",
                        "vodeko_operating_min_time"
                )));
    }

    @Test
    void shouldReturnRegisterFromApi() throws Exception {
        mockMvc.perform(get("/api/v1/device/smh4/register/gss_rahod_vchas3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.register_name").value("gss_rahod_vchas3"))
                .andExpect(jsonPath("$.register_value").value(63.305335998535156));
    }

    @Test
    void shouldReturn404ForUnknownDevice() throws Exception {
        mockMvc.perform(get("/api/v1/device/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownDeviceRegisters() throws Exception {
        mockMvc.perform(get("/api/v1/device/unknown/list-registers"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownRegister() throws Exception {
        mockMvc.perform(get("/api/v1/device/smh4/register/unknown"))
                .andExpect(status().isNotFound());
    }
}
