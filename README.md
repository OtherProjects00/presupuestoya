# PresupuestoYa

Aplicación web para que plomeros, electricistas, pintores, herreros y pequeños prestadores de servicios armen presupuestos profesionales en minutos, los compartan con un enlace y hagan seguimiento de si fueron aceptados o rechazados.

Está pensada como un proyecto **simple de leer y modificar**: Spring Boot + Thymeleaf, una base de datos H2 por defecto y PostgreSQL disponible para cuando quieras dar el salto a producción.

---

## 1. Qué incluye

- Registro e inicio de sesión con contraseña cifrada.
- Panel con resumen de presupuestos y valor aceptado.
- Clientes con datos de contacto y notas internas.
- Presupuestos con ítems, cantidades, precios, IVA y totales calculados.
- Estados: **Borrador → Enviado → Aceptado / Rechazado**, con vencimiento automático.
- Enlace público para que el cliente vea el presupuesto sin crear cuenta.
- Enlace con vencimiento, revocable y regenerable.
- Vista imprimible para guardar como PDF desde el navegador.
- Perfil del negocio: nombre, responsable, teléfono/WhatsApp, email, dirección, CUIT/RUT/RFC, moneda, IVA y validez por defecto.
- Interfaz propia con paleta azul, paneles redondeados y tipografía **Roboto** local; sin CDN ni dependencias de frontend.
- Seguridad desde el diseño: CSRF, BCrypt, cookies seguras, cabeceras CSP, control de pertenencia de datos y límite de intentos de login.
- Dockerfile multi-etapa con usuario no root y `compose.yaml` listo para usar.

---

## 2. Stack técnico

| Pieza | Versión / elección | Para qué |
|---|---|---|
| Java | 21 | Lenguaje y runtime |
| Spring Boot | 3.5.16 | Base de la aplicación |
| Spring MVC + Thymeleaf | Incluido en Boot | Páginas HTML renderizadas en servidor |
| Spring Security | Incluido en Boot | Login, sesiones, CSRF, cabeceras |
| Spring Data JPA + Hibernate | Incluido en Boot | Persistencia |
| Bean Validation | Incluido en Boot | Validación de formularios |
| H2 | Base por defecto, archivo local | Cero configuración para empezar |
| PostgreSQL | Driver incluido | Opción recomendada para producción |
| Maven | 3.9+ | Build |
| Docker | Multi-stage | Empaquetado y despliegue |

No hay JavaScript de framework, ni CDN, ni estilos inline. Eso simplifica el mantenimiento y permite una Content Security Policy estricta. La tipografía también se sirve desde la propia app: `static/fonts/roboto-latin.woff2` es una fuente variable local, así que `font-src 'self'` se mantiene sin excepciones.

---

## 3. Arranque rápido en local

### Requisitos

- Java 21
- Maven 3.9 o superior

### Ejecutar

```bash
cd PresupuestoYa
mvn spring-boot:run
```

Abrí <http://localhost:8080>.

Para desarrollo, el perfil `dev` desactiva la caché de Thymeleaf:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Si editás una plantilla mientras la app está corriendo, reiniciá la app o copiá los recursos con `mvn resources:resources` para que tome el archivo actualizado desde `target/classes`.

### Primer recorrido

1. Entrá a **Crear cuenta** y registrate.
2. Completá **Mi negocio** (nombre, teléfono, moneda, IVA y validez).
3. Creá un **cliente**.
4. Creá un **presupuesto**, cargá ítems y guardá el borrador.
5. Abrí el presupuesto y usá **Marcar como enviado**.
6. Generá el **enlace público** y copialo en ese momento: por seguridad, la URL completa no se vuelve a mostrar.
7. Abrí el enlace en una ventana incógnita para ver lo que ve el cliente.
8. Usá **Imprimir / PDF** y elegí “Guardar como PDF” en el diálogo del navegador.

La base local queda en `./data/` (archivos de H2). Esa carpeta está ignorada por Git.

---

## 4. Docker

### Construir y ejecutar

```bash
docker build -t presupuestoya .
docker run --name presupuestoya \
  -p 8080:8080 \
  -v presupuestoya-data:/app/data \
  -e APP_BASE_URL=http://localhost:8080 \
  -e SESSION_COOKIE_SECURE=false \
  presupuestoya
```

