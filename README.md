# Carrito de Compras en Ecommerce

Módulo robusto de carrito de compras desarrollado con Stack (Java 17 + Angular 20), diseñado para demostrar la implementación de reglas de negocio complejas, descuentos dinámicos y arquitectura limpia.

### Especificaciones del Backend
- El backend tiene seeds para inicializar correctamente en modo local
- El carrito se crea y actualiza en backend, se puede realizar un carrito en modo invitado o por sesión
- Posee Spring Security para confirmación de carrito, el usuario/cliente siempre es requerido
- Maneja una entidad de descuentos para realizar descuentos de forma dinámica y según condiciones por la cantidad de ítems y/o importe total, también incluye el TIER/Nivel del cliente y/o promo de fechas especiales
- El proyecto posee un BackOffice sin securizar con consumo SOAP para ver historial de clientes en cuanto a niveles

## Arquitectura Tecnológica

Stack tecnológico actualizado:

| Capa | Tecnología | Versión | Rol |
|------|------------|---------|-----|
| **Frontend** | Angular | 20.x | UI Reactiva, Standalone Components |
| **Backend** | Spring Boot | 3.4.13 | Core de Negocio & API REST |
| **Database** | MySQL | 8.0 | Persistencia Relacional |
| **Java** | OpenJDK | 17 | Runtime |
| **DevOps** | Docker | 24+ | Contenedorización e Infraestructura |

## Inicialización y Despliegue

La infraestructura se despliega mediante orquestación de contenedores y scripts de automatización.

### 1. Infraestructura de Datos
```bash
docker compose -f infra.yml up -d
```

### 2. Backend Service
El servicio cuenta con scripts de arranque que gestionan automáticamente la inyección de variables de entorno desde `.env`.

**UNIX / MacOS:**
```bash
./start-backend.sh
```

**Windows:**
```bat
start-backend.bat
```

**Ejecución Manual (Maven):**
Alternativamente, puede compilar y ejecutar directamente (requiere variables de entorno cargadas):
```bash
cd carrito-backend
mvn clean install -DskipTests
mvn spring-boot:run
```

### 3. Frontend App
```bash
cd carrito-frontend
npm install
ng serve
```

## Servicios y APIs

El sistema expone interfaces para integración y gestión:

- **REST API**: Gestión transaccional y catálogo (`/api`).
- **Swagger Documentation**: Documentación interactiva disponible en `/swagger-ui.html`.
- **SOAP Service**: Interfaz legacy para reportes corporativos y auditoría de clientes (`/ws`).

## Testing y Calidad

Cobertura de pruebas automatizadas para lógica crítica:
```bash
cd carrito-backend
mvn test
```
 

--- 