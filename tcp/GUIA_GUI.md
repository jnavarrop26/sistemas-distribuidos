# 🖥️ Guía de Interfaces Gráficas (Java Swing)

## 📌 Introducción

El sistema Cliente-Servidor TCP incluye interfaces gráficas intuitivas desarrolladas con Java Swing que facilitan el uso del sistema sin necesidad de comandos de consola.

---

## 🚀 Inicio Rápido

### Paso 1: Compilar el Proyecto

```bash
compilar.bat
```

### Paso 2: Ejecutar Servidor GUI

**Opción A - Script (Recomendado):**
```bash
servidor-gui.bat
```

**Opción B - Línea de comandos:**
```bash
java -cp bin servidor.ServidorGUI
```

### Paso 3: Ejecutar Cliente GUI

**En otra ventana:**

**Opción A - Script (Recomendado):**
```bash
cliente-gui.bat
```

**Opción B - Línea de comandos:**
```bash
java -cp bin cliente.ClienteGUI
```

---

## 🖥️ Servidor GUI - Manual de Uso

### Interfaz del Servidor

La interfaz del servidor está dividida en 4 secciones principales:

#### 1️⃣ **Panel de Configuración** (Superior)

Aquí configuras los parámetros del servidor antes de iniciarlo:

- **Puerto**: Puerto TCP donde el servidor escuchará (default: 8080)
  - Rango válido: 1024-65535
  - Recomendado: 8080, 9090, 3000

- **Reinicio Automático**: Checkbox para habilitar/deshabilitar
  - ✅ Habilitado: El servidor se reiniciará automáticamente si se cae
  - ❌ Deshabilitado: El servidor quedará detenido si se cae

- **Tiempo reinicio (ms)**: Milisegundos de espera antes de reiniciar
  - Default: 2000ms (2 segundos)
  - Rango: 500-10000ms

#### 2️⃣ **Registro de Eventos** (Centro)

Área de texto con fondo negro y texto verde que muestra:
- Eventos de inicio/detención del servidor
- Conexiones de nuevos clientes
- Desconexiones de clientes
- Errores y advertencias
- Mensajes del sistema

**Características:**
- Auto-scroll hacia el último mensaje
- Timestamps automáticos [HH:mm:ss]
- Se puede limpiar con el botón "🗑 Limpiar Log"

#### 3️⃣ **Estado del Servidor** (Derecha)

Panel que muestra información en tiempo real:

- **Estado**:
  - 🟢 EJECUTANDO (verde) - Servidor activo
  - 🔴 DETENIDO (rojo) - Servidor inactivo

- **Puerto**: Puerto actual en uso (o N/A si está detenido)

- **Clientes**: Número de clientes conectados actualmente
  - Se actualiza automáticamente cada segundo

- **Auto-reinicio**:
  - 🟢 SÍ (verde) - Reinicio automático habilitado
  - 🔴 NO (rojo) - Reinicio automático deshabilitado

#### 4️⃣ **Controles** (Inferior)

Botones para controlar el servidor:

- **▶ Iniciar Servidor** (Verde):
  - Inicia el servidor con la configuración actual
  - Se deshabilita cuando el servidor está ejecutando

- **⏹ Detener Servidor** (Rojo):
  - Detiene el servidor pero mantiene el monitor activo
  - Solo disponible cuando el servidor está ejecutando

- **🔄 Reiniciar** (Naranja):
  - Reinicia el servidor manualmente
  - Solo disponible cuando el servidor está ejecutando

- **🗑 Limpiar Log** (Gris):
  - Limpia el área de registro de eventos
  - Disponible siempre

### Flujo de Trabajo Típico - Servidor

1. **Configurar**:
   - Ingresa el puerto (ej: 8080)
   - Marca "Reinicio Automático" si lo deseas
   - Ajusta el tiempo de reinicio si es necesario

2. **Iniciar**:
   - Click en "▶ Iniciar Servidor"
   - Verás mensaje de confirmación en el log
   - El estado cambiará a "EJECUTANDO" en verde

3. **Monitorear**:
   - Observa las conexiones de clientes en el log
   - Verifica el número de clientes en el panel de estado

4. **Detener** (cuando termines):
   - Click en "⏹ Detener Servidor"
   - Todos los clientes se desconectarán

---

## 💻 Cliente GUI - Manual de Uso

### Interfaz del Cliente

La interfaz del cliente está dividida en 4 secciones principales:

#### 1️⃣ **Panel de Configuración** (Superior)

Configura los parámetros de conexión:

- **Nombre**: Identificador del cliente (ej: "Cliente1", "Juan", "PC-Oficina")
  - Aparecerá en los mensajes del servidor

- **Host**: Dirección del servidor
  - "localhost" - Para servidor en la misma máquina
  - IP local - Para servidor en red local (ej: 192.168.1.100)
  - Dominio - Para servidor remoto

