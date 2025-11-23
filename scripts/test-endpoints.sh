#!/bin/bash

# Script para probar los endpoints de la API
# Requiere que la aplicación esté ejecutándose en localhost:8080

BASE_URL="http://localhost:8080"
TOKEN=""

echo "=========================================="
echo "Prueba de Endpoints - Recetas API"
echo "=========================================="
echo ""

# Función para hacer login y obtener token
login() {
    echo "1. Probando login..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "usuario",
            "password": "password123"
        }')
    
    TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    
    if [ -z "$TOKEN" ]; then
        echo "❌ Error: No se pudo obtener token"
        echo "Respuesta: $RESPONSE"
        exit 1
    fi
    
    echo "✅ Login exitoso"
    echo "Token: ${TOKEN:0:50}..."
    echo ""
}

# Función para probar crear comentario
test_comentario() {
    echo "2. Probando crear comentario..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/api/comentarios" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "recetaId": 1,
            "comentario": "¡Excelente receta! Muy fácil de seguir."
        }')
    
    echo "Respuesta: $RESPONSE"
    echo ""
}

# Función para probar obtener comentarios
test_obtener_comentarios() {
    echo "3. Probando obtener comentarios..."
    RESPONSE=$(curl -s -X GET "$BASE_URL/api/comentarios/receta/1" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "Respuesta: $RESPONSE"
    echo ""
}

# Función para probar crear valoración
test_valoracion() {
    echo "4. Probando crear valoración..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/api/valoraciones" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "recetaId": 1,
            "puntuacion": 5
        }')
    
    echo "Respuesta: $RESPONSE"
    echo ""
}

# Función para probar obtener valoraciones
test_obtener_valoraciones() {
    echo "5. Probando obtener valoraciones..."
    RESPONSE=$(curl -s -X GET "$BASE_URL/api/valoraciones/receta/1" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "Respuesta: $RESPONSE"
    echo ""
}

# Función para probar compartir
test_compartir() {
    echo "6. Probando compartir receta..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/api/compartir" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "recetaId": 1,
            "plataforma": "facebook"
        }')
    
    echo "Respuesta: $RESPONSE"
    echo ""
}

# Función para probar obtener link de compartir
test_link_compartir() {
    echo "7. Probando obtener link de compartir..."
    RESPONSE=$(curl -s -X GET "$BASE_URL/api/compartir/receta/1/link" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "Respuesta: $RESPONSE"
    echo ""
}

# Ejecutar pruebas
login
test_comentario
test_obtener_comentarios
test_valoracion
test_obtener_valoraciones
test_compartir
test_link_compartir

echo "=========================================="
echo "Pruebas completadas"
echo "=========================================="

