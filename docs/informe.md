# REGLAS HEXAGONAL

## Regla 3: Lombok y validaciones

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/dto/command/CreateUserCommand.java`
* **Problema:** Se mezcla la anotación `@Builder` de Lombok con un `record`.
* **Solución:** Se eliminó la anotación `@Builder` y su importación, ya que los records proporcionan constructores canónicos por defecto, haciendo que el uso del builder sea redundante e innecesario.

### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/dto/query/GetUserByIdQuery.java`
* **Problema:** Uso redundante de `@Builder` en un `record`.
* **Solución:** Se eliminó la anotación y la importación de Lombok.

### Violación 3
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/dto/query/GetUserByIdQuery.java`
* **Problema:** La restricción `@NotBlank` tenía un mensaje de error personalizado (`message = ...`), lo cual va en contra de la directiva de usar los mensajes por defecto de Jakarta.
* **Solución:** Se eliminó el parámetro `message` de la anotación, delegando la responsabilidad del texto al estándar de Jakarta Validation.

### Violación 4 - Un solo nivel de abstracción por función
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/CreateUserService.java`
* **Problema:** El método `execute` mezclaba lógica de orquestación (negocio) con detalles técnicos de construcción de objetos de dominio (instanciación de Value Objects).
* **Solución:** Se delegó la creación del `UserModel` al `UserApplicationMapper`. Esto permite que el servicio opere exclusivamente en un nivel de abstracción de alto nivel, cumpliendo con la separación de responsabilidades.


### Violación 5
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/GetUserByIdService.java`
* **Problema:** La anotación `@Valid` estaba declarada como parámetro en la implementación del método sobrescrito (`@Override execute`). Según las especificaciones de Jakarta Validation y los principios de diseño de interfaces, las constraints de validación deben definirse en el contrato (el puerto de entrada o interfaz), no en la clase concreta que lo implementa.
* **Solución:** Se eliminó la anotación `@Valid` y su importación de la clase concreta `GetUserByIdService`, dejando la firma limpia y cumpliendo con el estándar de declaración de validaciones.

## Regla 6: Excepciones, logging y telemetría

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/DeleteUserService.java`
* **Problema:** Uso de un `Logger` manual (`java.util.logging.Logger`) en lugar de la anotación `@Log` de Lombok, rompiendo la consistencia del proyecto.
* **Solución:** Se eliminó la declaración manual y se integró la anotación `@Log`, estandarizando el mecanismo de logging.



### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/DeleteUserService.java`
* **Problema:** Presencia de un bloque `try-catch` que captura excepciones genéricas sin lógica de recuperación, realizando un log redundante antes de relanzar la excepción.
* **Solución:** Se eliminó el bloque `try-catch`. Esto permite que las excepciones se propaguen limpiamente hacia la capa de infraestructura para ser procesadas por el manejador global de excepciones, reduciendo la complejidad del método.


## Reglas 21 y 5: No usar códigos especiales de error y No retornar null

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/GetAllUsersService.java`
* **Problema:** El método `execute` comprobaba si la lista devuelta por la base de datos estaba vacía y, de ser así, retornaba explícitamente `null`. Esto utiliza a `null` como un código especial para denotar ausencia (Regla 21) y viola la directiva de nunca retornar nulos en colecciones (Regla 5), obligando a los consumidores a implementar comprobaciones manuales para evitar excepciones.
* **Solución:** Se eliminó la validación `isEmpty()` y el retorno de `null`. Ahora el método retorna directamente el resultado de `getAllUsersPort.getAll()`. Si no hay usuarios, devolverá una lista vacía, lo cual es semánticamente correcto y seguro de iterar.

# REGLAS CLEAN CODE

## Regla 21: No usar códigos especiales de error

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/mapper/UserApplicationMapper.java`
* **Problema:** El método `roleToCode` devolvía `-1` para señalar errores, obligando a adivinar el significado de ese número mágico. Además, el archivo contenía comentarios sobre la mutabilidad del dominio que ya estaban obsoletos.
* **Solución:** Se reemplazó el retorno de `-1` por el lanzamiento de `IllegalArgumentException` con mensajes claros. También se eliminó la advertencia de la Regla 15, ya que `UserModel` es inmutable por diseño.