- **Puerto**: Puerto del servidor (debe coincidir con el puerto del servidor)
  - Default: 8080

- **Reconexión Automática**: Checkbox
  - ✅ Habilitado: Se reconectará automáticamente si pierde conexión
  - ❌ Deshabilitado: No intentará reconectarse

- **Max Reintentos**: Número máximo de intentos de conexión
  - Default: 5
  - Rango: 1-20

- **Botones de Conexión**:
  - 🔌 **Conectar** (Verde): Inicia la conexión al servidor
  - ❌ **Desconectar** (Rojo): Cierra la conexión

#### 2️⃣ **Panel de Chat** (Centro Superior)

Área para ver y enviar mensajes:

**Área de Mensajes**:
- Muestra todos los mensajes enviados y recibidos
- Formato: `[HH:mm:ss] EMISOR: mensaje`
- Auto-scroll hacia el último mensaje
- Puede limpiarse con el botón 🗑

**Campo de Envío**:
- Campo de texto para escribir mensajes
- Solo habilitado cuando estás conectado
- Presiona Enter o click en "📤 Enviar"

**Botones**:
- 📤 **Enviar**: Envía el mensaje al servidor
- 🗑: Limpia el área de chat

#### 3️⃣ **Registro de Eventos** (Centro Inferior)

Área de log técnico con fondo negro y texto verde:
- Eventos de conexión/desconexión
- Intentos de reconexión
- Errores y advertencias
- Mensajes del sistema
- Timestamps automáticos

#### 4️⃣ **Estado de Conexión** (Derecha)

Panel visual con información en tiempo real:

**Indicador Visual**:
- 🔴 Círculo rojo - Desconectado
- 🟢 Círculo verde - Conectado

**Información**:
- **Estado**:
  - 🔴 DESCONECTADO (rojo)
  - 🟢 CONECTADO (verde)

- **Servidor**: Host:Puerto del servidor (o N/A)

- **Reconexión**:
  - 🟢 HABILITADA (verde)
  - 🔴 DESHABILITADA (rojo)

### Flujo de Trabajo Típico - Cliente

1. **Configurar Conexión**:
   - Ingresa tu nombre (ej: "Cliente1")
   - Ingresa host del servidor (ej: "localhost")
   - Ingresa puerto (ej: 8080)
   - Marca "Reconexión Automática" si lo deseas
   - Ajusta reintentos si es necesario

2. **Conectar**:
   - Click en "🔌 Conectar"
   - Observa el log - verás los intentos de conexión
   - Si conecta: indicador cambiará a verde
   - Si falla: verás los reintentos en el log

3. **Chatear**:
   - Escribe mensaje en el campo inferior
   - Presiona Enter o click "📤 Enviar"
   - Verás tu mensaje en el área de chat

4. **Desconectar** (cuando termines):
   - Click en "❌ Desconectar"
   - El indicador volverá a rojo

---

## 🎯 Escenarios de Uso

### Escenario 1: Comunicación Básica

**Objetivo**: Establecer comunicación entre servidor y cliente

1. **Servidor**:
   - Ejecuta `servidor-gui.bat`
   - Puerto: 8080
   - Click "▶ Iniciar Servidor"

2. **Cliente**:
   - Ejecuta `cliente-gui.bat`
   - Host: localhost, Puerto: 8080
   - Click "🔌 Conectar"
   - Envía mensajes

**Resultado esperado**:
- ✅ Cliente conecta exitosamente
- ✅ Servidor muestra nuevo cliente en el log
- ✅ Mensajes se envían correctamente

### Escenario 2: Múltiples Clientes

**Objetivo**: Conectar varios clientes al mismo servidor

1. **Servidor**:
   - Ejecuta `servidor-gui.bat`
   - Inicia servidor en puerto 8080

2. **Clientes** (ejecuta `cliente-gui.bat` múltiples veces):
   - Cliente 1: Nombre "Cliente1", conecta
   - Cliente 2: Nombre "Cliente2", conecta
   - Cliente 3: Nombre "Cliente3", conecta

3. **Observa**:
   - El servidor muestra "Clientes: 3"
   - Cada conexión aparece en el log del servidor

**Resultado esperado**:
- ✅ Todos los clientes conectan
- ✅ Contador de clientes se actualiza
- ✅ Cada cliente puede enviar mensajes

### Escenario 3: Reinicio Automático del Servidor

**Objetivo**: Demostrar la política de reinicio automático

1. **Servidor**:
   - Ejecuta `servidor-gui.bat`
   - ✅ Marca "Reinicio Automático"
   - Tiempo reinicio: 2000ms
   - Click "▶ Iniciar Servidor"

2. **Cliente**:
   - Ejecuta `cliente-gui.bat`
   - ✅ Marca "Reconexión Automática"
   - Max reintentos: 10
   - Conecta al servidor

