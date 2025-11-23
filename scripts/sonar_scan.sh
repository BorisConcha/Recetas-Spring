#!/bin/bash

# Script para ejecutar análisis de SonarQube
# Requiere que SonarQube esté ejecutándose en localhost:9000

echo "=========================================="
echo "Análisis de SonarQube - Recetas Spring"
echo "=========================================="

# Verificar si SonarQube está ejecutándose
if ! curl -s http://localhost:9000/api/system/status | grep -q "UP"; then
    echo "ERROR: SonarQube no está ejecutándose en localhost:9000"
    echo "Por favor, inicia SonarQube primero:"
    echo "  docker run -d --name sonarqube -p 9000:9000 sonarqube:community"
    exit 1
fi

echo "SonarQube está ejecutándose correctamente"
echo ""

# Compilar el proyecto
echo "Compilando el proyecto..."
mvn clean compile -DskipTests

if [ $? -ne 0 ]; then
    echo "ERROR: La compilación falló"
    exit 1
fi

echo "Compilación exitosa"
echo ""

# Ejecutar análisis de SonarQube
echo "Ejecutando análisis de SonarQube..."
mvn sonar:sonar \
    -Dsonar.projectKey=recetas-spring \
    -Dsonar.host.url=http://localhost:9000 \
    -Dsonar.login=admin \
    -Dsonar.password=admin

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "Análisis completado exitosamente"
    echo "=========================================="
    echo "Revisa los resultados en: http://localhost:9000"
    echo ""
    echo "Para ver el proyecto: http://localhost:9000/dashboard?id=recetas-spring"
else
    echo ""
    echo "ERROR: El análisis de SonarQube falló"
    exit 1
fi

