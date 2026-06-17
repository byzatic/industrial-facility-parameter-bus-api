#!/usr/bin/env bash
set -euo pipefail

mkdir -p mosquitto/config

cat > mosquitto/config/mosquitto.conf <<'EOF'
listener 1883 0.0.0.0
allow_anonymous true
persistence false
log_dest stdout
EOF

docker compose down -v --remove-orphans
docker compose up --build -d test-producer industrial-facility-parameter-bus-api

echo "Waiting for API..."
for i in {1..30}; do
  if curl -fsS http://localhost:8080/api/v1/list >/dev/null 2>&1; then
    break
  fi

  sleep 1
done

docker compose run --rm test-producer

echo
echo "Keys:"
curl -s http://localhost:8080/api/v1/list
echo

echo
echo "Param A:"
curl -s http://localhost:8080/api/v1/getparam/A
echo

echo
echo "Logs:"
docker compose logs --tail=50 industrial-facility-parameter-bus-api