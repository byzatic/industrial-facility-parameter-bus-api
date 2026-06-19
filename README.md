# Industrial Facility Parameter Bus API

Сервис предназначен для получения параметров промышленного объекта из MQTT-брокера и предоставления доступа к ним через REST API.

Сервис подписывается на указанный MQTT topic, принимает JSON-сообщения произвольной структуры и сохраняет актуальное состояние параметров в оперативной памяти. При поступлении нового сообщения предыдущее состояние заменяется новым.

Пример входящего MQTT-сообщения:

```
{
  "pump_station_1": {  — идентификатор устройства
    "inlet_pressure": 2.41, — регистр устройства
    "outlet_pressure": 5.87, — регистр устройства
    "flow_rate_m3h": 128.6 — регистр устройства
  },
  "water_treatment_1": {
    "osmosis_status": 1.0,
    "conductivity": 412.5
  },
  "power_unit_1": {
    "active_power_kw": 12.7,
    "frequency_hz": 50.0
  }
}
```
**Значение регистра может быть числом, строкой или логическим значением.**


## REST API

### Получение списка устройств
Возвращает список всех устройств, присутствующих в последнем полученном MQTT-сообщении.

Запрос:

`GET /api/v1/list-devices`

Ответ

```
{
  "result": [
    "pump_station_1",
    "power_unit_1",
    "water_treatment_1"
  ]
}
```

### Получение всех регистров устройства

Возвращает полный набор регистров и их значений для указанного устройства.

Запрос

`GET /api/v1/device/{deviceId}`

Пример:

`GET /api/v1/device/pump_station_1`

Ответ

```
{
  "result": [
    {
      "register_name": "inlet_pressure",
      "register_value": 2.41
    },
    {
      "register_name": "outlet_pressure",
      "register_value": 5.87
    }
  ]
}
```

Если параметр отсутствует, сервис возвращает код ответа HTTP 404 Not Found.


### Получение списка регистров устройства

Возвращает список имен регистров, доступных для указанного устройства.

Запрос

`GET /api/v1/device/{deviceId}/list-registers`

Ответ

```
{
  "result": [
    "inlet_pressure",
    "outlet_pressure",
    "flow_rate_m3h"
  ]
}
```
Если параметр отсутствует, сервис возвращает код ответа HTTP 404 Not Found.

### Получение значения регистра

Возвращает текущее значение указанного регистра устройства.

Запрос

`GET /api/v1/device/{deviceId}/register/{registerId}`

Пример:

`GET /api/v1/device/pump_station_1/register/inlet_pressure`

Ответ

```
{
  "register_name": "inlet_pressure",
  "register_value": 2.41
}
```

## Сборка и демо

Для сборки стенда использовать:
```shell
./scripts/setup-and-test.sh
```

Просто покурлить ручки:
```shell
Получить список устройств:
curl -s http://localhost:8080/api/v1/list-devices | jq
```
```shell
Получить все регистры устройства:
curl -s http://localhost:8080/api/v1/device/pump_station_1 | jq
Получить ошибку:
curl -i http://localhost:8080/api/v1/device/notvalid
```
```shell
Получить список регистров:
curl -s http://localhost:8080/api/v1/device/pump_station_1/list-registers | jq
Получить ошибку:
curl -i http://localhost:8080/api/v1/device/notvalid/list-registers
```
```shell
Получить конкретный регистр:
curl -s \
  http://localhost:8080/api/v1/device/pump_station_1/register/inlet_pressure \
  | jq
Получить ошибку:
curl -i http://localhost:8080/api/v1/device/notvalid/register/inlet_pressure
curl -i http://localhost:8080/api/v1/device/pump_station_1/register/notvalid
```

Вручную отправить другое сообщение:
```shell
docker compose exec mqtt mosquitto_pub \
-h localhost \
-p 1883 \
-t sensors/data \
-m '{your json as string}'
```

## Конфигурация

Сервис поддерживает настройку подключения к MQTT-брокеру через переменные окружения:

Переменная - Назначение \
MQTT_BROKER_URL - Адрес MQTT-брокера \
MQTT_CLIENT_ID - Идентификатор MQTT-клиента \
MQTT_TOPIC - MQTT topic для подписки \
MQTT_USERNAME - Имя пользователя MQTT \
MQTT_PASSWORD - Пароль MQTT

## Особенности
* Поддерживается автоматическое восстановление MQTT-соединения.
* поддерживаются произвольные наборы устройств и регистров.
* Данные полностью обновляются при получении нового MQTT-сообщения;
* Состояние хранится в памяти приложения.
* REST API предоставляет доступ только к последнему полученному набору данных.
* Аутентификация и авторизация REST API отсутствуют.