Abrí <http://localhost:8080>.

### Con Docker Compose

```bash
cp .env.example .env
docker compose up --build -d
```

Para ver logs:

```bash
docker compose logs -f app
```

Para detener:

```bash
docker compose down
```

El volumen `presupuestoya-data` conserva la base aunque se borre el contenedor. `docker compose down -v` **borra los datos**.

### Qué hace el Dockerfile

1. **Etapa build**: usa una imagen Maven con JDK 21, descarga dependencias y empaqueta el JAR.
2. **Etapa runtime**: usa solo el JRE de Java 21 sobre Alpine.
3. Crea el usuario `app` sin privilegios y ejecuta la aplicación con ese usuario.
4. Expone el puerto 8080 y define un healthcheck contra `/actuator/health`.
5. Guarda la base en `/app/data`, que debe montarse como volumen.

---

## 5. Configuración

Todo se configura por variables de entorno. En local podés usar valores por defecto; en producción usá un archivo `.env` o las variables del proveedor.

| Variable | Valor por defecto | Qué controla |
|---|---|---|
| `APP_BASE_URL` | `http://localhost:8080` | URL que se usa para armar los enlaces públicos |
| `PUBLIC_QUOTE_DAYS` | `30` | Días de validez de un enlace público |
| `REGISTRATION_ENABLED` | `true` | Permite crear cuentas nuevas |
| `DB_URL` | `jdbc:h2:file:./data/presupuestoya;DB_CLOSE_ON_EXIT=FALSE` | Conexión JDBC |
| `DB_USERNAME` | `sa` | Usuario de base de datos |
| `DB_PASSWORD` | vacío en H2 local | Contraseña de base de datos |
| `DB_DRIVER` | `org.h2.Driver` | Driver JDBC |
| `SESSION_COOKIE_SECURE` | `false` en local, `true` en prod | La cookie de sesión solo viaja por HTTPS |
| `PORT` | `8080` | Puerto HTTP |
| `FORWARD_HEADERS_STRATEGY` | `none` | Poné `native` detrás de un proxy confiable con HTTPS |
| `LOGIN_MAX_ATTEMPTS` | `5` | Intentos fallidos antes de bloquear una IP |
| `LOGIN_BLOCK_MINUTES` | `15` | Minutos de bloqueo |
| `SPRING_PROFILES_ACTIVE` | sin perfil | `dev` para desarrollo, `prod` en producción |

### Pasar a PostgreSQL

1. Creá la base y el usuario en PostgreSQL.
2. Configurá:

```bash
DB_URL=jdbc:postgresql://localhost:5432/presupuestoya
DB_USERNAME=presupuestoya
DB_PASSWORD=una-clave-larga-y-unica
DB_DRIVER=org.postgresql.Driver
```

3. Arrancá con el perfil `prod`.

Con `ddl-auto: update`, Hibernate crea/actualiza las tablas. Para un proyecto que va a crecer, reemplazá eso por **Flyway** o **Liquibase** y versioná los cambios de esquema.

### Producción mínima recomendada

- Un dominio propio y HTTPS (Caddy, Nginx, Traefik o el proxy del proveedor).
- `APP_BASE_URL=https://tu-dominio.com`
- `SESSION_COOKIE_SECURE=true`
- `FORWARD_HEADERS_STRATEGY=native`
- `REGISTRATION_ENABLED=false` si va a ser una beta cerrada.
- Base PostgreSQL con backups automáticos.
- Firewall: solo 80/443 expuestos; la base no debe estar abierta a Internet.
- Monitoreo del endpoint `/actuator/health`.

---

## 6. Cómo funciona cada parte

### Flujo de una petición

```
Navegador
  → filtros de Spring Security (CSRF, sesión, cabeceras, rate limit de login)
  → controlador (@Controller)
  → servicio (reglas de negocio y pertenencia)
  → repositorio (Spring Data JPA)
  → H2 / PostgreSQL
  → plantilla Thymeleaf
  → HTML
```

### Estructura de carpetas

