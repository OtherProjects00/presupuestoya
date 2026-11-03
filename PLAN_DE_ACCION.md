# Plan de acción para compartir PresupuestoYa y conseguir usuarios

El objetivo de este plan no es “tener una app publicada”, sino lograr que **personas reales la usen para enviar presupuestos y vuelvan a usarla**. La app ya está construida; ahora hay que validar que resuelva un dolor concreto y que el uso sea repetible.

---

## 1. Objetivo de los primeros 30 días

| Meta | Número sugerido |
|---|---|
| Personas contactadas | 60 |
| Demos o charlas de 15 minutos | 15 |
| Cuentas creadas | 20 |
| Usuarios que crean al menos 1 presupuesto | 10 |
| Usuarios que comparten al menos 1 enlace público | 5 |
| Usuarios que vuelven en la semana 2 | 5 |
| Testimonios recogidos | 3 |
| Primeros ingresos (opcional) | 3 clientes de pago |

No midas solo “registros”: un registro sin presupuesto no valida nada. La métrica principal es **primer presupuesto compartido**.

---

## 2. Propuesta de valor

**Frase corta:**

> “Armá un presupuesto profesional desde el celular en 3 minutos, mandalo por WhatsApp y enterate si te lo aceptaron.”

**Para quién:**

- Plomeros, electricistas, pintores, herreros, albañiles, jardineros, técnicos de aire acondicionado, cerrajeros, instaladores.
- Pequeños negocios de servicios que hoy presupuestan por WhatsApp, cuaderno o Excel.
- Personas que hacen 3 a 30 presupuestos por semana.

**Dolor que resuelve:**

- Pierden tiempo escribiendo presupuestos a mano.
- Se olvidan de cobrar ítems o calcular mal el total.
- No tienen un formato profesional.
- No saben si el cliente vio, aceptó o rechazó el presupuesto.
- Les da vergüenza mandar una foto de un papel.

**Qué NO prometer:**

- No es un sistema contable ni de facturación electrónica.
- No reemplaza un CRM grande.
- No hace magia con los precios: el usuario sigue decidiendo cuánto cobrar.

---

## 3. Preparación previa (días 1 a 3)

Antes de invitar gente, dejá la app lista para que la primera experiencia no falle.

### Producto

- [ ] Publicar la app con HTTPS y dominio propio.
- [ ] Configurar `APP_BASE_URL` con el dominio real.
- [ ] Configurar `SESSION_COOKIE_SECURE=true` y `FORWARD_HEADERS_STRATEGY=native`.
- [ ] Probar registro, login, cliente, presupuesto, enlace público, impresión y logout en celular y computadora.
- [ ] Crear una cuenta demo con datos de ejemplo.
- [ ] Revisar que los enlaces públicos no muestren notas internas.
- [ ] Configurar backups automáticos y probar una restauración.

### Materiales

- [ ] Video de 60 segundos mostrando: cliente → presupuesto → enlace → aceptación.
- [ ] PDF de una página con la propuesta y el link.
- [ ] Capturas de pantalla para WhatsApp.
- [ ] Mensaje de presentación y mensaje de seguimiento.
- [ ] Formulario simple de feedback (Google Forms o similar).
- [ ] Precio de beta: gratis durante 30 días, sin tarjeta.

### Operación

- [ ] Definir un canal de soporte (WhatsApp o email).
- [ ] Definir horario de respuesta (por ejemplo, 9 a 18).
- [ ] Crear una planilla con usuarios, fecha de alta, primer presupuesto y feedback.
- [ ] Escribir una política de privacidad básica y términos de uso.
- [ ] Definir qué pasa si alguien pide borrar su cuenta y sus datos.

---

## 4. Semana 1: conversaciones, no publicidad

La tentación es publicar en todos lados. Es mejor hablar con 20 personas y observar.

### Acciones

1. Hacé una lista de 30 conocidos o contactos de segundo grado que trabajen en oficios.
2. Escribiles de a uno, no en cadena.
3. Ofrecé una demo de 15 minutos o directamente crearles la cuenta y cargar su primer presupuesto con ellos.
4. Observá dónde se traban. No defiendas la app: tomá notas.
5. Al final de cada charla, pedí dos referidos.

### Guion de primer contacto (WhatsApp)