## Regla 24: Consistencia Semántica

### Violación 1
* **Archivo:** `UserApplicationMapper.java`
* **Problema:** Uso de nombres diferentes (`correo` vs `correoElectronico`) para el mismo concepto.
* **Solución:** Se unificó el nombre de la variable en todos los métodos de la clase.


### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/mapper/UserApplicationMapper.java`
* **Problema:** El mismo concepto (email del usuario) recibía múltiples nombres sin justificación en fromUpdateCommandToModel.
* **Solución:** Se unificaron las variables bajo el nombre `userEmail` para mantener la consistencia semántica en toda la clase.




## Regla 15 - Inmutabilidad como preferencia

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/mapper/UserApplicationMapper.java`
* **Problema:** El código contenía advertencias sobre el "efecto cascada" y la mutabilidad de `UserModel`, lo cual generaba ruido visual y confusión dado que el modelo ya había sido corregido.
* **Solución:** Se limpiaron los comentarios obsoletos, ya que no había ningún @data

## Regla 10 - Eliminar comentarios redundantes

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/CreateUserService.java`
* **Problema:** El método `execute` estaba plagado de comentarios que explicaban lo obvio (ej. `// guardar el usuario` antes de un método `save`). 
* **Solución:** Se eliminaron todos los comentarios redundantes para reducir el ruido visual y forzar a que el código se explique por sí mismo mediante buenos nombres de variables y métodos.



## Regla 9: Código expresivo antes que comentarios

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/CreateUserService.java`
* **Problema:** Uso de comentarios descriptivos (`// validar campos`, `// verificar si el email existe`) para compensar la falta de expresividad del código.
* **Solución:** Se extrajo la lógica a los métodos privados `validateCommand` y `ensureEmailIsNotRegistered`. Esto permite que el método `execute` sea autodocumentado, eliminando la necesidad de comentarios.


## Regla 1 y 2: Una sola cosa por función y funciones cortas

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/CreateUserService.java`
* **Problema:** El método `execute` realizaba múltiples tareas: validación, lógica de negocio, persistencia y notificaciones externas, resultando en una función extensa.
* **Solución:** Se fragmentó la lógica extrayendo el proceso de guardado y notificación al método privado `createAndNotify`. El método `execute` ahora es una función corta que cumple con una única responsabilidad: orquestar el flujo de creación.


## Regla 25 y 26: Claridad sobre ingenio y Evitar sobrecompactación
### Violación 1
* **Archivo:** `EmailNotificationService.java`
* **Problema:** Los métodos `notifyUser...` comprimían la carga de plantillas, renderización, construcción del destino y envío en una sola línea anidada.
* **Solución:** Se descompactaron las expresiones en variables declarativas (`rawTemplate`, `htmlBody`, `destination`), priorizando la legibilidad paso a paso.

## Regla 3 y 11: Niveles de abstracción y Evitar duplicación
### Violación 1
* **Archivo:** `EmailNotificationService.java`
* **Problema:** La lógica de orquestación de correos estaba duplicada en dos métodos y mezclaba lógica de alto nivel con manipulación de strings de bajo nivel.
* **Solución:** Se extrajo la orquestación al método privado `processAndSend`, centralizando el proceso y manteniendo un único nivel de abstracción por método.


## Regla 6: Evitar parámetros booleanos de control (Clean Code)

### Violación 1
* **Archivo:** `EmailNotificationService.java`
* **Problema:** El método `sendNotificationWithFlag` utilizaba un booleano (`includePassword`) para bifurcar el flujo de ejecución entre dos comportamientos distintos (creación vs actualización).
* **Solución:** Se eliminó el método con el "Flag Argument". Ahora los consumidores de la clase deben invocar explícitamente `notifyUserCreated` o `notifyUserUpdated`, eliminando efectos secundarios y mejorando la claridad de la API.


## Regla 4: Estilo y Naming

### Violación 1
* **Archivo:** `EmailNotificationService.java`
* **Problema:** El método privado `renderTemplate` actuaba como una función pura que no utilizaba el estado de la instancia, pero no estaba marcado como `static`.
* **Solución:** Se añadió el modificador `static` a `renderTemplate` para indicar claramente su independencia del estado de la clase y adherirse a las convenciones de Clean Code en Java.



## Regla 7: Evitar efectos secundarios ocultos

### Violación 1
* **Archivo:** `EmailNotificationService.java`
* **Problema:** El método `sendOrLog` prometía enviar o registrar un log, pero en la práctica lanzaba una excepción no declarada en su nombre, ocultando un efecto secundario importante al consumidor del método.
* **Solución:** Se eliminó el método `sendOrLog` y su bloque `try-catch` redundante. Ahora el método invoca directamente a `emailSenderPort.send()`, dejando que la excepción de dominio fluya transparentemente si el envío falla.

---

# REGLAS HEXAGONAL (continuación)

## Regla 2: Modelado y tipos

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/dto/UserResponse.java`
* **Problema:** El DTO de respuesta usaba `@Data` de Lombok, generando setters públicos y haciendo el objeto mutable. Los DTOs de salida deben ser inmutables, impediendo que su estado sea modificado después de ser construido. Con `@Data` es posible hacer `response.setEmail("otro@email.com")` desde cualquier lugar, comprometiendo la integridad del objeto.
* **Solución:** Se convirtió `UserResponse` de una clase con `@Data` a un Java `record`, que proporciona inmutabilidad total, genera automáticamente el constructor canónico, getters (sin prefijo "get"), `toString`, `equals` y `hashCode`. Los accesores en records usan notación sin prefijo: `response.id()` en lugar de `response.getId()`. Se actualizaron todas las referencias en `UserResponsePrinter.java` y en las pruebas de `UserControllerTest.java`.

