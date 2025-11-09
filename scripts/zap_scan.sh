#!/bin/bash

################################################################################
# Script de Análisis Automatizado con OWASP ZAP
# Aplicación: Recetas del Mundo
# Versión: 1.0
################################################################################

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuración
TARGET_URL="http://localhost:8080"
ZAP_PORT=8090
REPORT_DIR="./documentacion/zap_reports"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

echo -e "${BLUE}╔══════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   OWASP ZAP - Análisis Automatizado de Seguridad    ║${NC}"
echo -e "${BLUE}║   Aplicación: Recetas del Mundo                     ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════╝${NC}"
echo ""

# Función para verificar si un servicio está corriendo
check_service() {
    local url=$1
    local name=$2
    
    echo -e "${YELLOW}[*] Verificando ${name}...${NC}"
    
    if curl -s --head --request GET "${url}" | grep "200\|302" > /dev/null; then 
        echo -e "${GREEN}[✓] ${name} está corriendo${NC}"
        return 0
    else
        echo -e "${RED}[✗] ${name} no está disponible${NC}"
        return 1
    fi
}

# Función para iniciar ZAP en modo daemon
start_zap_daemon() {
    echo -e "${YELLOW}[*] Iniciando ZAP en modo daemon...${NC}"
    
    # Verificar si ZAP ya está corriendo
    if curl -s "http://localhost:${ZAP_PORT}" > /dev/null 2>&1; then
        echo -e "${GREEN}[✓] ZAP ya está corriendo en puerto ${ZAP_PORT}${NC}"
        return 0
    fi
    
    # Iniciar ZAP (ajustar ruta según instalación)
    if command -v zap.sh &> /dev/null; then
        zap.sh -daemon -port ${ZAP_PORT} -config api.disablekey=true > /dev/null 2>&1 &
        ZAP_PID=$!
        echo -e "${GREEN}[✓] ZAP iniciado (PID: ${ZAP_PID})${NC}"
        
        # Esperar a que ZAP esté listo
        echo -e "${YELLOW}[*] Esperando a que ZAP esté listo...${NC}"
        for i in {1..30}; do
            if curl -s "http://localhost:${ZAP_PORT}" > /dev/null 2>&1; then
                echo -e "${GREEN}[✓] ZAP está listo${NC}"
                return 0
            fi
            sleep 2
        done
        
        echo -e "${RED}[✗] ZAP no respondió después de 60 segundos${NC}"
        return 1
    else
        echo -e "${RED}[✗] Comando 'zap.sh' no encontrado${NC}"
        echo -e "${YELLOW}[i] Por favor, instala ZAP o ajusta la ruta en el script${NC}"
        return 1
    fi
}

# Función para ejecutar Spider Scan
run_spider_scan() {
    echo ""
    echo -e "${BLUE}══════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}   FASE 1: SPIDER SCAN (Descubrimiento de URLs)      ${NC}"
    echo -e "${BLUE}══════════════════════════════════════════════════════${NC}"
    
    echo -e "${YELLOW}[*] Iniciando Spider Scan en ${TARGET_URL}...${NC}"
    
    # Iniciar Spider
    SPIDER_ID=$(curl -s "http://localhost:${ZAP_PORT}/JSON/spider/action/scan/?url=${TARGET_URL}" | jq -r '.scan')
    
    if [ -z "$SPIDER_ID" ] || [ "$SPIDER_ID" == "null" ]; then
        echo -e "${RED}[✗] Error al iniciar Spider Scan${NC}"
        return 1
    fi
    
    echo -e "${GREEN}[✓] Spider Scan iniciado (ID: ${SPIDER_ID})${NC}"
    
    # Monitorear progreso
    while true; do
        PROGRESS=$(curl -s "http://localhost:${ZAP_PORT}/JSON/spider/view/status/?scanId=${SPIDER_ID}" | jq -r '.status')
        
        if [ "$PROGRESS" == "100" ]; then
            echo -e "${GREEN}[✓] Spider Scan completado (100%)${NC}"
            break
        fi
        
        echo -e "${YELLOW}[*] Progreso: ${PROGRESS}%${NC}"
        sleep 3
    done
    
    # Obtener resultados
    URLS_FOUND=$(curl -s "http://localhost:${ZAP_PORT}/JSON/spider/view/results/?scanId=${SPIDER_ID}" | jq '.urlsInScope | length')
    echo -e "${GREEN}[✓] URLs encontradas: ${URLS_FOUND}${NC}"
    
    return 0
}

# Función para ejecutar Passive Scan
run_passive_scan() {
    echo ""
    echo -e "${BLUE}══════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}   FASE 2: PASSIVE SCAN (Análisis Pasivo)            ${NC}"
    echo -e "${BLUE}══════════════════════════════════════════════════════${NC}"
    
    echo -e "${YELLOW}[*] Ejecutando Passive Scan...${NC}"
    
    # El Passive Scan se ejecuta automáticamente mientras se explora
    # Esperar a que complete
    while true; do
        RECORDS_TO_SCAN=$(curl -s "http://localhost:${ZAP_PORT}/JSON/pscan/view/recordsToScan/" | jq -r '.recordsToScan')
        
        if [ "$RECORDS_TO_SCAN" == "0" ]; then
            echo -e "${GREEN}[✓] Passive Scan completado${NC}"
            break
        fi
        
        echo -e "${YELLOW}[*] Registros pendientes: ${RECORDS_TO_SCAN}${NC}"
        sleep 3
    done
    
    return 0
}