```text
PresupuestoYa/
├─ pom.xml                     Dependencias y build Maven
├─ Dockerfile                  Build multi-etapa + runtime no root
├─ compose.yaml                Ejecución local con volumen persistente
├─ .env.example                Variables de entorno de ejemplo
├─ .gitignore / .dockerignore  Exclusión de datos, secretos y artefactos
├─ src/main/java/com/presupuestoya/
│  ├─ PresupuestoYaApplication.java   Punto de entrada
│  ├─ config/
│  │  ├─ AppProperties.java           Propiedades propias de la app
│  │  └─ SecurityConfig.java          Cadena de filtros, CSP, CSRF, login/logout
│  ├─ domain/
│  │  ├─ BaseEntity.java              id, createdAt, updatedAt
│  │  ├─ AppUser.java                 Cuenta de acceso
│  │  ├─ BusinessProfile.java         Datos del negocio por usuario
│  │  ├─ Client.java                  Cliente por usuario
│  │  ├─ Quote.java                   Presupuesto, totales, token público
│  │  ├─ QuoteItem.java               Ítem del presupuesto
│  │  └─ QuoteStatus.java             DRAFT/SENT/ACCEPTED/REJECTED
│  ├─ repository/                     Consultas JPA con filtro por dueño
│  ├─ security/
│  │  ├─ AppUserDetailsService.java   Carga el usuario para Spring Security
│  │  ├─ TokenService.java            Token aleatorio + hash SHA-256
│  │  ├─ LoginAttemptService.java     Límite de intentos en memoria
│  │  ├─ LoginRateLimitFilter.java    Corta POST /login bloqueados
│  │  ├─ LoginSuccessHandler.java     Limpia intentos y redirige a /app
│  │  └─ LoginFailureHandler.java     Registra el intento fallido
│  ├─ service/
│  │  ├─ CurrentUserService.java      Usuario autenticado actual
│  │  ├─ UserService.java             Registro y perfil inicial
│  │  ├─ BusinessProfileService.java  Lectura/actualización del negocio
│  │  ├─ ClientService.java           ABM de clientes con pertenencia
│  │  ├─ QuoteService.java            ABM, totales, estados y enlaces públicos
│  │  ├─ PublicQuoteView.java         DTO seguro para la vista pública
│  │  └─ DashboardStats.java          Resumen del panel
│  └─ web/
│     ├─ HomeController.java          Landing pública
│     ├─ AuthController.java          Login y registro
│     ├─ DashboardController.java     Panel
│     ├─ ClientController.java        Clientes
│     ├─ QuoteController.java         Presupuestos, estados y enlaces
│     ├─ SettingsController.java      Datos del negocio
│     ├─ PublicQuoteController.java   /q/{token}
│     ├─ CurrentUserAdvice.java       Expone currentUser a las plantillas
│     ├─ GlobalExceptionHandler.java  400/404 controlados
│     └─ form/                        Formularios + validaciones
├─ src/main/resources/
│  ├─ application.yml                 Configuración base
│  ├─ application-dev.yml             Plantillas sin caché
│  ├─ application-prod.yml            H2 en /app/data y cookie Secure
│  ├─ templates/                      Vistas Thymeleaf y fragmentos reutilizables
│  └─ static/                         CSS, JS, favicon y fuente Roboto local
└─ src/test/                          Tests de integración y seguridad
```

### Interfaz y sistema visual

La interfaz busca sentirse como una herramienta de oficio, no como una plantilla genérica:

- **Paleta**: azul vivo (`--blue`), azul profundo (`--blue-deep`) y tinta navy (`--ink`) sobre un fondo papel azulado.
- **Marca**: hexágono con la inicial “P” y un wordmark propio, reutilizado en la cabecera, el acceso y el pie.
- **Acceso**: layout partido a pantalla completa; panel azul con grilla, hexágonos y una vista previa de presupuesto; formulario blanco y limpio.
- **Landing**: hero con una composición de presupuesto, tarjetas flotantes de seguimiento y una tira de seguridad.
- **Paneles y formularios**: tarjetas de 20–28 px de radio, etiquetas en mayúsculas, inputs suaves y foco azul visible.
- **Tablas**: en escritorio son tablas densas; por debajo de 620 px se convierten en tarjetas apiladas con etiquetas. El editor de ítems hace lo mismo a 760 px para no exigir desplazamiento horizontal.
- **Impresión**: hoja A4 con encabezado, cliente, ítems, totales y notas; la barra de acciones se oculta al imprimir.