## Regla 8: Separar comandos y consultas (CQS)

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/UpdateUserService.java`
* **Problema:** El método `execute` violaba el Principio de Separación de Comandos y Consultas (CQS). El método tanto **modificaba estado** (actualizaba el usuario en la base de datos) como **retornaba una consulta** (el usuario actualizado). Esto crea confusión: ¿es un comando que retorna efectos secundarios, o una consulta que modifica datos? El contrato es ambiguo y dificulta el razonamiento sobre los efectos del método.
* **Solución:** Se refactorizó `execute` para que sea un puro comando (`void execute(command)`). El método ahora solo modifica estado: valida, persiste la actualización y notifica. Se modificó el `UpdateUserUseCase` para retornar `void`. El controlador `UserController` ahora ejecuta el comando y luego realiza una consulta separada usando `GetUserByIdUseCase` para recuperar el usuario actualizado. Esto clarifica la intención: el comando ejecuta la operación, la consulta obtiene el resultado. También se removió el método helper `notifyIfRequired` que usaba un parámetro booleano para bifurcar el comportamiento, simplificando el flujo al notificar siempre durante la actualización.

## Regla 13: Evitar clases utilitarias innecesarias

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/UserValidationUtils.java`
* **Problema:** `UserValidationUtils` era una clase "util" que agrupaba métodos de validación fragmentados (`isUserActive`, `isAdmin`, `isValidEmail`, `isValidPassword`, `canPerformAction`). Estos métodos pertenecen naturalmente a sus respectivos objetos de dominio (`UserModel`, `UserRole`, `UserEmail`, `UserPassword`) o a servicios de dominio dedicados. La clase no tenía estado, nunca era instanciada, y su única función era coleccionar lógica dispersa sin cohesión. Además, no era utilizada en ningún lugar del codebase, siendo código muerto. Representa el anti-patrón de crear clases "Utils" para esconder lógica que no se sabe dónde poner.
* **Solución:** Se eliminó completamente la clase `UserValidationUtils.java`. Esto fuerza a que la validación regrese a sus lugares correctos: reglas de negocio en los value objects del dominio, y verificaciones de estado/permisos en los servicios que las necesitan. Si en el futuro se requiere acceder a validaciones comunes, se crearán métodos en los objetos de dominio o se usarán servicios dedicados. Esta eliminación mejora la cohesión: cada objeto responsable de su propia validación.