> Hola [Nombre], ¿cómo va? Estoy probando una herramienta para armar presupuestos de trabajos (plomería, electricidad, pintura, etc.) y mandarlos por WhatsApp con un link prolijo. No es venta: busco 5 personas que la prueben y me digan qué les falta. ¿Te interesa que te muestre en 10 minutos? Si no es para vos, ¿conocés a alguien que haga presupuestos seguido?

### Guion de demo (15 minutos)

1. **2 min — contexto:** “¿Cómo hacés los presupuestos hoy? ¿Qué es lo que más te molesta?”
2. **3 min — crear cliente:** cargar nombre y teléfono.
3. **5 min — crear presupuesto:** dos ítems, cantidad, precio, IVA.
4. **2 min — enviar:** marcar como enviado, generar link, abrir en incógnito.
5. **2 min — imprimir/PDF y estado aceptado.**
6. **1 min — feedback:** “¿Qué te faltaría para usarlo la semana que viene?”

No muestres todas las funciones. Mostrá el camino que resuelve su problema.

### Señales de interés real

- Pregunta cuánto sale.
- Pide cargar un trabajo real.
- Comparte el link con un cliente en la misma llamada.
- Pregunta si puede usarlo con un socio.
- Pide que le mandes el link para probarlo después.

### Señales de falsa validación

- “Está buenísimo” y nada más.
- Se ríe y cambia de tema.
- Pide funciones que no tiene antes de probar lo básico.
- No quiere cargar ni un cliente de prueba.

---

## 5. Semana 2: onboarding y primeras victorias

El objetivo es que cada usuario cree **un presupuesto real** y lo comparta.

### Onboarding asistido (15 minutos)

1. Crear la cuenta juntos.
2. Completar “Mi negocio” con sus datos reales.
3. Cargar un cliente real.
4. Cargar un trabajo real con 2 o 3 ítems.
5. Generar el enlace y mandarlo al cliente o a un familiar.
6. Guardar el PDF.
7. Acordar un seguimiento a los 7 días.

### Checklist de activación

- [ ] Cuenta creada.
- [ ] Perfil del negocio completo.
- [ ] Al menos un cliente.
- [ ] Al menos un presupuesto.
- [ ] Al menos un enlace público generado.
- [ ] Al menos una impresión/PDF.
- [ ] El usuario sabe cómo volver a entrar.

### Seguimiento a los 7 días

> Hola [Nombre], ¿pudiste usar PresupuestoYa para algún trabajo? Quiero saber si el link le sirvió al cliente o si algo te trabó. Con 2 minutos de feedback me ayudás un montón.

---

## 6. Semana 3: ampliar el alcance

Cuando ya tengas 5 usuarios usándola, ampliá con canales de bajo costo.

### Canales recomendados

| Canal | Cómo usarlo | Cuidado |
|---|---|---|
| WhatsApp directo | Mensajes personalizados a oficios conocidos | No hacer spam masivo |
| Referidos | “Si conocés a alguien, pasale mi contacto” | Recompensá con algo simple |
| Grupos de oficios | Aportar valor antes de promocionar | Leer reglas del grupo |
| Ferreterías y corralones | Dejar un QR con una demo | Pedir permiso al dueño |
| Instagram/TikTok | Video corto “cómo hago un presupuesto en 3 minutos” | Mostrar valor, no solo la app |
| Cursos/talleres | Ofrecer una charla de presupuestos | No vender en el primer minuto |
| Cámaras o asociaciones | Proponer una prueba para socios | Preparar propuesta formal |

### Publicación de lanzamiento (adaptar, no copiar y pegar en todos lados)

> Estoy abriendo 10 lugares para probar PresupuestoYa gratis durante 30 días. Es una app simple para armar presupuestos de trabajos y mandarlos por WhatsApp con un link profesional. La idea es que puedas presupuestar desde el celular sin Excel ni papel. Si trabajás en oficios o conocés a alguien, escribime y te muestro en 10 minutos. No pido tarjeta; sí pido que la uses con un trabajo real y me cuentes qué mejorar.

### QR para ferreterías

> “¿Hacés presupuestos? Probá PresupuestoYa gratis. Escaneá y creá tu primer presupuesto en 3 minutos.”

Poné el QR en un flyer chico, con el link corto y tu WhatsApp de soporte.

---

## 7. Semana 4: medir, iterar y decidir el precio

### Tablero mínimo

