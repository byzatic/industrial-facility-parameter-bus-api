package io.github.byzatic.side.ifpba.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DBDataStorageService {

    private final DBDataRepository repository;

    public DBDataStorageService(DBDataRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void saveMqttPayload(JsonObject root) {
        LocalDateTime timestamp = LocalDateTime.now();
        List<DBData> rows = new ArrayList<>();

        for (var deviceEntry : root.entrySet()) {
            String deviceName = deviceEntry.getKey();
            JsonElement deviceElement = deviceEntry.getValue();

            if (deviceElement == null || !deviceElement.isJsonObject()) {
                continue;
            }

            JsonObject device = deviceElement.getAsJsonObject();

            for (var registerEntry : device.entrySet()) {
                String registerName = registerEntry.getKey();
                JsonElement value = registerEntry.getValue();

                if (value == null || !value.isJsonPrimitive()) {
                    continue;
                }

                var primitive = value.getAsJsonPrimitive();

                if (!primitive.isNumber()) {
                    continue;
                }

//                String fullRegisterName = deviceName + "." + registerName;
                String fullRegisterName = registerName;
                Double data = primitive.getAsDouble();

                rows.add(new DBData(timestamp, fullRegisterName, data));
            }
        }

        if (!rows.isEmpty()) {
            repository.saveAll(rows);
        }
    }
}