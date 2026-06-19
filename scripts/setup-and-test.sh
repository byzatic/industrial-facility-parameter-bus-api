#!/usr/bin/env bash
set -euo pipefail

mkdir -p mosquitto/config

cat > mosquitto/config/mosquitto.conf <<'MOSQUITTO_CONF'
listener 1883 0.0.0.0
allow_anonymous true
persistence false
log_dest stdout
MOSQUITTO_CONF

docker compose down -v --remove-orphans
docker compose up --build -d eclipse-mosquitto industrial-facility-parameter-bus-api test-producer

echo "Waiting for API and MQTT payload..."

for i in {1..60}; do
  RESPONSE="$(curl -fsS http://localhost:8080/api/v1/list-devices 2>/dev/null || true)"

  if echo "$RESPONSE" | grep -q 'pump_station_1'; then
    break
  fi

  if [ "$i" -eq 60 ]; then
    echo "API did not receive expected MQTT payload"
    echo "Last /api/v1/list-devices response: $RESPONSE"
    echo
    echo "API logs:"
    docker compose logs --tail=80 industrial-facility-parameter-bus-api
    echo
    echo "Producer logs:"
    docker compose logs --tail=80 test-producer
    exit 1
  fi

  sleep 1
done

echo
echo "Devices:"
curl -fsS http://localhost:8080/api/v1/list-devices
echo

echo
echo "Device pump_station_1 registers:"
curl -fsS http://localhost:8080/api/v1/device/pump_station_1
echo

echo
echo "Device pump_station_1 register names:"
curl -fsS http://localhost:8080/api/v1/device/pump_station_1/list-registers
echo

echo
echo "Register pump_station_1/inlet_pressure:"
curl -fsS http://localhost:8080/api/v1/device/pump_station_1/register/inlet_pressure
echo

echo
echo "Register water_treatment_1/conductivity:"
curl -fsS http://localhost:8080/api/v1/device/water_treatment_1/register/conductivity
echo

echo
echo "Register power_unit_1/active_power_kw:"
curl -fsS http://localhost:8080/api/v1/device/power_unit_1/register/active_power_kw
echo

echo
echo "Logs:"
docker compose logs --tail=50 industrial-facility-parameter-bus-api