Los estilos viven en un único `static/css/app.css`. Los fragmentos `fragments/brand.html` y `fragments/auth-aside.html` concentran la marca y el panel de acceso para no duplicarlos.

### Registro y login

1. `AuthController` recibe el formulario `RegisterForm`.
2. Bean Validation exige nombre, email válido y contraseña de 12 a 72 caracteres.
3. `UserService` normaliza el email a minúsculas, verifica que no exista y cifra la contraseña con **BCrypt strength 12**.
4. Se crea un `BusinessProfile` inicial con los datos mínimos.
5. Spring Security autentica contra `AppUserDetailsService`.
6. `LoginSuccessHandler` limpia los intentos fallidos y redirige a `/app`.
7. `LoginFailureHandler` suma un intento fallido por IP.

La contraseña nunca se guarda en texto plano ni se muestra. No existe recuperación automática de contraseña: si un usuario la olvida, hay que definir un mecanismo administrativo o agregar recuperación por email.

### Panel

`DashboardController` pide a `QuoteService`:

- total de presupuestos,
- cantidad en borrador, enviados, aceptados y rechazados,
- suma de totales aceptados,
- últimos 5 presupuestos.

`DashboardStats` transporta esos números a la vista.

### Clientes

`ClientController` permite listar, crear, editar y borrar clientes.

- Cada cliente tiene un `owner` (`AppUser`).
- Las consultas usan `findByIdAndOwnerId`.
- Si un usuario intenta abrir el cliente de otro, recibe **404**, no un 403 que confirme que el recurso existe.
- No se puede borrar un cliente que ya tiene presupuestos.

### Presupuestos

`Quote` guarda:

- dueño, cliente, número, fechas de emisión y validez,
- estado y marcas de tiempo de envío/aceptación/rechazo,
- IVA, subtotal, impuesto y total,
- observaciones y condiciones,
- token público (solo el hash), fechas de creación/vencimiento,
- ítems ordenados.

`QuoteService`:

1. Verifica que el cliente pertenezca al usuario.
2. Genera un número único con formato `P-2026-XXXXXXXX`.
3. Convierte los ítems del formulario, ignora filas vacías y valida descripción, cantidad y precio.
4. Recalcula subtotal, IVA y total con `BigDecimal` y redondeo `HALF_UP`.
5. Solo permite editar o borrar presupuestos en **DRAFT**.
6. Aplica transiciones de estado:
   - `DRAFT → SENT`
   - `SENT → ACCEPTED`
   - `SENT → REJECTED`
   - Cualquier otro cambio se rechaza.

Un presupuesto enviado cuya fecha de validez ya pasó se muestra como **Vencido** sin cambiar el estado almacenado.

### Enlace público

1. El usuario autenticado presiona **Generar enlace público**.
2. `TokenService` genera 32 bytes aleatorios con `SecureRandom` y los codifica en Base64 URL-safe.
3. Se guarda **solo el hash SHA-256** del token en `Quote.publicTokenHash`.
4. El token crudo se devuelve una única vez en un flash attribute y se muestra en pantalla.
5. El enlace es `APP_BASE_URL + /q/ + token`.
6. `PublicQuoteController` busca por hash, verifica que no haya vencido y arma un `PublicQuoteView` con datos limitados.
7. Regenerar invalida el anterior; revocar borra el hash y el vencimiento.
8. La respuesta pública lleva `Cache-Control: no-store`, `Pragma: no-cache` y `X-Robots-Tag: noindex`.

La vista pública **no** muestra notas internas del cliente, ni el email de la cuenta, ni identificadores internos. Sí muestra los datos del negocio, del cliente, los ítems, totales, observaciones y condiciones del presupuesto, más un botón de WhatsApp si el negocio cargó teléfono.

### Imprimir / PDF

No hay librería de PDF en el servidor. La ruta `/app/quotes/{id}/print` renderiza una hoja con estilos de impresión y un botón que llama a `window.print()`. El navegador permite “Guardar como PDF”.

