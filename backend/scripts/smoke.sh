#!/usr/bin/env bash
# Smoke da API: health, contrato de erro 400/401/403/404 e login dos três perfis.
# Uso (API já no ar): ./scripts/smoke.sh
set -euo pipefail

BASE="${BASE_URL:-http://localhost:8080/api/v1}"

expect_code() {
  local got="$1"
  local want="$2"
  local label="$3"
  if [[ "$got" != "$want" ]]; then
    echo "FALHOU $label: HTTP $got (esperado $want)" >&2
    exit 1
  fi
  echo "OK $label ($want)"
}

health=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/health")
expect_code "$health" "200" "GET /health"

unauth=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/auth/me")
expect_code "$unauth" "401" "GET /auth/me sem token"

bad_login=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"email":"nao-e-email","senha":""}')
expect_code "$bad_login" "400" "POST /auth/login inválido"

login() {
  local email="$1"
  curl -s -X POST "$BASE/auth/login" \
    -H 'Content-Type: application/json' \
    -d "{\"email\":\"$email\",\"senha\":\"123456\"}" \
    | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])"
}

PEDRO=$(login "pedro.santos@aguiabranca.com.br")
ANA=$(login "ana.oliveira@aguiabranca.com.br")
MARCOS=$(login "marcos.silva@aguiabranca.com.br")

pedro_dash=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/dashboard/summary" \
  -H "Authorization: Bearer $PEDRO")
expect_code "$pedro_dash" "403" "Operador em dashboard"

ana_dash=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/dashboard/summary" \
  -H "Authorization: Bearer $ANA")
expect_code "$ana_dash" "403" "Gestor em dashboard"

marcos_dash=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/dashboard/summary" \
  -H "Authorization: Bearer $MARCOS")
expect_code "$marcos_dash" "200" "Líder em dashboard"

missing=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/ideias/000000000000000000000000" \
  -H "Authorization: Bearer $ANA")
expect_code "$missing" "404" "GET ideia inexistente"

pedro_proj=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/projetos" \
  -H "Authorization: Bearer $PEDRO")
expect_code "$pedro_proj" "403" "Operador em projetos"

echo "Smoke API concluído."