# Función para ejecutar Active Scan
run_active_scan() {
    echo ""
    echo -e "${BLUE}══════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}   FASE 3: ACTIVE SCAN (Pruebas Activas)             ${NC}"
    echo -e "${BLUE}══════════════════════════════════════════════════════${NC}"
    
    echo -e "${YELLOW}[*] Iniciando Active Scan en ${TARGET_URL}...${NC}"
    echo -e "${YELLOW}[!] ADVERTENCIA: Esto puede tomar varios minutos${NC}"
    
    # Iniciar Active Scan
    SCAN_ID=$(curl -s "http://localhost:${ZAP_PORT}/JSON/ascan/action/scan/?url=${TARGET_URL}" | jq -r '.scan')
    
    if [ -z "$SCAN_ID" ] || [ "$SCAN_ID" == "null" ]; then
        echo -e "${RED}[✗] Error al iniciar Active Scan${NC}"
        return 1
    fi
    
    echo -e "${GREEN}[✓] Active Scan iniciado (ID: ${SCAN_ID})${NC}"
    
    # Monitorear progreso
    while true; do
        PROGRESS=$(curl -s "http://localhost:${ZAP_PORT}/JSON/ascan/view/status/?scanId=${SCAN_ID}" | jq -r '.status')
        
        if [ "$PROGRESS" == "100" ]; then
            echo -e "${GREEN}[✓] Active Scan completado (100%)${NC}"
            break
        fi
        
        echo -e "${YELLOW}[*] Progreso: ${PROGRESS}%${NC}"
        sleep 5
    done
    
    return 0
}

# Función para obtener alertas
get_alerts() {
    echo ""
    echo -e "${BLUE}══════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}   RESULTADOS: Vulnerabilidades Encontradas          ${NC}"
    echo -e "${BLUE}══════════════════════════════════════════════════════${NC}"
    
    # Obtener todas las alertas
    ALERTS=$(curl -s "http://localhost:${ZAP_PORT}/JSON/core/view/alerts/?baseurl=${TARGET_URL}")
    
    # Contar por severidad
    HIGH=$(echo "$ALERTS" | jq '[.alerts[] | select(.risk=="High")] | length')
    MEDIUM=$(echo "$ALERTS" | jq '[.alerts[] | select(.risk=="Medium")] | length')
    LOW=$(echo "$ALERTS" | jq '[.alerts[] | select(.risk=="Low")] | length')
    INFO=$(echo "$ALERTS" | jq '[.alerts[] | select(.risk=="Informational")] | length')
    TOTAL=$(echo "$ALERTS" | jq '.alerts | length')
    
    echo ""
    echo -e "${RED}🔴 Alta (High):          ${HIGH}${NC}"
    echo -e "${YELLOW}🟠 Media (Medium):       ${MEDIUM}${NC}"
    echo -e "${GREEN}🟡 Baja (Low):           ${LOW}${NC}"
    echo -e "${BLUE}🔵 Informacional:        ${INFO}${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}   TOTAL:                ${TOTAL}${NC}"
    echo ""
    
    # Mostrar alertas críticas
    if [ "$HIGH" -gt 0 ]; then
        echo -e "${RED}[!] VULNERABILIDADES CRÍTICAS DETECTADAS:${NC}"
        echo "$ALERTS" | jq -r '.alerts[] | select(.risk=="High") | "  • \(.alert) (\(.url))"'
        echo ""
    fi
    
    return 0
}

# Función para generar reportes
generate_reports() {
    echo ""
    echo -e "${BLUE}══════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}   GENERANDO REPORTES                                 ${NC}"
    echo -e "${BLUE}══════════════════════════════════════════════════════${NC}"
    
    # Crear directorio de reportes
    mkdir -p "${REPORT_DIR}"
    
    # Reporte HTML
    echo -e "${YELLOW}[*] Generando reporte HTML...${NC}"
    curl -s "http://localhost:${ZAP_PORT}/OTHER/core/other/htmlreport/" > "${REPORT_DIR}/zap_report_${TIMESTAMP}.html"
    echo -e "${GREEN}[✓] Reporte HTML: ${REPORT_DIR}/zap_report_${TIMESTAMP}.html${NC}"
    
    # Reporte JSON
    echo -e "${YELLOW}[*] Generando reporte JSON...${NC}"
    curl -s "http://localhost:${ZAP_PORT}/JSON/core/view/alerts/?baseurl=${TARGET_URL}" > "${REPORT_DIR}/zap_report_${TIMESTAMP}.json"
    echo -e "${GREEN}[✓] Reporte JSON: ${REPORT_DIR}/zap_report_${TIMESTAMP}.json${NC}"
    
    # Reporte XML
    echo -e "${YELLOW}[*] Generando reporte XML...${NC}"
    curl -s "http://localhost:${ZAP_PORT}/OTHER/core/other/xmlreport/" > "${REPORT_DIR}/zap_report_${TIMESTAMP}.xml"
    echo -e "${GREEN}[✓] Reporte XML: ${REPORT_DIR}/zap_report_${TIMESTAMP}.xml${NC}"
    
    # Reporte Markdown resumido
    echo -e "${YELLOW}[*] Generando reporte Markdown...${NC}"
    generate_markdown_report
    echo -e "${GREEN}[✓] Reporte Markdown: ${REPORT_DIR}/zap_report_${TIMESTAMP}.md${NC}"
    
    echo ""
    echo -e "${GREEN}[✓] Todos los reportes generados exitosamente${NC}"
    
    return 0
}