Ventajas: menos dependencias, menos superficie de ataque y el PDF conserva la tipografía y el estilo de la app. Limitación: el resultado depende del navegador y de la configuración de márgenes; para que salgan los fondos azules, activá “Gráficos de fondo” en el diálogo de impresión.

### Datos del negocio

`SettingsController` carga y actualiza `BusinessProfile`. Los datos se usan en las vistas internas, en la impresión y en el enlace público. La moneda se normaliza a mayúsculas y el IVA se redondea a dos decimales.

---

## 7. Modelo de datos

```text
AppUser 1 ─── 1 BusinessProfile
   │
   └── 1 ─── * Client
                 │
                 └── 1 ─── * Quote
                               │
                               └── 1 ─── * QuoteItem
```

| Entidad | Campos principales |
|---|---|
| `AppUser` | email único, hash de contraseña, nombre visible, habilitado |
| `BusinessProfile` | negocio, responsable, teléfono, email, dirección, CUIT/RUT/RFC, moneda, IVA y validez por defecto |
| `Client` | dueño, nombre, teléfono, email, dirección, notas internas |
| `Quote` | dueño, cliente, número único, estado, fechas, IVA, subtotal, impuesto, total, notas, condiciones, token público |
| `QuoteItem` | posición, descripción, cantidad, precio unitario |
| `QuoteStatus` | `DRAFT`, `SENT`, `ACCEPTED`, `REJECTED` |

Todas las entidades heredan `id`, `createdAt` y `updatedAt` de `BaseEntity`.

---

## 8. Seguridad implementada

### Autenticación y sesiones

- Contraseñas con **BCrypt strength 12**.
- Contraseña mínima de 12 caracteres y máxima de 72 (límite práctico de BCrypt).
- Sesión de 30 minutos.
- Cookie `PRESUPUESTOYA_SESSION` con `HttpOnly`, `SameSite=Lax` y `Secure` configurable.
- Renovación del ID de sesión al autenticarse (`changeSessionId`) para mitigar fijación de sesión.
- Logout por POST, con invalidación de sesión y borrado de cookie.
- Límite de intentos fallidos por IP: 5 intentos y 15 minutos por defecto.
- No hay login básico HTTP ni consola H2 expuesta.

### Autorización y aislamiento

- Todo `/app/**` requiere autenticación.
- Cada consulta de clientes y presupuestos incluye el `ownerId`.
- Los recursos de otro usuario devuelven 404 para no filtrar su existencia.
- El enlace público solo accede a un DTO con campos explícitos; no serializa la entidad completa.
- No se puede editar ni borrar un presupuesto que no esté en borrador.

### CSRF y formularios

- CSRF activado por defecto en Spring Security.
- Todos los formularios usan `th:action`, por lo que Thymeleaf inserta el token automáticamente.
- Los POST sin token reciben 403.
- Validación del lado del servidor con Bean Validation y reglas adicionales en los servicios.
- Los datos se muestran con `th:text`, que escapa HTML; no se usa `th:utext`.

### Cabeceras HTTP

`SecurityConfig` agrega:

- `Content-Security-Policy`: solo recursos propios, sin scripts ni estilos inline, sin frames.
- `X-Frame-Options: DENY`.
- `Referrer-Policy: no-referrer`.
- `Strict-Transport-Security` (HSTS); el navegador solo lo aplica cuando la app se sirve por HTTPS.
- `Permissions-Policy`: cámara, micrófono y geolocalización deshabilitados.

### Enlaces públicos

- Token de 32 bytes con `SecureRandom` (256 bits).
- Solo se almacena el hash SHA-256; un volcado de la base no permite reconstruir el enlace.
- Vencimiento por defecto a 30 días.
- Revocación y regeneración disponibles.
- La URL completa se muestra una sola vez.
- `no-store` y `noindex` en la respuesta pública.
- La página pública no requiere login, pero tampoco permite listar ni adivinar otros presupuestos.

### Errores y logs

- `server.error.include-stacktrace`, `include-message` y `include-binding-errors` están en `never`.
- Las páginas 400/404/403/500 no exponen detalles técnicos.
- No se registran contraseñas, tokens crudos ni cuerpos completos de formularios.
- Los logs incluyen número de presupuesto y `ownerId`, no datos personales innecesarios.

