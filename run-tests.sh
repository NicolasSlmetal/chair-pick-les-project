#!/bin/bash

set -e

echo "🔧 Subindo containers com Docker Compose..."
docker compose -f docker-compose-test.yaml up --build -d

echo "⏳ Aguardando containers ficarem saudáveis..."

# Aguarda até que todos os containers estejam com status "healthy" ou "running"
function wait_for_containers() {
  retries=30
  for ((i=0; i<retries; i++)); do
    unhealthy=$(docker inspect --format='{{.Name}}: {{range .State.Health}} {{.Status}} {{end}}' $(docker ps -q) | grep -v "healthy" || true)
    if [ -z "$unhealthy" ]; then
      echo "✅ Todos os containers estão saudáveis."
      return 0
    fi
    echo "⌛ Aguardando containers saudáveis... Tentativa $((i+1))/$retries"
    sleep 5
  done

  echo "❌ Timeout: alguns containers não ficaram saudáveis a tempo:"
  echo "$unhealthy"
  return 1
}

function cleanup {
    echo "🧹 Encerrando containers e removendo volumes..."
    docker compose -f docker-compose-test.yaml down -v

}
trap cleanup EXIT

wait_for_containers

echo "🚀 Executando testes com Maven..."
mvn test