| Métrica | Cómo se calcula | Objetivo inicial |
|---|---|---|
| Activación | % de cuentas con al menos 1 presupuesto | > 50% |
| Time to first quote | Tiempo desde registro hasta primer presupuesto | < 24 h |
| Presupuestos por usuario/semana | Total / usuarios activos | > 1 |
| Tasa de compartido | % de presupuestos con enlace público | > 40% |
| Tasa de aceptación | Aceptados / enviados | > 30% |
| Retención semana 2 | Usuarios que vuelven | > 40% |
| Referidos | Usuarios que traen a otro | > 10% |
| NPS | Recomendación 0-10 | > 30 |

### Preguntas de feedback

1. ¿Qué estabas usando antes para presupuestar?
2. ¿Qué fue lo más rápido de la app?
3. ¿Qué te trabó o te hizo perder tiempo?
4. ¿Qué te faltó para usarla siempre?
5. ¿La recomendarías a un colega? ¿Por qué?
6. ¿Cuánto pagarías por mes si te ahorra 3 horas?
7. ¿Qué precio te parecería caro y cuál barato?

### Decisión de precio

No fijes un precio solo por intuición. Probá en la semana 4 con 5 usuarios:

- **Gratis:** beta de 30 días con límite de 3 presupuestos o sin límite.
- **Plan Solo:** uso individual, presupuestos ilimitados, enlaces públicos, PDF.
- **Plan Pro:** más adelante, con equipo, plantillas o recordatorios.

Para Argentina, definí el precio en pesos y revisalo todos los meses por inflación. Una referencia de trabajo: si la app ahorra 2 o 3 horas por semana y un presupuesto promedio vale varios miles de pesos, un precio de **USD 4 a 10 por mes** (o su equivalente en ARS) es razonable para probar. Validalo con los usuarios; no lo uses como verdad absoluta.

### Prueba de precio

- A 5 usuarios: ofrecer 30 días gratis y después un precio fundador con descuento permanente.
- A otros 5: ofrecer el precio normal.
- Medir conversión y objeciones.
- No cambies el precio todas las semanas: confunde.

---

## 8. Guiones de venta y objeciones

### Llamada de 30 segundos

> “Hola [Nombre], ¿cómo hacés los presupuestos hoy? … Te cuento: armé una app para que cargues el trabajo, los materiales y la mano de obra, y le mandes al cliente un link con el total y la validez. El cliente no necesita descargar nada. ¿Querés que te muestre con un trabajo real tuyo?”

### Cierre de beta

> “Te dejo la cuenta creada y cargamos tu primer presupuesto ahora. Si en 30 días no te sirve, no pagás nada. Lo único que te pido es que me cuentes qué te falta.”

### Objeciones

**“Ya uso WhatsApp/Excel.”**

> “Perfecto, no te pido que lo dejes. Probá hacer el próximo presupuesto con la app y compará cuál te lleva menos tiempo y cuál se ve más profesional. Si Excel te sirve más, me lo decís y no pasa nada.”

**“No tengo tiempo.”**

> “Justamente. La primera vez te lleva 10 minutos conmigo; después son 2 o 3 minutos. Si querés, lo hacemos ahora con un trabajo que tengas pendiente.”

**“¿Es gratis?”**

> “La beta sí, 30 días. Después voy a cobrar un plan simple. Antes de cobrarte quiero que la uses y que me digas si vale la pena.”

**“¿Mis datos están seguros?”**

> “La contraseña se guarda cifrada, cada cuenta ve solo sus datos y los links públicos vencen y se pueden revocar. No vendo datos ni los comparto. Igual, no cargues información que no necesites.”

**“¿Y si el cliente no tiene internet?”**

> “Le podés mandar el PDF o imprimirlo. El link es una comodidad, no una obligación.”

**“¿Sirve para mi oficio?”**

> “Si hacés presupuestos con ítems, cantidades y precios, sí. Si tu trabajo se cobra de otra forma, contame y vemos si encaja.”

**“¿Puedo usarlo con mi socio?”**

> “Hoy cada cuenta es personal. Si lo necesitás, anotámelo: es una de las cosas que quiero agregar.”

---

## 9. Seguridad operativa durante la beta

La seguridad no termina en el código. Antes de invitar usuarios:

### Infraestructura