### Docker y secretos

- Contenedor final con usuario `app`, no root.
- Secretos por variables de entorno; `.env` está en `.gitignore`.
- `.dockerignore` excluye `data/`, `.env`, logs y artefactos.
- La base H2 vive en un volumen separado de la imagen.

### Límites conocidos de seguridad

Estas piezas **no** están implementadas y conviene tenerlas presentes antes de abrir la app al público:

- Verificación de email.
- Recuperación/cambio de contraseña autoservicio.
- Segundo factor (2FA).
- Roles de administrador o soporte.
- Equipos o múltiples usuarios por negocio.
- Auditoría persistente de accesos y cambios.
- Rate limiting distribuido (el actual es en memoria y se reinicia con la app).
- Protección contra fuerza bruta basada en email además de IP.
- WAF, reputación de IP o CAPTCHA.
- Cifrado de la base a nivel de disco (depende del proveedor).
- Borrado automático de datos por inactividad.
- Cumplimiento legal específico (privacidad, facturación electrónica, términos y condiciones).

Para una beta chica y controlada, la base es razonable. Para abrir el registro masivamente, sumá al menos verificación de email, recuperación de contraseña, backups automáticos y un plan de respuesta a incidentes.

---

## 9. Tests

```bash
mvn test
```

La suite actual (10 tests) verifica:

- Que las páginas públicas rendericen.
- Que las cabeceras de seguridad estén presentes.
- Que `/app` exija login.
- Que el registro exija CSRF y cree la cuenta.
- Que un usuario no pueda ver presupuestos ni clientes de otro.
- Que el enlace público funcione, tenga `no-store`/`noindex` y se revoque.
- Que panel, listado, detalle e impresión rendericen.
- Que los formularios de cliente, presupuesto y configuración rendericen.
- Que la configuración del negocio normalice la moneda a mayúsculas.
- Que se pueda crear un cliente y un presupuesto por POST, con decimales y totales correctos.

Para agregar tests, seguí el patrón de `PresupuestoYaApplicationTests`: `@SpringBootTest`, `@AutoConfigureMockMvc`, `@ActiveProfiles("test")` y `MockMvc`.

---

## 10. Backups y restauración

### H2 local

Con la aplicación detenida, copiá la carpeta `data/`. Es la forma más simple y segura de respaldar el archivo completo.

### Docker

El dato vive en el volumen `presupuestoya-data`. Un backup básico:

```bash
docker compose stop
docker run --rm \
  -v presupuestoya-data:/data \
  -v "$PWD":/backup \
  alpine tar czf /backup/presupuestoya-backup.tar.gz -C /data .
docker compose start
```

En Windows, ejecutá el comando desde Git Bash o adaptá la ruta del volumen. También podés usar la herramienta de backup de tu proveedor de volúmenes.

### Restauración

1. Detené la aplicación.
2. Reemplazá el contenido de `data/` o del volumen por el backup.
3. Arrancá de nuevo.
4. Verificá login, panel y un presupuesto.

### PostgreSQL

Usá `pg_dump`/`pg_restore` o los backups automáticos del proveedor. Programá una prueba de restauración, no solo el backup.

---

## 11. Personalización

### Cambiar colores y marca

- `src/main/resources/static/css/app.css`: variables `--blue`, `--blue-deep`, `--ink`, `--paper`, `--radius`, tipografía y espaciados.
- `src/main/resources/templates/fragments/brand.html`: isotipo y wordmark reutilizables.
- `src/main/resources/templates/fragments/header.html`: cabecera y navegación.
- `src/main/resources/templates/index.html`: landing y hero.
- `src/main/resources/templates/fragments/auth-aside.html`: panel azul de login y registro.
- `src/main/resources/static/favicon.svg`: ícono.

### Tipografía y licencia

La app usa **Roboto** como fuente variable, autoalojada en `src/main/resources/static/fonts/roboto-latin.woff2`. Un solo `@font-face` cubre los pesos de 100 a 900 y mantiene la CSP en `font-src 'self'`.

