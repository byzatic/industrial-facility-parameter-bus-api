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
  RESPONSE="$$(curl -fsS http://localhost:8080/api/v1/list-devices 2>/dev/null || true)"

  if echo "$$RESPONSE" | grep -q 'smh4'; then
    break
  fi

  if [ "$$i" -eq 60 ]; then
    echo "API did not receive expected MQTT payload"
    echo "Last /api/v1/list-devices response: $$RESPONSE"
    docker compose logs --tail=80 industrial-facility-parameter-bus-api
    exit 1
  fi

  sleep 1
done

echo
echo "Devices:"
curl -fsS http://localhost:8080/api/v1/list-devices
echo

echo
echo "Device smh4 registers:"
curl -fsS http://localhost:8080/api/v1/device/smh4
echo

echo
echo "Device smh4 register names:"
curl -fsS http://localhost:8080/api/v1/device/smh4/list-registers
echo

echo
echo "Register smh4/gss_rahod_vchas3:"
curl -fsS http://localhost:8080/api/v1/device/smh4/register/gss_rahod_vchas3
echo

echo
echo "Register matrix2/aks_rashodchas3:"
curl -fsS http://localhost:8080/api/v1/device/matrix2/register/aks_rashodchas3
echo

echo
echo "Logs:"
docker compose logs --tail=50 industrial-facility-parameter-bus-api
