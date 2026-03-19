# Sistema Cliente-Servidor TCP con Políticas de Reinicio y Reconexión

Sistema completo de comunicación TCP con políticas avanzadas de gestión de fallos, reintentos, reconexión y reinicio automático.

## 📋 Características

### Servidor
- ✅ **Política de Reinicio Automático**: El servidor puede reiniciarse automáticamente cuando se cae
- ✅ **Monitor de Servidor**: Supervisa el estado del servidor y ejecuta reinicios cuando es necesario
- ✅ **Gestión de Múltiples Clientes**: Maneja conexiones concurrentes de varios clientes
- ✅ **Configuración Flexible**: Políticas configurables según necesidades

### Cliente
- ✅ **Política de Reconexión**: Reconexión automática cuando se pierde la conexión
- ✅ **Política de Reintentos**: Reintentos configurables con límite máximo
- ✅ **Timeouts Configurables**: Timeouts para conexión, lectura y escritura
- ✅ **Detección de Desconexión**: Detección automática de pérdida de conexión

## 🏗️ Arquitectura

```
tcp/
├── src/
│   ├── comun/                          # Clases compartidas
│   │   ├── EstadoConexion.java        # Enum de estados de conexión
│   │   └── ConfiguracionPoliticas.java # Configuración general
│   │
│   ├── servidor/                       # Paquete del servidor
│   │   ├── Servidor.java              # Clase principal del servidor
│   │   ├── PoliticaReinicio.java      # Política de reinicio automático
│   │   ├── MonitorServidor.java       # Monitor que supervisa el servidor
│   │   └── ManejadorCliente.java      # Maneja cada cliente conectado
│   │
│   ├── cliente/                        # Paquete del cliente
│   │   ├── Cliente.java               # Clase principal del cliente
│   │   ├── PoliticaReconexion.java    # Política de reconexión
│   │   ├── PoliticaReintentos.java    # Política de reintentos
│   │   └── ConfiguracionTimeout.java  # Configuración de timeouts
│   │
│   └── pruebas/                        # Escenarios de prueba
│       ├── EscenarioUno.java          # Clientes sin servidor
│       ├── EscenarioDos.java          # Servidor sin reinicio
│       └── EscenarioTres.java         # Servidor con reinicio automático
```

## 🔧 Configuración

### ConfiguracionPoliticas

```java
ConfiguracionPoliticas config = new ConfiguracionPoliticas();
config.setMaxReintentos(5);                      // Número máximo de reintentos
config.setTiempoEsperaEntreReintentos(3000);     // 3 segundos entre reintentos
config.setTimeoutConexion(5000);                 // 5 segundos de timeout
config.setReinicioAutomaticoHabilitado(true);    // Habilitar reinicio automático
config.setTiempoEsperaReinicio(2000);            // 2 segundos antes de reiniciar
```

### ConfiguracionTimeout

```java
ConfiguracionTimeout configTimeout = new ConfiguracionTimeout();
configTimeout.setTimeoutConexion(5000);   // Timeout de conexión
configTimeout.setTimeoutLectura(10000);   // Timeout de lectura
configTimeout.setTimeoutEscritura(5000);  // Timeout de escritura
configTimeout.setTimeoutReintento(3000);  // Timeout entre reintentos
```

## 🚀 Ejecución Rápida (Interfaces Principales)

### Opción 1: Usando Scripts (Windows)

```bash
# Compilar el proyecto
compilar.bat

# Ejecutar el servidor (en una terminal)
ejecutar-servidor.bat

# Ejecutar el cliente (en otra terminal)
ejecutar-cliente.bat
```

### Opción 2: Línea de Comandos

```bash
# Compilar
javac -d bin src/comun/*.java src/servidor/*.java src/cliente/*.java

# Ejecutar servidor
java -cp bin servidor.ServidorPrincipal

# Ejecutar cliente (en otra terminal)
java -cp bin cliente.ClientePrincipal
```

### Características de las Interfaces Principales

**ServidorPrincipal** - Menú interactivo para controlar el servidor:
- ✅ Iniciar/Detener servidor
- ✅ Reiniciar servidor manualmente
- ✅ Ver estado y clientes conectados
- ✅ Habilitar/Deshabilitar reinicio automático
- ✅ Configuración de puerto y políticas

