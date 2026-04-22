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



### Violación 3
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/valueobject/UserEmail.java`
* **Problema:** La capa de dominio contenía un `Logger` e invocaba `LOGGER.warning()` para registrar un dato PII (el email del usuario) durante la validación del value object. El dominio no debe tener dependencias de infraestructura (logging, I/O, etc.) ni debe ser responsable de telemetría. Además, loguear datos PII en el dominio viola principios de privacidad y seguridad.
* **Solución:** Se removió completamente el `Logger`, su importación y la llamada al logging. El dominio ahora es puro: solo realiza validación y persiste el estado sin efectos secundarios de infraestructura. Si se requiere logging, esa responsabilidad recae en los servicios de aplicación que invocan al dominio.

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
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/model/UserModel.java`
* **Problema:** `UserModel` usaba `@Data` de Lombok junto con `@AllArgsConstructor`, lo cual generaba setters públicos para todos los campos. El modelo de dominio debe ser inmutable: los setters públicos permiten que cualquier clase modifique el estado sin pasar por invariantes ni reglas de negocio. Con `@Data`, cualquiera podría hacer `userModel.setStatus(BLOCKED)` desde fuera del dominio, rompiendo el encapsulamiento.
* **Solución:** Se reemplazó `@Data + @AllArgsConstructor` por `@Value` de Lombok, que automáticamente hace todos los campos `final` y no genera setters. Los datos solo pueden ser leídos, no modificados después de la construcción, garantizando la inmutabilidad del agregado raíz.

## Regla 10 - Eliminar comentarios redundantes

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/exception/EmailSenderException.java`
* **Problema:** Los métodos factory contenían strings hardcodeados directamente en los mensajes de error sin extraerlos a constantes nombradas, dificultando su mantenimiento y cambios futuros.
* **Solución:** Se extrajeron los mensajes a constantes estáticas `SMTP_ERROR_MESSAGE` y `SEND_FAILED_MESSAGE` en la clase, permitiendo reutilizarlas y facilitar cambios centralizados.

### Violación 4
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/valueobject/UserName.java`
* **Problema:** El método `validateMinimumLength()` contenía un magic number `3` sin nombre descriptivo. Además, el canonical constructor usaba `if (value == null)` en lugar de `Objects.requireNonNull()`.
* **Solución:** Se extrajo la constante `MINIMUM_LENGTH = 3` y se reemplazó la validación de nulidad manual por `Objects.requireNonNull()`, mejorando la legibilidad y siguiendo las convenciones estándar de Java.

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
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/cli/io/ConsoleIO.java`
* **Problema:** Uso de nombres de variables abreviados (`v`, `r`) en lugar de nombres descriptivos. En `readRequired()` la variable se llamaba `v` para el valor leído, y en `readInt()` se llamaba `r` para el raw input. Nombres cortos y sin significado hacen el código más difícil de leer y mantener, especialmente cuando el mismo concepto recibe nombres diferentes en la misma clase (violando también Regla 24).
* **Solución:** Se renombraron las variables a `value` y `rawInput` respectivamente, nombres que comunican claramente su propósito. El código ahora se auto-documenta sin necesidad de comentarios explicativos.

### Violación 7
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/controller/UserController.java`
* **Problema:** El método `listAllUsers()` usaba la abreviatura `usrs` para el nombre de variable en lugar del nombre completo. Las abreviaturas reducen la legibilidad y requieren que el lector descifre qué significa `usrs` — especialmente problemático en un equipo donde otros no escribieron el código.
* **Solución:** Se renombró la variable a `users`, que es clara, autodocumentada, y no crea confusión. El nombre explícito comunica su propósito sin ambigüedad.

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

## Regla 14: Ley de Deméter

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/LoginService.java`
* **Problema:** El método `getAndValidateUser` hacía `user.getPassword().verifyPlain(plainPassword)`, navegando a través del objeto intermedio `UserPassword` para acceder a su método `verifyPlain`. Esto viola la Ley de Deméter que establece: "habla solo con tus amigos directos, no con los amigos de tus amigos". El servicio no debe conocer la estructura interna de `UserModel` (que tiene un `UserPassword`) ni cómo verificar la contraseña internamente. Rompe el encapsulamiento y crea acoplamiento frágil.
* **Solución:** Se agregó un método delegador `verifyPassword(String plainPassword)` en `UserModel` que encapsula la verificación. Ahora `LoginService` llama directamente a `user.verifyPassword(plainPassword)`, hablando solo con su "amigo directo" (`UserModel`). El método delegador internamente accede a `password.verifyPlain()`, manteniendo la responsabilidad de verificación dentro del agregado raíz. Esto reduce el acoplamiento y mejora el encapsulamiento.

### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/adapter/persistence/mapper/UserPersistenceMapper.java`
* **Problema:** El método `fromModelToDto` hacía múltiples llamadas encadenadas como `user.getId().value()`, `user.getName().value()`, etc., navegando a través de cada value object para extraer su valor primitivo. El mapper violaba la Ley de Deméter al necesitar conocer la estructura interna de `UserModel` (qué value objects contiene) y cómo acceder a sus valores. Esto crea acoplamiento fuerte: cualquier cambio en los value objects requeriría cambiar el mapper.
* **Solución:** Se agregaron métodos delegadores en `UserModel`: `idValue()`, `nameValue()`, `emailValue()`, `passwordValue()` que extraen y retornan directamente los valores primitivos. Ahora el mapper llama a estos métodos en lugar de navegar al interior del objeto. El mapper habla solo con `UserModel` (su "amigo directo"), mientras que `UserModel` es responsable de proporcionar acceso a sus datos de forma segura y encapsulada.

## Regla 16: Evitar condicionales repetitivas

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/cli/io/UserResponsePrinter.java`
* **Problema:** El método `getStatusLabel()` utilizaba una cascada larga de `if/else if` para mapear estados de usuario a sus etiquetas de presentación. Esta estructura crece linealmente con cada nuevo estado agregado, es repetitiva, difícil de mantener, y viola la intención: solo necesita buscar un valor en una tabla.
* **Solución:** Se reemplazó la cascada de condicionales por un `Map<String, String>` estático (`STATUS_LABELS`) que agrupa todos los estados y sus etiquetas. El método ahora usa `getOrDefault()` para buscar la etiqueta o retornar un valor por defecto. Agregar un nuevo estado es ahora trivial: solo añadir una entrada al mapa, sin modificar lógica condicional.

## Regla 9: Arquitectura Hexagonal — Dependencias hacia el centro

### Violación 3
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/controller/UserController.java`
* **Problema:** En el método `createUser`, el entrypoint construía directamente un `CreateUserCommand` del dominio de aplicación sin pasar por el mapper. Esto acopla la capa de infraestructura directamente con los objetos de transferencia de datos de la capa de aplicación, saltándose la responsabilidad del mapper que es justamente traducir entre las peticiones externas y los comandos internos.
* **Solución:** Se utilizó el método estático `toCreateCommand(request)` de la clase `UserDesktopMapper` para delegar la creación del comando. Se eliminaron los comentarios relativos a la violación, manteniendo la capa de entrada aislada de los detalles de estructuración interna del dominio.