3. **Simular Caída**:
   - En el servidor, click "⏹ Detener Servidor"

4. **Observa**:
   - Servidor se detiene
   - Monitor detecta la caída
   - Después de 2 segundos, servidor se reinicia automáticamente
   - Cliente intenta reconectarse
   - Cliente se reconecta exitosamente

**Resultado esperado**:
- ✅ Servidor se reinicia automáticamente
- ✅ Cliente se reconecta automáticamente
- ✅ Conexión se restablece sin intervención manual

### Escenario 4: Política de Reintentos

**Objetivo**: Demostrar reintentos cuando no hay servidor

1. **NO inicies el servidor**

2. **Cliente**:
   - Ejecuta `cliente-gui.bat`
   - Host: localhost, Puerto: 8080
   - Max reintentos: 3
   - Click "🔌 Conectar"

3. **Observa en el log**:
   - Intento #1 - Falla
   - Espera 3 segundos
   - Intento #2 - Falla
   - Espera 3 segundos
   - Intento #3 - Falla
   - Mensaje de error final

**Resultado esperado**:
- ✅ Cliente intenta 3 veces
- ✅ Espera entre cada intento
- ✅ Muestra error después del último intento

---

## 🎨 Características Visuales

### Servidor GUI

- **Tema**: Profesional con paneles separados
- **Colores de Estado**:
  - Verde: Ejecutando, Habilitado
  - Rojo: Detenido, Deshabilitado
  - Naranja: Advertencias
- **Log**: Fondo negro con texto verde (estilo terminal)
- **Bordes**: Títulos claros en cada sección

### Cliente GUI

- **Indicador Visual**: Círculo grande que cambia de rojo a verde
- **Panel de Chat**: Blanco con texto negro legible
- **Log Técnico**: Fondo negro con texto verde
- **Botones Coloridos**:
  - Verde: Conectar
  - Rojo: Desconectar
  - Azul: Enviar

---

## ⚙️ Configuraciones Recomendadas

### Para Desarrollo Local

**Servidor**:
- Puerto: 8080
- Reinicio automático: ✅ SÍ
- Tiempo reinicio: 2000ms

**Cliente**:
- Host: localhost
- Puerto: 8080
- Reconexión: ✅ SÍ
- Max reintentos: 5

### Para Red Local

**Servidor**:
- Puerto: 8080 (o cualquier puerto libre)
- Reinicio automático: ✅ SÍ
- Encontrar IP local: `ipconfig` (Windows) o `ifconfig` (Linux/Mac)

**Cliente**:
- Host: IP del servidor (ej: 192.168.1.100)
- Puerto: 8080 (mismo que el servidor)
- Reconexión: ✅ SÍ
- Max reintentos: 10 (por si hay latencia de red)

---

## ❓ Solución de Problemas

### "Address already in use"

**Problema**: El puerto ya está siendo usado por otro programa

**Solución**:
1. Cierra cualquier servidor anterior
2. Cambia a otro puerto (ej: 9090, 3000, 8888)
3. Verifica con `netstat -an | findstr :8080`

### Cliente no conecta

**Verificar**:
1. ✅ Servidor está iniciado (estado: EJECUTANDO en verde)
2. ✅ Puerto correcto en ambos lados
3. ✅ Host correcto (localhost para local, IP para red)
4. ✅ Firewall no está bloqueando la conexión

**Observar el log**:
- Si dice "Connection refused": Servidor no está escuchando
- Si dice "Timeout": Problema de red o firewall

### Servidor no reinicia automáticamente

**Verificar**:
1. ✅ "Reinicio Automático" está marcado antes de iniciar
2. ✅ Usaste "Detener Servidor" y no cerraste la ventana
3. ✅ Observa el log - debe decir "Reiniciando servidor"

### Mensajes no se envían

**Verificar**:
1. ✅ Estado: CONECTADO (verde)
2. ✅ Campo de mensaje está habilitado
3. ✅ Servidor sigue ejecutándose

---

## 🎓 Ventajas de la Interfaz Gráfica

✅ **Facilidad de Uso**: No requiere conocimientos de comandos
✅ **Visual**: Estado claro con colores e indicadores
✅ **Tiempo Real**: Actualización automática de estados
✅ **Multitarea**: Múltiples ventanas de clientes fácilmente
✅ **Logs Integrados**: Toda la información en un solo lugar
✅ **Configuración Simple**: Formularios intuitivos

---

## 📚 Recursos Adicionales

- **README.md**: Documentación técnica completa
- **GUIA_USO.md**: Guía de interfaces de consola
- Código fuente:
  - `src/servidor/ServidorGUI.java`
  - `src/cliente/ClienteGUI.java`

---

**¿Prefieres la interfaz de consola?** Lee `GUIA_USO.md` para instrucciones de las interfaces de texto.
