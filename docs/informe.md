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

## Regla 11: Pruebas (Calidad y Estructura)

### Violación 2
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/GetAllUsersServiceTest.java`
* **Problema:** La clase carecía de Javadoc descriptivo. Los tests carecían de la estructura estándar (Arrange-Act-Assert) y usaban aserciones obsoletas o imprecisas (`assertTrue(x == y)`, `assertTrue(result == null)`). Además, un test no tenía `@DisplayName` y validaba un comportamiento de negocio incorrecto (esperar `null` en lugar de una lista vacía).
* **Solución:** Se agregó el Javadoc a nivel de clase. Se estructuraron los cuerpos de los métodos con comentarios `// Arrange`, `// Act` y `// Assert`. Se actualizaron las aserciones a los métodos semánticamente correctos (`assertEquals`, `assertSame`, `assertNotNull`). Finalmente, se añadió `@DisplayName` al segundo test y se adaptó para validar el retorno de una colección vacía.

### Violación 3
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/UpdateUserServiceTest.java`
* **Problema:** El método `shouldUpdateUserAndNotifyWhenDataIsValid()` carecía de la estructura estándar Arrange-Act-Assert. El comentario de violación indicaba que los comentarios AAA habían sido eliminados, y el código mezclaba la configuración de mocks con la ejecución y validaciones sin separación clara.
* **Solución:** Se insertaron comentarios `// Arrange`, `// Act` y `// Assert` para dividir claramente el método en sus tres fases. Se reorganizó el código: primero se prepara el command y se configuran los mocks, luego se ejecuta el servicio, y finalmente se validan los resultados mediante `verify()`.

### Violación 4
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/UpdateUserServiceTest.java`
* **Problema:** El método `shouldThrowWhenUserNotFound()` no tenía la anotación `@DisplayName`, incumpliendo la directiva de Regla 11 que exige documentar claramente la intención de cada test con un nombre legible para reportes y ejecuciones con filtros.
* **Solución:** Se agregó la anotación `@DisplayName("execute() lanza UserNotFoundException cuando el usuario no existe")` al método, permitiendo que el framework JUnit y herramientas de reporting muestren una descripción clara del comportamiento esperado.

### Violación 5
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/EmailNotificationServiceTest.java`
* **Problema:** La clase test carecía de Javadoc descriptivo, haciendo que los lectores no supieran qué casos de uso o escenarios estaban siendo cubiertos por los tests.
* **Solución:** Se agregó el Javadoc de clase documentando los casos cubiertos: notificación exitosa de usuario creado y actualizado, validación del destinatario correcto, y manejo de excepciones del puerto de envío de email.

### Violación 6
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/EmailNotificationServiceTest.java`
* **Problema:** El método `shouldSendCreatedNotificationToCorrectEmail()` carecía de la anotación `@DisplayName`, incumpliendo la directiva de Regla 11.
* **Solución:** Se agregó la anotación `@DisplayName("notifyUserCreated() envía notificación al email correcto con asunto apropiado")`, documentando claramente el comportamiento esperado del test.

### Violación 7
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/EmailNotificationServiceTest.java`
* **Problema:** El método `shouldSendCreatedNotificationToCorrectEmail()` carecía de la estructura estándar Arrange-Act-Assert, mezclando la ejecución del servicio con la validación sin separación clara de fases.
* **Solución:** Se insertaron comentarios `// Arrange`, `// Act` y `// Assert` para dividir claramente el método. Se reorganizó el código manteniendo el setup implícito en Arrange y separando la invocación al servicio de las verificaciones.

### Violación 8
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/CreateUserServiceTest.java`
* **Problema:** La clase test carecía de la anotación `@DisplayName` a nivel de clase y los métodos no tenían @DisplayName, lo que hace que los reportes y ejecuciones filtradas muestren solo nombres técnicos sin documentación del comportamiento.
* **Solución:** Se agregó el Javadoc de clase documentando los casos cubiertos (creación exitosa, email duplicado, validación de command, notificación). Se agregó la anotación `@DisplayName("CreateUserService")` a la clase para proporcionar un nombre legible en reportes.

### Violación 9
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/CreateUserServiceTest.java`
* **Problema:** El método `shouldSaveUserAndNotifyWhenEmailIsNew()` no tenía la anotación `@DisplayName`, incumpliendo la directiva de Regla 11.
* **Solución:** Se agregó la anotación `@DisplayName("execute() guarda el usuario y notifica cuando el email es nuevo")` para documentar claramente el comportamiento esperado del test.

