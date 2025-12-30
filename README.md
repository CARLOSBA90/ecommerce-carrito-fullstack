# Frontend – Angular

Este proyecto corresponde al frontend del sistema de e-commerce con carrito de compras.

El frontend fue desarrollado utilizando Angular 20.0.0.

---

## Tecnologías utilizadas

- Angular: 20.0.0
- Angular CLI: 20.x
- Node.js: recomendado 20 LTS
- npm

---

## Requisitos

Antes de ejecutar el proyecto, verificar que estén instaladas las siguientes herramientas:

```bash
node -v
npm -v
ng version
```

El comando ng version debe mostrar una versión compatible con Angular 20.0.0.

Si Angular CLI no está instalado o la versión es distinta, instalar la versión correspondiente:

```bash
npm install -g @angular/cli@20
```

---

## Ubicación del frontend

El código del frontend se encuentra en la carpeta:

```text
ecommerce-carrito-fullstack/
├── carrito-frontend/
```

---

## Ejecución del frontend

El frontend puede ejecutarse de dos maneras: mediante un script .sh o mediante comandos manuales.

---

### Opción A – Ejecución mediante script (.sh)

Válido para Linux, macOS y Windows con WSL o Git Bash.

El script debe ejecutarse desde la raíz del proyecto.

Primera ejecución (dar permisos):

```bash
chmod +x start-frontend.sh
```

Ejecutar el script:

```bash
./start-frontend.sh
```

La aplicación quedará disponible en:

http://localhost:4200

---

### Opción B – Ejecución mediante comandos manuales

Recomendado para Windows (CMD o PowerShell) o entornos sin soporte para .sh.

Ingresar a la carpeta del frontend:

```bash
cd carrito-frontend
```

Instalar dependencias:

```bash
npm install
```

Levantar el servidor de desarrollo:

```bash
ng serve
```

La aplicación quedará disponible en:

http://localhost:4200

---

## Notas

- Los archivos .sh no se ejecutan de forma nativa en Windows. 