# Función para generar reporte Markdown resumido
generate_markdown_report() {
    local MD_FILE="${REPORT_DIR}/zap_report_${TIMESTAMP}.md"
    
    ALERTS=$(curl -s "http://localhost:${ZAP_PORT}/JSON/core/view/alerts/?baseurl=${TARGET_URL}")
    
    cat > "$MD_FILE" << EOF
# Reporte de Análisis ZAP - Recetas del Mundo

**Fecha:** $(date '+%Y-%m-%d %H:%M:%S')
**Target:** ${TARGET_URL}
**Herramienta:** OWASP ZAP

## Resumen de Vulnerabilidades

| Severidad | Cantidad |
|-----------|----------|
| 🔴 Alta | $(echo "$ALERTS" | jq '[.alerts[] | select(.risk=="High")] | length') |
| 🟠 Media | $(echo "$ALERTS" | jq '[.alerts[] | select(.risk=="Medium")] | length') |
| 🟡 Baja | $(echo "$ALERTS" | jq '[.alerts[] | select(.risk=="Low")] | length') |
| 🔵 Informacional | $(echo "$ALERTS" | jq '[.alerts[] | select(.risk=="Informational")] | length') |
| **TOTAL** | $(echo "$ALERTS" | jq '.alerts | length') |

## Vulnerabilidades Encontradas

EOF

    # Agregar alertas al reporte
    echo "$ALERTS" | jq -r '.alerts[] | "### \(.alert)\n\n- **Riesgo:** \(.risk)\n- **Confianza:** \(.confidence)\n- **URL:** \(.url)\n- **Descripción:** \(.description)\n- **Solución:** \(.solution)\n\n---\n"' >> "$MD_FILE"
}

# Función de limpieza
cleanup() {
    echo ""
    echo -e "${YELLOW}[*] Limpiando...${NC}"
    
    # Detener ZAP si fue iniciado por el script
    if [ ! -z "$ZAP_PID" ]; then
        echo -e "${YELLOW}[*] Deteniendo ZAP (PID: ${ZAP_PID})...${NC}"
        kill $ZAP_PID 2>/dev/null
        echo -e "${GREEN}[✓] ZAP detenido${NC}"
    fi
}

# Manejo de señales
trap cleanup EXIT INT TERM

################################################################################
# MAIN - Ejecución Principal
################################################################################

main() {
    echo -e "${YELLOW}[*] Verificando requisitos...${NC}"
    
    # Verificar que jq está instalado
    if ! command -v jq &> /dev/null; then
        echo -e "${RED}[✗] 'jq' no está instalado${NC}"
        echo -e "${YELLOW}[i] Instalar con: brew install jq (macOS) o apt-get install jq (Linux)${NC}"
        exit 1
    fi
    
    # Verificar aplicación target
    if ! check_service "${TARGET_URL}" "Aplicación Recetas"; then
        echo -e "${RED}[!] La aplicación debe estar corriendo en ${TARGET_URL}${NC}"
        echo -e "${YELLOW}[i] Ejecutar con: ./mvnw spring-boot:run${NC}"
        exit 1
    fi
    
    # Iniciar o verificar ZAP
    if ! start_zap_daemon; then
        echo -e "${RED}[!] No se pudo iniciar ZAP${NC}"
        exit 1
    fi
    
    echo ""
    echo -e "${GREEN}[✓] Todos los requisitos cumplidos${NC}"
    echo -e "${YELLOW}[*] Iniciando análisis de seguridad...${NC}"
    
    # Ejecutar análisis
    run_spider_scan || { echo -e "${RED}[✗] Spider Scan falló${NC}"; exit 1; }
    run_passive_scan || { echo -e "${RED}[✗] Passive Scan falló${NC}"; exit 1; }
    run_active_scan || { echo -e "${RED}[✗] Active Scan falló${NC}"; exit 1; }
    
    # Obtener y mostrar resultados
    get_alerts
    
    # Generar reportes
    generate_reports
    
    echo ""
    echo -e "${GREEN}╔══════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║   ✓ ANÁLISIS COMPLETADO EXITOSAMENTE                ║${NC}"
    echo -e "${GREEN}╚══════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${BLUE}[i] Los reportes están disponibles en: ${REPORT_DIR}${NC}"
    echo ""
}

# Ejecutar script
main "$@"