### Violación 10
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/CreateUserServiceTest.java`
* **Problema:** El método `shouldSaveUserAndNotifyWhenEmailIsNew()` mezclaba Arrange con Act y Assert sin separación, y usaba aserciones débiles como `assertTrue(result != null)` en lugar de `assertNotNull(result)` y `assertTrue(result.getId().value().equals("u-01"))` en lugar de `assertEquals`.
* **Solución:** Se insertaron comentarios `// Arrange`, `// Act` y `// Assert` para dividir claramente las fases. Se reemplazó `assertTrue(result != null)` por `assertNotNull(result)` y `assertTrue(x.equals(y))` por `assertEquals(x, y)`, utilizando aserciones más expresivas y correctas según JUnit 5.


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


## Regla 14: Ley de Deméter

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/LoginService.java`
* **Problema:** El método navegaba profundamente en la estructura interna de `UserModel` encadenando llamadas (`user.getPassword().verifyPlain(plainPassword)`). Esto crea un alto acoplamiento, ya que la capa de aplicación asume y conoce cómo el modelo estructura internamente sus *Value Objects*.
* **Solución:** Se aplicó la Ley de Deméter (o principio de menor conocimiento) delegando la validación directamente al objeto mediante `user.passwordMatches(plainPassword)`. Ahora el modelo encapsula su estado y la aplicación solo interactúa con su comportamiento expuesto.


## Regla 17: Condición booleana compleja

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/LoginService.java`
* **Problema:** El método de validación contenía una expresión booleana redundante y larga (`user.getStatus() != ACTIVE || user.getStatus() == BLOCKED...`) que dificultaba la lectura y la comprensión rápida de la intención.
* **Solución:** Se simplificó la lógica a una sola comprobación (`!= ACTIVE`) y se extrajo al método privado `ensureUserIsActive()`, mejorando drásticamente la legibilidad (Regla 17) y encapsulando parcialmente la regla de negocio (Regla 12).

## Regla 10: Eliminar comentarios redundantes y Magic Numbers

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/exception/InvalidCredentialsException.java`
* **Problema:** Los métodos factory contenían mensajes de error hardcodeados directamente como String literals, violando la Regla 10 que exige extraer valores especiales en constantes con nombres descriptivos.
* **Solución:** Se extrajeron los dos mensajes de error en constantes privadas estáticas (`INVALID_CREDENTIALS_MESSAGE` e `INACTIVE_USER_MESSAGE`), permitiendo una única fuente de verdad para estos textos y mejorando la mantenibilidad.

### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/exception/InvalidUserEmailException.java`
* **Problema:** Los métodos factory contenían mensajes de error hardcodeados directamente como String literals, incluido un String literal dentro de `String.format()`.
* **Solución:** Se extrajeron los mensajes en constantes privadas estáticas (`EMPTY_EMAIL_MESSAGE` e `INVALID_FORMAT_MESSAGE`), centralizando los textos de error y facilitando su mantenimiento.

### Violación 3
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/exception/InvalidUserIdException.java`
* **Problema:** El método factory contenía un mensaje de error hardcodeado directamente.
* **Solución:** Se extrajo el mensaje en la constante privada estática `EMPTY_ID_MESSAGE`, aplicando la misma pauta de centralización de textos de error.

### Violación 4
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/exception/InvalidUserNameException.java`
* **Problema:** Los dos métodos factory contenían mensajes de error hardcodeados directamente, incluido un String literal dentro de `String.format()`.
* **Solución:** Se extrajeron los mensajes en constantes privadas estáticas (`EMPTY_NAME_MESSAGE` y `TOO_SHORT_MESSAGE`), centralizando los textos y mejorando la mantenibilidad.