- [ ] HTTPS obligatorio; redirigir HTTP a HTTPS.
- [ ] Contraseñas y secretos solo en variables de entorno, nunca en el repositorio.
- [ ] `SESSION_COOKIE_SECURE=true`.
- [ ] Firewall: solo 80/443 públicos.
- [ ] Base de datos no expuesta a Internet.
- [ ] Acceso SSH con clave, no con contraseña.
- [ ] Actualizaciones de seguridad del sistema operativo.
- [ ] Backups automáticos y prueba de restauración.

### Aplicación

- [ ] Registro abierto solo si estás preparado para soporte; si no, `REGISTRATION_ENABLED=false` e invitaciones.
- [ ] Revisar logs sin datos sensibles.
- [ ] Probar enlaces vencidos y revocados.
- [ ] Probar que un usuario no pueda ver datos de otro.
- [ ] Revisar dependencias con `mvn versions:display-dependency-updates` o Dependabot.
- [ ] Definir qué hacer ante un incidente: quién responde, cómo se avisa, cómo se revocan sesiones/tokens.

### Datos y privacidad

- [ ] Publicar una política de privacidad simple: qué datos se guardan, para qué, dónde y cómo se borran.
- [ ] Pedir solo datos necesarios.
- [ ] Permitir que un usuario pida exportación o borrado de su cuenta.
- [ ] No usar datos de clientes para marketing.
- [ ] Definir plazo de retención de backups.
- [ ] En Argentina, tener presente la Ley 25.326 de Protección de Datos Personales y buscar asesoramiento si la app crece. Este documento no es asesoramiento legal.

### Incidentes

Si sospechás una brecha:

1. Contené: revocá tokens, cambiá contraseñas de infraestructura, aislá el servicio.
2. Evaluá: qué datos, cuántos usuarios, desde cuándo.
3. Notificá: a los afectados y, si corresponde, a la autoridad.
4. Corregí: parche, pruebas, monitoreo.
5. Documentá: qué pasó, qué hiciste y cómo evitarlo.

---

## 10. Calendario de 30 días

| Día | Foco | Entregable |
|---|---|---|
| 1 | Producto | App publicada con HTTPS y backups |
| 2 | Materiales | Video, PDF, capturas y formulario |
| 3 | Lista | 30 contactos de oficios |
| 4-7 | Conversaciones | 20 contactos, 5 demos |
| 8-10 | Onboarding | 5 usuarios con primer presupuesto |
| 11-14 | Iteración | Corregir las 3 trabas más repetidas |
| 15-17 | Testimonios | 3 quotes/capturas autorizadas |
| 18-21 | Lanzamiento | Publicaciones y referidos |
| 22-24 | Seguimiento | Contactar a quienes no activaron |
| 25-27 | Precio | Entrevistas de precio y prueba |
| 28-30 | Balance | Métricas, decisiones y próximo ciclo |

---

## 11. Qué hacer después de los 30 días

### Si hay tracción

- Duplicar el canal que mejor funcionó.
- Agregar recuperación de contraseña y verificación de email.
- Mejorar onboarding con plantillas de presupuesto.
- Ofrecer plan pago y soporte.
- Medir churn y motivos de cancelación.

### Si no hay tracción

No agregues funciones a ciegas. Revisá:

- ¿El problema es doloroso y frecuente?
- ¿Las personas adecuadas están probando?
- ¿La activación falla por la app o por falta de tiempo?
- ¿El precio es el problema o la propuesta de valor?
- ¿El canal es el correcto?

A veces el problema no es el producto sino el segmento. Un plomero que hace 2 presupuestos por mes no necesita la app; uno que hace 10 por semana, sí.

---

## 12. Checklist final antes de compartir

- [ ] La app abre en HTTPS y no muestra errores.
- [ ] El registro funciona en celular.
- [ ] El login y logout funcionan.
- [ ] Se puede crear cliente, presupuesto y enlace público.
- [ ] El enlace se ve bien en el celular.
- [ ] El PDF sale legible.
- [ ] Los datos de otro usuario no son accesibles.
- [ ] Los enlaces vencen y se revocan.
- [ ] Hay backups y se probó restaurar.
- [ ] Hay un canal de soporte claro.
- [ ] Hay una política de privacidad y términos básicos.
- [ ] Sabés qué métrica vas a mirar y con qué objetivo.
- [ ] Tenés claro qué NO prometés.

---

## 13. Resumen en una frase

Publicá con HTTPS, invitá de a una persona, hacé que cargue un presupuesto real en la primera sesión, escuchá dónde se traba, corregí esa fricción y recién después ampliá el alcance.
