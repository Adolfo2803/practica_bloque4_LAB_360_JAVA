# 🦖 Parque Turístico de Dinosaurios

Simulación secuencial **no-determinista** de un parque temático de dinosaurios, desarrollada en **Java 17 + Maven**. El sistema modela el comportamiento de turistas, trabajadores, dinosaurios y vehículos a través de distintas zonas del parque, gestiona recursos compartidos, dispara eventos aleatorios (escapes, apagones, tormentas, etc.) y persiste todos los ingresos, gastos y eventos en una base de datos **H2** versionada con **Liquibase**.

> **Nivel:** Intermedio (Bloque 4).

---

## 📋 Tabla de contenidos

1. [Herramientas utilizadas](#-herramientas-utilizadas)
2. [Requisitos previos](#-requisitos-previos)
3. [Instrucciones de configuración](#-instrucciones-de-configuración)
4. [Forma de ejecución](#-forma-de-ejecución)
5. [Explicación general del sistema](#-explicación-general-del-sistema)
6. [Patrones de diseño utilizados](#-patrones-de-diseño-utilizados)
7. [Persistencia de datos](#-persistencia-de-datos)
8. [Pruebas y cobertura](#-pruebas-y-cobertura)
9. [Estructura del proyecto](#-estructura-del-proyecto)

---

## 🛠 Herramientas utilizadas

| Elemento | Versión / Tecnología |
|---|---|
| Lenguaje | **Java 17** (`maven.compiler.release = 17`) |
| Gestor de build | **Maven** |
| Base de datos | **H2** `2.2.224` (embebida, modo archivo) |
| Versionado de BD | **Liquibase** `4.27.0` |
| Pruebas unitarias | **JUnit Jupiter** `5.10.2` |
| Mocking | **Mockito** `5.11.0` |
| Cobertura | **JaCoCo** (umbral mínimo configurado: **65%**) |
| Ejecución | **exec-maven-plugin** |
| Paquete raíz | `com.axity.dinosaurpark` |

---

## ✅ Requisitos previos

- **JDK 17** instalado (`java -version` debe reportar 17).
- **Maven 3.8+** (`mvn -version`).
- No se requiere instalar ningún servidor de base de datos: **H2 es embebida** y se crea automáticamente en disco al ejecutar.

---

## ⚙️ Instrucciones de configuración

Toda la configuración del parque se realiza mediante el archivo de propiedades externo:

```
src/main/resources/park.properties
```

Ahí se ajustan, sin recompilar lógica de negocio, parámetros como:

- **Población del parque:** número de turistas, dinosaurios carnívoros/herbívoros, guardias y técnicos.
- **Capacidades y precios:** capacidad de la zona de arribo, precio de boletos, precio de souvenirs, capacidad de baños, precios de SPA y de recintos de observación (Basic/Premium/VIP).
- **Planta de energía:** energía inicial, consumo por paso, probabilidad de falla, costos de mantenimiento y reparación.
- **Flota de vehículos:** cantidad de vehículos y pasos necesarios para repararlos.
- **Probabilidades de eventos:** escape, apagón, tormenta, hora de ofertas y falla de vehículos.
- **Monitoreo:** cada cuántos pasos se imprime el snapshot del estado del parque.
- **Ruta de la base de datos:** `db.path`.

> En el nivel intermedio la simulación es **no-determinista**: el motor usa `new Random()` sin semilla, por lo que cada corrida produce resultados (ingresos, eventos) distintos.

Ejemplo de propiedades:

```properties
tourists=50
dinosaurs.carnivores=5
dinosaurs.herbivores=15
vehicles.count=4
vehicles.repairSteps=5
event.escape.probability=0.05
monitoring.intervalSteps=10
db.path=./data/parkdb
```

---

## ▶️ Forma de ejecución

Desde la raíz del proyecto:

```bash
# 1. Compilar
mvn compile

# 2. Ejecutar las pruebas y validar cobertura (JaCoCo)
mvn test

# 3. Ejecutar la simulación del parque
mvn exec:java
```

Al ejecutar `mvn exec:java`:

- Se crea (si no existe) la base de datos embebida en `./data/parkdb.mv.db`.
- Liquibase aplica los changelogs y crea las tablas `revenues`, `expenses` y `events`.
- La simulación corre el número de pasos configurado, imprimiendo el monitoreo del parque en los intervalos definidos.
- Al finalizar se muestra un resumen con ingresos y gastos totales.

El reporte de cobertura queda disponible en:

```
target/site/jacoco/index.html
```

---

## 🧩 Explicación general del sistema

El sistema simula la operación de un parque de dinosaurios a lo largo de una serie de **pasos de ejecución** (`steps`). En cada paso ocurre, en orden:

1. **Llegadas** — La zona de arribo procesa por lotes a los turistas en cola, les vende boletos (generando ingresos) y los hace pasar al parque.
2. **Movimiento** — Cada turista activo recorre las zonas: el Recinto Central (donde puede comprar souvenirs), los baños (capacidad limitada, con SPA opcional) y los recintos de observación (Basic/Premium/VIP, con encuestas de satisfacción).
3. **Ticks de zonas** — La planta de energía consume energía y puede fallar; los baños liberan slots por tiempo; los vehículos avanzan su estado de reparación.
4. **Eventos aleatorios** — Por cada evento posible se "tira el dado": si la probabilidad se cumple, el evento se ejecuta y se registra (escape de dinosaurio, apagón masivo, tormenta torrencial, hora de ofertas, falla de vehículo).
5. **Trabajadores** — Los guardias recapturan dinosaurios escapados; los técnicos reparan la planta usando un vehículo disponible; se descuentan salarios como gasto operativo.
6. **Monitoreo** — En los intervalos configurados se imprime un snapshot con: turistas activos, dinosaurios en recinto, energía disponible, eventos activos del paso y vehículos no disponibles.

### Zonas del parque

| Zona | Función |
|---|---|
| **Zona de Arribo** | Entrada de turistas, control de capacidad y venta de boletos. |
| **Recinto Central** | Punto principal, venta de souvenirs y distribución hacia otras zonas. |
| **Baños** | Capacidad y uso por tiempo limitados; servicio de SPA de pago. |
| **Planta de Energía** | Suministra energía; sufre fallas aleatorias; genera costos operativos. |
| **Recintos de Observación** | Tres experiencias (Basic/Premium/VIP) con precios y encuestas distintas. |

### Sistema de monitoreo

El monitor reporta periódicamente cinco métricas clave del estado del parque, dando visibilidad del sistema sin saturar la consola (se imprime solo cada `monitoring.intervalSteps`).

---

## 🎯 Patrones de diseño utilizados

Se aplican **tres** patrones, debidamente justificados:

### 1. Singleton — `config.ParkConfig`
La configuración del parque debe leerse **una sola vez** desde `park.properties` y estar disponible globalmente de forma consistente. `ParkConfig` tiene constructor **privado** y un método estático `getInstance()` que garantiza una única instancia compartida durante toda la simulación. Esto evita lecturas redundantes del archivo y un estado de configuración inconsistente.

### 2. Strategy — `event.SimulationEvent` y sus implementaciones
Los eventos aleatorios (escape, apagón, tormenta, ofertas, falla de vehículo) comparten una misma interfaz `SimulationEvent` con un método `execute(ParkState, Random)`. El motor de simulación los trata de forma homogénea (una lista de eventos) y dispara `execute()` **sin conocer el tipo concreto**. Agregar un nuevo tipo de evento no requiere modificar el motor: solo crear una nueva implementación de la estrategia.

### 3. Herencia / Polimorfismo — jerarquías `Dinosaur` y `Worker`
`Dinosaur` es una clase abstracta especializada en `CarnivoreDinosaur` y `HerbivoreDinosaur`, cada una con su propia dieta y nivel de peligrosidad. `Worker` es abstracta y se especializa en `Guard` (recaptura dinosaurios) y `Technician` (repara la planta). Esto permite compartir lo común e iterar colecciones homogéneas tratando a cada entidad por su comportamiento polimórfico.

> Diagramas de secuencia / flujo del loop de simulación y de las jerarquías de clases se incluyen en la carpeta `docs/` del proyecto.

---

## 💾 Persistencia de datos

Toda la información económica y de eventos se persiste en la base de datos H2, en tres tablas creadas y versionadas por Liquibase:

| Tabla | Contenido |
|---|---|
| `revenues` | Ingresos: venta de boletos, souvenirs, SPA, entradas a recintos. |
| `expenses` | Gastos operativos: mantenimiento, consumo de energía, reparaciones, salarios. |
| `events` | Todos los eventos generados durante la simulación, con el paso en que ocurrieron. |

Los changelogs de Liquibase están en `src/main/resources/db/changelog/`. Todo acceso a la base de datos se realiza mediante `PreparedStatement` parametrizado para evitar inyección de SQL.

---

## 🧪 Pruebas y cobertura

- **121 pruebas unitarias** con **JUnit 5** y **Mockito** (para verificar interacciones, p. ej. que un evento invoque a la planta de energía).
- **JaCoCo** valida una cobertura **mínima del 65%**: si no se alcanza, `mvn test` falla con `BUILD FAILURE` aunque todos los tests pasen. Cobertura actual: **97.8%**.
- Las pruebas que requieren base de datos usan una instancia aislada por test (nombre único) para no interferir entre sí.

```bash
mvn test
# Reporte detallado: target/site/jacoco/index.html
```

---

## 📁 Estructura del proyecto

```
dinosaur-park/
├── pom.xml
├── README.md
├── docs/                         # diagramas UML / de flujo
└── src/
    ├── main/
    │   ├── java/com/axity/dinosaurpark/
    │   │   ├── config/            # ParkConfig (Singleton)
    │   │   ├── model/             # Tourist, Dinosaur, Worker, Ticket, Vehicle...
    │   │   ├── zone/              # ParkZone y las 5 zonas
    │   │   ├── event/             # SimulationEvent (Strategy) + eventos
    │   │   ├── persistence/       # DatabaseService + records
    │   │   ├── simulation/        # ParkState, SimulationEngine
    │   │   ├── monitoring/        # ParkMonitor
    │   │   └── Main.java
    │   └── resources/
    │       ├── park.properties
    │       └── db/changelog/      # changelogs Liquibase
    └── test/
        └── java/com/axity/dinosaurpark/
```

---

## 👤 Autor

Proyecto desarrollado como parte del laboratorio **Bloque 4 — Parque Turístico de Dinosaurios (Nivel Intermedio)**.
