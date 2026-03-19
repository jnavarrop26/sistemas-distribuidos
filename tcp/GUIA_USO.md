# 📖 Guía de Uso Rápida - Sistema Cliente-Servidor TCP

## 🎯 Inicio Rápido

### Paso 1: Compilar el Proyecto

**Opción A - Windows (usando script):**
```bash
compilar.bat
```

**Opción B - Línea de comandos:**
```bash
javac -d bin src/comun/*.java src/servidor/*.java src/cliente/*.java
```

### Paso 2: Ejecutar el Servidor

**Opción A - Windows (usando script):**
```bash
ejecutar-servidor.bat
```

**Opción B - Línea de comandos:**
```bash
java -cp bin servidor.ServidorPrincipal
```

**Al ejecutar verás:**
```
╔════════════════════════════════════════════════╗
║        SERVIDOR TCP - INTERFAZ PRINCIPAL       ║
╚════════════════════════════════════════════════╝

Ingrese el puerto [default: 8080]:
```

**Configuración recomendada para empezar:**
1. Presiona ENTER para usar puerto 8080
2. Escribe `s` para habilitar reinicio automático
3. Presiona ENTER para tiempo de reinicio por defecto (2000ms)
4. En el menú, selecciona opción `1` para **Iniciar servidor**

### Paso 3: Ejecutar el Cliente

**En OTRA terminal/ventana:**

**Opción A - Windows (usando script):**
```bash
ejecutar-cliente.bat
```

**Opción B - Línea de comandos:**
```bash
java -cp bin cliente.ClientePrincipal
```

**Configuración recomendada para empezar:**
1. Presiona ENTER para usar nombre "MiCliente"
2. Presiona ENTER para usar host "localhost"
3. Presiona ENTER para usar puerto 8080
4. Escribe `n` para no configurar políticas avanzadas
5. En el menú, selecciona opción `1` para **Conectar al servidor**

---

## 📋 Menú del Servidor

```
╔════════════════════════════════════════════════╗
║                  MENÚ PRINCIPAL                ║
╠════════════════════════════════════════════════╣
║  1. Iniciar servidor                           ║
║  2. Detener servidor                           ║
║  3. Reiniciar servidor                         ║
║  4. Ver estado del servidor                    ║
║  5. Habilitar/Deshabilitar reinicio automático ║
║  6. Apagar servidor completamente              ║
║  7. Salir                                      ║
╚════════════════════════════════════════════════╝
```

### Opciones del Servidor:

- **Opción 1**: Inicia el servidor en el puerto configurado
- **Opción 2**: Detiene el servidor pero mantiene el monitor activo
- **Opción 3**: Reinicia el servidor (útil para aplicar cambios)
- **Opción 4**: Muestra información del servidor (clientes conectados, estado, etc.)
- **Opción 5**: Activa/desactiva el reinicio automático en caso de caída
- **Opción 6**: Apaga completamente el servidor y el monitor
- **Opción 7**: Sale del programa

---

## 📋 Menú del Cliente

```
╔════════════════════════════════════════════════╗
║                  MENÚ PRINCIPAL                ║
╠════════════════════════════════════════════════╣
║  1. Conectar al servidor                       ║
║  2. Enviar mensaje                             ║
║  3. Modo interactivo (chat)                    ║
║  4. Ver estado de conexión                     ║
║  5. Desconectar                                ║
║  6. Salir                                      ║
╚════════════════════════════════════════════════╝
```

### Opciones del Cliente:

- **Opción 1**: Conecta al servidor con políticas de reintentos automáticos
- **Opción 2**: Envía un mensaje individual al servidor
- **Opción 3**: Activa modo chat para enviar múltiples mensajes (escribe "salir" para volver al menú)
- **Opción 4**: Muestra el estado de la conexión actual
- **Opción 5**: Desconecta del servidor
- **Opción 6**: Sale del programa

---

## 💬 Ejemplo de Uso Completo

### Terminal 1 - Servidor:
```
Ingrese el puerto [default: 8080]: [ENTER]
¿Habilitar reinicio automático? (s/n) [default: s]: s
Tiempo de espera antes de reiniciar en ms [default: 2000]: [ENTER]

✓ Servidor configurado correctamente
  Puerto: 8080
  Reinicio automático: SÍ

Seleccione una opción: 1

╔══════════════════════════════════════════╗
║      SERVIDOR INICIADO                   ║
╠══════════════════════════════════════════╣
║  Puerto: 8080                            ║
║  Reinicio automático: SÍ                 ║
╚══════════════════════════════════════════╝

[SERVIDOR] Nuevo cliente aceptado. Total de clientes: 1
```