**ClientePrincipal** - Menú interactivo para el cliente:
- ✅ Conectar al servidor con reintentos automáticos
- ✅ Enviar mensajes individuales
- ✅ Modo chat interactivo
- ✅ Ver estado de conexión
- ✅ Configuración de host, puerto y políticas

---

## 📚 Uso Programático

### Crear Servidor

```java
// Servidor básico (puerto 8080)
Servidor servidor = new Servidor();

// Servidor con puerto personalizado
Servidor servidor = new Servidor(9090);

// Servidor con configuración completa
ConfiguracionPoliticas config = new ConfiguracionPoliticas();
config.setReinicioAutomaticoHabilitado(true);
Servidor servidor = new Servidor(8080, config);

// Iniciar servidor
servidor.iniciar();

// Habilitar/deshabilitar reinicio automático
servidor.setReinicioAutomatico(true);

// Detener servidor
servidor.detener();

// Apagar completamente (detiene servidor y monitor)
servidor.apagar();
```

### Crear Cliente

```java
// Cliente básico
Cliente cliente = new Cliente("MiCliente");

// Cliente con host y puerto personalizados
Cliente cliente = new Cliente("MiCliente", "192.168.1.100", 8080);

// Cliente con configuración completa
ConfiguracionPoliticas config = new ConfiguracionPoliticas();
config.setMaxReintentos(10);
ConfiguracionTimeout configTimeout = new ConfiguracionTimeout();
Cliente cliente = new Cliente("MiCliente", "localhost", 8080, config, configTimeout);

// Conectar (aplica política de reintentos)
boolean conectado = cliente.conectar();

// Enviar mensajes
cliente.enviarMensaje("Hola servidor");

// Modo interactivo
cliente.modoInteractivo();

// Configurar políticas
cliente.setReconexionHabilitada(true);
cliente.setReintentosHabilitados(true);

// Desconectar
cliente.desconectar();
```

## 📝 Escenarios de Prueba

### Escenario 1: Clientes sin Servidor

**Objetivo**: Demostrar política de reintentos y timeout cuando no hay servidor disponible.

```bash
# Compilar
javac -d bin src/comun/*.java src/cliente/*.java src/pruebas/EscenarioUno.java

# Ejecutar
java -cp bin pruebas.EscenarioUno
```

**Comportamiento esperado**:
- ❌ Servidor NO iniciado
- 🔄 Clientes intentan conectarse
- ⏱️ Aplican timeouts y reintentos
- ❌ Terminan con timeout después de agotar reintentos

---

### Escenario 2: Servidor sin Reinicio Automático

**Objetivo**: Servidor se apaga manualmente y NO se reinicia automáticamente.

```bash
# Compilar
javac -d bin src/comun/*.java src/servidor/*.java src/cliente/*.java src/pruebas/EscenarioDos.java

# Ejecutar
java -cp bin pruebas.EscenarioDos
```

**Comportamiento esperado**:
1. ✅ Servidor inicia normalmente
2. ✅ N clientes se conectan
3. 🛑 Servidor se apaga manualmente (presionar ENTER)
4. 🔄 Clientes ejecutan política de reintentos
5. ❌ Servidor NO se reinicia (política deshabilitada)
6. ❌ Clientes agotan reintentos

---

### Escenario 3: Servidor con Reinicio Automático

**Objetivo**: Servidor se reinicia automáticamente y clientes se reconectan.

```bash
# Compilar
javac -d bin src/comun/*.java src/servidor/*.java src/cliente/*.java src/pruebas/EscenarioTres.java

# Ejecutar
java -cp bin pruebas.EscenarioTres
```

**Comportamiento esperado**:
1. ✅ Servidor inicia con reinicio automático habilitado
2. ✅ N clientes se conectan
3. 🛑 Servidor se apaga (presionar ENTER)
4. 👁️ Monitor detecta servidor caído
5. 🔄 Monitor reinicia servidor automáticamente
6. 🔄 Clientes ejecutan política de reintentos
7. ✅ Clientes se reconectan al servidor reiniciado
8. ✅ Comunicación restablecida

---

## 🎯 Políticas Implementadas

### Servidor: PoliticaReinicio