Roboto se distribuye bajo **Apache License 2.0**. El texto completo está en `src/main/resources/static/fonts/LICENSE-Roboto.txt`; conservalo si redistribuís el proyecto. Si cambiás de fuente, actualizá el `@font-face`, el `<link rel="preload">` de `fragments/head.html` y los archivos de `templates/quotes/print.html` y `public-quote.html`.

### Cambiar textos

Los textos están directamente en las plantillas, en español. Para internacionalizar, movelos a `messages.properties` y usá `#{...}`.

### Agregar un campo al presupuesto

1. Agregá el campo a `Quote` (entidad).
2. Agregalo a `QuoteForm` con sus validaciones.
3. Copialo en `QuoteService.applyForm` y en `toForm`.
4. Mostralo en `templates/quotes/form.html`, `detail.html` y `print.html`.
5. Si es público, agregalo a `PublicQuoteView` y a `public-quote.html`.
6. En producción, creá una migración (Flyway/Liquibase) en lugar de confiar en `ddl-auto: update`.

### Cambiar la numeración

`QuoteService.generateNumber()` usa `P-{año}-{8 caracteres}`. Podés cambiarlo por una secuencia por usuario o por año.

### Cambiar estados o transiciones

- `QuoteStatus`: etiquetas y clases CSS.
- `QuoteService.markStatus`: reglas de transición.
- `templates/quotes/detail.html`: botones disponibles.

### PDF en servidor

Hoy se usa impresión del navegador. Si necesitás PDFs idénticos y automatizados, agregá OpenPDF, iText o un servicio de renderizado HTML→PDF. Eso suma dependencias y superficie de ataque; mantenelo separado y validá el contenido.

### Email o WhatsApp automático

La app solo genera enlaces. Para enviar mensajes automáticos necesitás:

- email: Spring Mail + un proveedor SMTP;
- WhatsApp: WhatsApp Cloud API, con número verificado, plantillas aprobadas y consentimiento del destinatario.

No agregues envíos masivos sin opt-in: además de ser spam, puede violar las políticas del proveedor.

---

## 12. Problemas frecuentes

| Síntoma | Causa probable | Solución |
|---|---|---|
| `Port 8080 was already in use` | Otro proceso usa el puerto | Cambiá `PORT` o detené el proceso |
| `Database may be already in use` | Otra instancia de la app abrió el archivo H2 | Cerrá la otra instancia |
| 403 al guardar | Falta el token CSRF o la sesión expiró | Recargá la página y volvé a intentar |
| El login no “queda” | Cookie `Secure` sobre HTTP | `SESSION_COOKIE_SECURE=false` en local |
| El enlace público da 404 | Venció, fue revocado o se regeneró | Generá uno nuevo |
| El enlace apunta a `localhost` | `APP_BASE_URL` sin configurar | Poné tu dominio real |
| La impresión sale corrida | Márgenes del navegador | Ajustá márgenes y “Gráficos de fondo” |
| El contenedor no pasa el healthcheck | La app no arrancó o la base falló | `docker compose logs app` |
| Se perdieron los datos al recrear el contenedor | No se montó el volumen | Usá `-v presupuestoya-data:/app/data` |
| `mvn` no se reconoce | Maven no está en PATH | Instalá Maven o usá el wrapper si lo agregás |

---

## 13. Roadmap sugerido

1. Verificación de email y recuperación de contraseña.
2. Cambio de contraseña desde “Mi negocio”.
3. Numeración correlativa por usuario.
4. Adjuntos o fotos del trabajo.
5. Envío por email/WhatsApp con plantillas.
6. PDF del lado del servidor.
7. Roles y equipos.
8. Auditoría y panel de actividad.
9. Flyway + PostgreSQL como opción principal.
10. Suscripciones/pagos si el proyecto se comercializa.

---

## 14. Estado del proyecto

Proyecto funcional de punta a punta para uso personal, demo o beta cerrada. No está pensado todavía como SaaS multiusuario de alta escala: para eso hay que reforzar operaciones, backups, observabilidad, recuperación de cuentas y cumplimiento legal.

No hay una licencia definida en este repositorio. Si lo vas a publicar, agregá la licencia que corresponda.