### Terminal 2 - Cliente:
```
Ingrese el nombre del cliente [default: MiCliente]: Cliente1
Ingrese el host del servidor [default: localhost]: [ENTER]
Ingrese el puerto [default: 8080]: [ENTER]
¿Configurar políticas avanzadas? (s/n) [default: n]: n

✓ Cliente configurado correctamente

Seleccione una opción: 1

╔══════════════════════════════════════════╗
║      CLIENTE: Cliente1                   ║
╠══════════════════════════════════════════╣
║  Servidor: localhost:8080                ║
║  Max reintentos: 5                       ║
║  Timeout: 5000ms                         ║
╚══════════════════════════════════════════╝

[CLIENTE Cliente1] ✓ Conectado exitosamente

Seleccione una opción: 3

══════════════════════════════════════
  MODO INTERACTIVO (CHAT)
══════════════════════════════════════
Escriba 'salir' para volver al menú

Cliente1> Hola servidor!
[CLIENTE Cliente1 → SERVIDOR] Hola servidor!
Cliente1> ¿Cómo estás?
[CLIENTE Cliente1 → SERVIDOR] ¿Cómo estás?
Cliente1> salir
```

---

## 🧪 Probar Políticas de Reconexión

### Escenario: Servidor se cae y se reinicia automáticamente

1. **Terminal 1 (Servidor)**: Inicia el servidor con reinicio automático
2. **Terminal 2 (Cliente)**: Conecta un cliente
3. **Terminal 1 (Servidor)**: Selecciona opción `2` (Detener servidor)
4. **Observa**: El monitor detectará que el servidor cayó
5. **Observa**: El servidor se reiniciará automáticamente después de 2 segundos
6. **Terminal 2 (Cliente)**: El cliente reintentará conectarse y se reconectará exitosamente

### Escenario: Política de Reintentos

1. **NO inicies el servidor**
2. **Terminal 1 (Cliente)**: Intenta conectar al servidor
3. **Observa**: El cliente intentará 5 veces (por defecto) con 3 segundos entre intentos
4. **Observa**: Después de 5 intentos fallidos, el cliente reportará timeout

---

## ⚙️ Configuración Avanzada

### Políticas del Cliente:

Si seleccionas configurar políticas avanzadas al iniciar el cliente:

- **Máximo de reintentos**: Cuántas veces intentará conectarse (default: 5)
- **Tiempo entre reintentos**: Milisegundos entre cada intento (default: 3000)
- **Timeout de conexión**: Tiempo máximo para establecer conexión (default: 5000)
- **Timeout de lectura**: Tiempo máximo esperando respuesta del servidor (default: 10000)

### Políticas del Servidor:

Al iniciar el servidor puedes configurar:

- **Puerto**: Puerto TCP donde escuchará (default: 8080)
- **Reinicio automático**: Si el servidor debe reiniciarse automáticamente al caer (default: sí)
- **Tiempo antes de reiniciar**: Milisegundos de espera antes del reinicio (default: 2000)

---

## ❓ Solución de Problemas

### Error: "Address already in use"
- **Causa**: El puerto 8080 ya está siendo usado
- **Solución**: Cierra el servidor anterior o usa otro puerto

### Cliente no se conecta
- **Verifica**: Que el servidor esté iniciado (opción 4 del servidor)
- **Verifica**: Puerto y host correctos en el cliente
- **Verifica**: Firewall no esté bloqueando la conexión

### Servidor no reinicia automáticamente
- **Verifica**: Que el reinicio automático esté habilitado (opción 4 del servidor)
- **Verifica**: Que usaste "Detener" y no "Apagar completamente"
- **Nota**: Si usas opción 6 (Apagar), el monitor también se detiene

---

## 🎓 Conceptos Demostrados

✅ **Conexiones TCP**: Cliente-Servidor usando Sockets
✅ **Política de Reintentos**: El cliente intenta múltiples veces antes de rendirse
✅ **Política de Reconexión**: El cliente se reconecta automáticamente si pierde conexión
✅ **Política de Reinicio**: El servidor se reinicia automáticamente si se cae
✅ **Timeouts**: Evita esperas indefinidas en operaciones de red
✅ **Multithreading**: Múltiples clientes simultáneos, monitores en segundo plano
✅ **Gestión de Recursos**: Cierre correcto de sockets y streams

---

**¿Necesitas ayuda?** Consulta el `README.md` para información técnica detallada.