| Método | Descripción |
|--------|-------------|
| `setReinicioHabilitado(boolean)` | Habilita/deshabilita reinicio automático |
| `debeReiniciar()` | Verifica si el servidor debe reiniciarse |
| `esperarAntesDeReiniciar()` | Espera tiempo configurado antes de reiniciar |
| `marcarServidorActivo()` | Marca servidor como activo |
| `marcarServidorInactivo()` | Marca servidor como inactivo |

### Cliente: PoliticaReconexion

| Método | Descripción |
|--------|-------------|
| `setReconexionHabilitada(boolean)` | Habilita/deshabilita reconexión |
| `debeReconectar()` | Verifica si debe intentar reconectar |
| `actualizarEstado(EstadoConexion)` | Actualiza estado de conexión |

### Cliente: PoliticaReintentos

| Método | Descripción |
|--------|-------------|
| `puedeReintentar()` | Verifica si puede hacer más reintentos |
| `registrarIntento()` | Registra un intento de conexión |
| `esperarAntesDeReintentar()` | Espera antes de reintentar |
| `reiniciarContador()` | Reinicia contador de reintentos |

## 📊 Estados de Conexión

```java
public enum EstadoConexion {
    DESCONECTADO,      // Sin conexión
    CONECTANDO,        // Intentando conectar
    CONECTADO,         // Conectado exitosamente
    ERROR,             // Error de conexión
    TIMEOUT,           // Timeout agotado
    REINTENTANDO       // Reintentando conexión
}
```

## 🔍 Monitoreo

El `MonitorServidor` supervisa el estado del servidor en segundo plano:

- ✅ Se ejecuta en un hilo daemon
- ✅ Chequea cada 1 segundo
- ✅ Detecta caídas del servidor
- ✅ Ejecuta reinicio automático según política
- ✅ Registra eventos de reinicio

## 💡 Ejemplos de Uso

### Ejemplo 1: Servidor Resiliente

```java
ConfiguracionPoliticas config = new ConfiguracionPoliticas();
config.setReinicioAutomaticoHabilitado(true);
config.setTiempoEsperaReinicio(5000);

Servidor servidor = new Servidor(8080, config);
servidor.iniciar();

// El servidor se reiniciará automáticamente si falla
```

### Ejemplo 2: Cliente Persistente

```java
ConfiguracionPoliticas config = new ConfiguracionPoliticas();
config.setMaxReintentos(10);
config.setTiempoEsperaEntreReintentos(5000);

Cliente cliente = new Cliente("ClientePersistente", "localhost", 8080, config, new ConfiguracionTimeout());
cliente.setReconexionHabilitada(true);
cliente.conectar();

// El cliente reintentará 10 veces con 5 segundos de espera
// Si se desconecta, intentará reconectarse automáticamente
```

## 🛠️ Compilación

```bash
# Compilar todo el proyecto
javac -d bin src/comun/*.java src/servidor/*.java src/cliente/*.java src/pruebas/*.java

# Crear estructura de directorios
mkdir -p bin
```

## ▶️ Ejecución

```bash
# Escenario 1
java -cp bin pruebas.EscenarioUno

# Escenario 2
java -cp bin pruebas.EscenarioDos

# Escenario 3
java -cp bin pruebas.EscenarioTres
```

## 📌 Notas Importantes

1. **Thread Safety**: Todas las clases utilizan `AtomicBoolean` y sincronización para garantizar seguridad en entornos multihilo.

2. **Recursos**: Los sockets y streams se cierran correctamente en bloques `finally` o mediante métodos de limpieza.

3. **Timeouts**: Los timeouts previenen bloqueos indefinidos en operaciones de red.

4. **Monitor Daemon**: El monitor se ejecuta como daemon para no impedir la terminación del programa.

5. **Estados de Conexión**: El sistema mantiene estados precisos para depuración y monitoreo.

## 🎓 Conceptos Implementados

- ✅ Sockets TCP (ServerSocket, Socket)
- ✅ Multithreading (Thread, Runnable)
- ✅ Sincronización (synchronized, AtomicBoolean)
- ✅ Manejo de excepciones (try-catch-finally)
- ✅ Patrones de diseño (Strategy, Monitor, Observer)
- ✅ Políticas de resiliencia (Retry, Timeout, Auto-restart)
- ✅ Gestión de recursos (AutoCloseable)

## 📄 Licencia

Este código fue desarrollado con fines educativos para el curso de Sistemas Distribuidos.

---

**Desarrollado con ☕ y Java**
