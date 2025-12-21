# Frontend Angular - Tienda Online

Este es el frontend de la aplicación Tienda Online desarrollado con Angular 17 y Bootstrap 5.

## Características

- **Responsive Design**: Adaptado a 3 tamaños de pantalla (Mobile, Tablet, Desktop) usando Bootstrap Grid de 12 columnas
- **Validaciones de formularios**: Validaciones completas en todos los formularios
- **Validaciones de contraseña**: 6 validaciones de seguridad implementadas
- **Comunicación con APIs**: Comunicación completa con los microservicios del backend

## Instalación

1. Instalar dependencias:
```bash
npm install
```

2. Configurar la URL de la API en los servicios (por defecto: `http://localhost:8080`)

3. Ejecutar el servidor de desarrollo:
```bash
npm start
```

La aplicación estará disponible en `http://localhost:4200`

## Estructura

- `src/app/components/`: Componentes de la aplicación
- `src/app/services/`: Servicios para comunicación con APIs
- `src/app/app-routing.module.ts`: Configuración de rutas

## Páginas Implementadas

- Login
- Registro
- Recuperar Contraseña
- Modificación de Perfil
- Inicio (catálogo de productos)
- Productos
- Pedidos

