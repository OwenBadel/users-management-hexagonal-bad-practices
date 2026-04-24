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

### Violación 3
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/Main.java`
* **Problema:** La clase Main utilizaba `org.slf4j.Logger` (SLF4J), mientras que todo el resto del proyecto usa `java.util.logging.Logger` (via @Log de Lombok). El mismo concepto se resolvía con dos frameworks distintos sin justificación, violando la consistencia semántica.
* **Solución:** Se reemplazó SLF4J por `@Log` de Lombok (java.util.logging), manteniendo la consistencia con el estándar del proyecto y eliminando la dependencia innecesaria de SLF4J.


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

### Violación 11
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/GetUserByIdServiceTest.java`
* **Problema:** El método `shouldReturnUserWhenFound()` carecía de la estructura estándar Arrange-Act-Assert, y usaba aserciones débiles: `assertTrue(result != null)` en lugar de `assertNotNull(result)` y `assertTrue(result == expected)` en lugar de `assertSame(expected, result)`.
* **Solución:** Se insertaron comentarios `// Arrange`, `// Act` y `// Assert`. Se reemplazó `assertTrue(result != null)` por `assertNotNull(result)` y `assertTrue(result == expected)` por `assertSame(expected, result)`.

### Violación 12
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/GetUserByIdServiceTest.java`
* **Problema:** El método `shouldThrowWhenUserNotFound()` no tenía la anotación `@DisplayName`.
* **Solución:** Se agregó la anotación `@DisplayName("execute() lanza UserNotFoundException cuando el usuario no existe")`.

### Violación 13
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/LoginServiceTest.java`
* **Problema:** El método `shouldReturnUserWhenCredentialsAreValidAndUserIsActive()` carecía de estructura AAA y usaba aserciones débiles: `assertTrue(result != null)` y `assertTrue(result == activeUser)`.
* **Solución:** Se insertaron comentarios `// Arrange`, `// Act` y `// Assert`. Se reemplazó `assertTrue(result != null)` por `assertNotNull(result)` y `assertTrue(result == activeUser)` por `assertSame(activeUser, result)`.

### Violación 14
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/LoginServiceTest.java`
* **Problema:** El método `shouldThrowWhenEmailNotFound()` no tenía la anotación `@DisplayName`.
* **Solución:** Se agregó la anotación `@DisplayName("execute() lanza InvalidCredentialsException cuando el email no está registrado")`.

### Violación 15
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/domain/valueobject/UserIdTest.java`
* **Problema:** La clase test carecía de la anotación `@DisplayName` a nivel de clase y de Javadoc descriptivo.
* **Solución:** Se agregó el Javadoc de clase documentando los casos cubiertos (creación con trimming, excepciones en valores nulos y vacíos). Se agregó la anotación `@DisplayName("UserId")` a la clase.

### Violación 16
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/domain/valueobject/UserIdTest.java`
* **Problema:** El método parametrizado `shouldCreateUserIdWithTrimmedValue` carecía de estructura AAA y usaba `assertTrue(x.equals(y))` en lugar de `assertEquals(x, y)`.
* **Solución:** Se insertaron comentarios `// Arrange`, `// Act` y `// Assert`. Se reemplazó `assertTrue(correctUserId.equals(userId.toString()))` por `assertEquals(correctUserId, userId.toString())`.

### Violación 17
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/domain/valueobject/UserNameTest.java`
* **Problema:** La clase test carecía de la anotación `@DisplayName` a nivel de clase y de Javadoc descriptivo.
* **Solución:** Se agregó el Javadoc de clase documentando los casos cubiertos (validación de longitud mínima, trimming, excepciones). Se agregó la anotación `@DisplayName("UserName")`.

### Violación 18
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/domain/valueobject/UserNameTest.java`
* **Problema:** El método parametrizado `shouldValidateUserNameMinimumLength` carecía de estructura AAA y usaba `assertTrue(x.equals(y))` en lugar de `assertEquals(x, y)`.
* **Solución:** Se insertaron comentarios `// Arrange`, `// Act` y `// Assert`. Se reemplazó `assertTrue(correctUserName.equals(userNameVo.toString()))` por `assertEquals(correctUserName, userNameVo.toString())`.

### Violación 19
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/domain/valueobject/UserPasswordTest.java`
* **Problema:** La clase test carecía de la anotación `@DisplayName` a nivel de clase y de Javadoc descriptivo.
* **Solución:** Se agregó el Javadoc de clase documentando los casos cubiertos (normalización y hash, validación de longitud, validación de strings vacíos). Se agregó la anotación `@DisplayName("UserPassword")`.

### Violación 20
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/domain/valueobject/UserPasswordTest.java`
* **Problema:** El método parametrizado `shouldNormalizeAndHashPassword` carecía de `@DisplayName`, estructura AAA y usaba `assertTrue(result.value() != null)` en lugar de `assertNotNull(result.value())`.
* **Solución:** Se agregó la anotación `@DisplayName("Normaliza espacios en blanco y hashea la contraseña correctamente")`. Se insertaron comentarios `// Arrange`, `// Act` y `// Assert`. Se reemplazó `assertTrue(result.value() != null)` por `assertNotNull(result.value())`.

### Violación 21
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/infrastructure/adapter/persistence/config/DatabaseConnectionFactoryTest.java`
* **Problema:** La clase test carecía de Javadoc descriptivo documentando los casos cubiertos.
* **Solución:** Se agregó el Javadoc de clase documentando los casos: creación exitosa de conexión JDBC y manejo de SQLException con conversión a PersistenceException.

### Violación 22
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/infrastructure/adapter/persistence/mapper/UserPersistenceMapperTest.java`
* **Problema:** La clase test carecía de Javadoc descriptivo.
* **Solución:** Se agregó el Javadoc de clase documentando los casos cubiertos: mapeo bidireccional entre UserModel y UserEntity, mapeo desde ResultSet a UserModel, conversión de roles y estados.

### Violación 23
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/LoginServiceTest.java`
* **Problema:** El método `shouldThrowWhenPasswordIsWrong()` no tenía la anotación `@DisplayName`, incumpliendo la directiva de Regla 11 que exige documentar claramente la intención de cada test con un nombre legible para reportes y ejecuciones con filtros.
* **Solución:** Se agregó la anotación `@DisplayName("execute() lanza InvalidCredentialsException cuando la contraseña es incorrecta")` al método, permitiendo que el framework JUnit y herramientas de reporting muestren una descripción clara del comportamiento esperado.

### Violación 24
* **Archivo:** `src/test/java/com/jcaa/usersmanagement/application/service/CreateUserServiceTest.java`
* **Problema:** El método `shouldThrowWhenEmailAlreadyExists()` carecía de la anotación `@DisplayName` y tenía el patrón Arrange-Act-Assert mezclado sin separación mediante comentarios, dificultando la lectura de las tres fases del test.
* **Solución:** Se agregó la anotación `@DisplayName("execute() lanza UserAlreadyExistsException cuando el email ya existe")` y se insertaron comentarios `// Arrange` y `// Act & Assert` para dividir claramente las fases del test, mejorando la estructura y legibilidad.

## Regla 17: Condición booleana compleja

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/LoginService.java`
* **Problema:** El método de validación contenía una expresión booleana redundante y larga (`user.getStatus() != ACTIVE || user.getStatus() == BLOCKED...`) que dificultaba la lectura y la comprensión rápida de la intención.
* **Solución:** Se simplificó la lógica a una sola comprobación (`!= ACTIVE`) y se extrajo al método privado `ensureUserIsActive()`, mejorando drásticamente la legibilidad (Regla 17) y encapsulando parcialmente la regla de negocio (Regla 12).

### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/UpdateUserService.java`
* **Problema:** El método `ensureEmailIsNotTakenByAnotherUser()` contenía una condición booleana monumental e ilegible que llamaba al mismo repositorio 5 veces con lógica redundante y difícil de comprender: (A && B && C) || (A && D) donde A = getByEmail().isPresent().
* **Solución:** Se simplificó mediante Optional.ifPresent() con una lógica clara de una sola llamada al repositorio: primero obtener el usuario existente, luego verificar si pertenece a otro owner. Esto es más legible, eficiente y expresa claramente la intención: "si el email ya existe y no es de este usuario, fallar".

## Regla 8: Argumentos de función

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/LoginService.java`
* **Problema:** El método `getAndValidateUser()` usaba `Optional.orElse(null)` para obtener el usuario, violando Regla 5 (no retornar null) y creando una validación nula innecesaria: si(user == null) lanzar excepción.
* **Solución:** Se refactorizo para usar `Optional.orElseThrow()`, eliminando la variable nula y la validación redundante. Ahora si el usuario no existe, directamente se lanza la excepción, haciendo el código más limpio y eliminando el anti-patrón de null.


## Regla 22: Código difícil de refactorizar

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/Main.java`
* **Problema:** El método `main()` estaba acoplado directamente a clases concretas (DependencyContainer, UserManagementCli, ConsoleIO). Si se quisiera cambiar el entrypoint (de CLI a GUI), habría que editar el punto de entrada de la aplicación. No había abstracción que protegiera este acoplamiento.
* **Solución:** Se refactorizó para extraer la lógica de construcción del CLI a `buildCli()`, encapsulando el acoplamiento en un método específico y dejando `main()` libre de detalles de implementación. Ahora si se cambia el tipo de CLI, solo buildCli() necesita modificarse.

### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/config/DependencyContainer.java`
* **Problema:** El contenedor dependía directamente del tipo concreto `UserRepositoryMySQL` en el ensamblaje de casos de uso, propagando el conocimiento de la implementación concreta y dificultando reemplazar el adaptador de persistencia sin tocar varias líneas del cableado.
* **Solución:** Se introdujo un ensamblado intermedio tipado por puertos (`UserRepositoryPorts`) y se movió la creación del repositorio concreto al método `buildUserRepositoryPorts(...)`. El wiring de servicios ahora consume únicamente interfaces de puertos, reduciendo el acoplamiento a concreciones y mejorando la refactorabilidad.


## Regla 6: Evitar parámetros booleanos de control (Clean Code)

### Violación 1
* **Archivo:** `EmailNotificationService.java`
* **Problema:** El método `sendNotificationWithFlag` utilizaba un booleano (`includePassword`) para bifurcar el flujo de ejecución entre dos comportamientos distintos (creación vs actualización).
* **Solución:** Se eliminó el método con el "Flag Argument". Ahora los consumidores de la clase deben invocar explícitamente `notifyUserCreated` o `notifyUserUpdated`, eliminando efectos secundarios y mejorando la claridad de la API.


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

### Violación 5
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/exception/InvalidUserRoleException.java`
* **Problema:** El método factory contenía un mensaje de error hardcodeado directamente dentro de `String.format()`.
* **Solución:** Se extrajo el mensaje en la constante privada estática `INVALID_ROLE_MESSAGE`.

### Violación 6
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/exception/InvalidUserStatusException.java`
* **Problema:** El método factory contenía un mensaje de error hardcodeado directamente dentro de `String.format()`.
* **Solución:** Se extrajo el mensaje en la constante privada estática `INVALID_STATUS_MESSAGE`.

### Violación 7
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/exception/UserAlreadyExistsException.java`
* **Problema:** El método factory contenía un mensaje de error hardcodeado directamente dentro de `String.format()`.
* **Solución:** Se extrajo el mensaje en la constante privada estática `EMAIL_EXISTS_MESSAGE`.

### Violación 8
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/exception/UserNotFoundException.java`
* **Problema:** El método factory contenía un mensaje de error hardcodeado directamente dentro de `String.format()`.
* **Solución:** Se extrajo el mensaje en la constante privada estática `USER_NOT_FOUND_MESSAGE`.

### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/model/EmailDestinationModel.java`
* **Problema:** El constructor utilizaba cuatro mensajes de error hardcodeados directamente como String literals al validar parámetros, sin centralizar estos valores en constantes nombradas.
* **Solución:** Se extrajeron los cuatro mensajes de error en constantes privadas estáticas (`DESTINATION_EMAIL_REQUIRED_MESSAGE`, `DESTINATION_NAME_REQUIRED_MESSAGE`, `SUBJECT_REQUIRED_MESSAGE`, `BODY_REQUIRED_MESSAGE`), centralizando los valores en un único lugar para facilitar cambios futuros.


## Regla 4: Estilo y Naming

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/model/EmailDestinationModel.java`
* **Problema:** El método `validateNotBlank` utilizaba el operador `==` para comparar con `null` en lugar de usar `Objects.isNull()`, violando la directiva de Regla 4 que exige usar métodos de utilidad de Objects para comparaciones nulas.
* **Solución:** Se agregó el import de `java.util.Objects` y se reemplazó `value == null` por `Objects.isNull(value)`, mejorando la claridad y consistencia del código.

### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/config/AppProperties.java`
* **Problema:** El método `doLoad` utilizaba el operador `==` para comparar el stream con `null` en lugar de usar `Objects.isNull()`.
* **Solución:** Se agregó el import de `java.util.Objects` y se reemplazó `stream == null` por `Objects.isNull(stream)`.

### Violación 3
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/config/AppProperties.java`
* **Problema:** La variable local en `doLoad` utilizaba la abreviatura `props` en lugar del nombre completo y descriptivo `properties`.
* **Solución:** Se renombró la variable de `props` a `properties` para mejorar la claridad y cumplir con la directiva de Regla 4 que exige nombres descriptivos sin abreviaturas.

### Violación 4
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/config/AppProperties.java`
* **Problema:** El método `get` utilizaba la abreviatura `val` para la variable local y el operador `==` para comparar con `null`.
* **Solución:** Se renombró la variable de `val` a `value` y se reemplazó `val == null` por `Objects.isNull(value)`, mejorando tanto la claridad de nombres como el uso de métodos de utilidad.


### Violación 5
* **Archivo:** `EmailNotificationService.java`
* **Problema:** El método privado `renderTemplate` actuaba como una función pura que no utilizaba el estado de la instancia, pero no estaba marcado como `static`.
* **Solución:** Se añadió el modificador `static` a `renderTemplate` para indicar claramente su independencia del estado de la clase y adherirse a las convenciones de Clean Code en Java.


## Regla 9: Separación de responsabilidades en mapeos (Hexagonal)

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/controller/UserController.java`
* **Problema:** El método `login()` construía directamente la `LoginCommand` sin utilizar el mapper `UserDesktopMapper.toLoginCommand()`, violando la separación de responsabilidades de la arquitectura hexagonal. Todos los otros métodos del controlador utilizaban el mapper para construir sus comandos, pero este método lo hacía directamente.
* **Solución:** Se reemplazó `new LoginCommand(request.email(), request.password())` por `UserDesktopMapper.toLoginCommand(request)`, delegando la transformación de datos al mapper y manteniendo la consistencia arquitectónica en toda la clase.

## Regla 5: No retornar null

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/cli/io/UserResponsePrinter.java`
* **Problema:** El método `printList()` llamaba directamente a `users.isEmpty()` sin verificar si `users` era nulo primero. Si `GetAllUsersService` retornaba `null` (violando también Regla 5 en esa capa), este método lanzaría `NullPointerException`. El código no era defensivo contra violaciones de contrato en capas precedentes.
* **Solución:** Se agregó una verificación nula: `if (users == null || users.isEmpty())`, haciendo el código más defensivo y evitando NPE. Aunque idealmente GetAllUsersService no debería retornar null, ahora printList es resiliente a esa violación.


### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/adapter/persistence/mapper/UserPersistenceMapper.java`
* **Problema:** La clase `UserPersistenceMapper` contenía solo métodos públicos de conversión sin estado de instancia, pero NO estaba anotada con `@UtilityClass` de Lombok. Esto permitía que se instanciara accidentalmente, violando el patrón de clase utilitaria. Además, los métodos no eran `static`, aumentando la confusión sobre cómo usarla.
* **Solución:** Se agregó la anotación `@UtilityClass` de Lombok a la clase (con el import correspondiente) y se convirtieron todos los métodos públicos a `static`. Además, se eliminó la instanciación innecesaria del mapper en `UserRepositoryMySQL` y en `UserPersistenceMapperTest`, reemplazando todos los llamados con invocaciones estáticas `UserPersistenceMapper.metodo()`.


## Regla 9: Factory methods para excepciones (Clean Code)

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/exception/EmailSenderException.java`
* **Problema:** La clase `EmailSenderException` exponía constructores públicos, permitiendo que cualquier código creara excepciones con mensajes arbitrarios sin restricción. Esto violaba el control sobre cómo se instancia la excepción y hacía que el contrato de mensajes de error fuera frágil.
* **Solución:** Se convirtieron ambos constructores públicos a privados. Ahora todas las instancias deben crearse a través de los factory methods (`becauseSmtpFailed()` y `becauseSendFailed()`), centralizando el control sobre los mensajes de error permitidos y mejorando la cohesión de la clase.


### Violación 7
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/cli/handler/UpdateUserHandler.java`
* **Problema:** El método `handle()` utilizaba variables con nombres abreviados no descriptivos: `pw` en lugar de `password` y `upd` en lugar de `updated`. Estas abreviaturas reducen la legibilidad y obligan al lector a descifrar su significado.
* **Solución:** Se renombraron las variables a sus formas completas y descriptivas: `password` y `updated`, mejorando la claridad del código sin sacrificar concisión.


## Regla 6: Evitar logging de PII (Personally Identifiable Information)

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/cli/handler/LoginHandler.java`
* **Problema:** El método `handle()` registraba el email del usuario (PII) en un log de warning cuando el login fallaba: `log.warning("Intento de login fallido para email: " + email)`. Los datos personales de negocio nunca deben exponerse en logs.
* **Solución:** Se eliminó completamente la línea de logging. La excepción se captura y se muestra al usuario mediante la consola, lo cual es suficiente sin comprometer datos sensibles en los registros de log del sistema.

### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/cli/handler/CreateUserHandler.java`
* **Problema:** Combinaba dos violaciones: (Regla 4) Logger instanciado manualmente como `Logger.getLogger(...)` en lugar de usar `@Log` de Lombok, y (Regla 6) registraba el mensaje de excepción que contenía PII (el email del usuario duplicado).
* **Solución:** Se reemplazó el Logger manual por la anotación `@Log` de Lombok y se eliminó la línea `LOGGER.warning("Usuario ya existe: " + exception.getMessage())` que exponía datos sensibles. El error ahora se comunica solo al usuario mediante consola, sin comprometer privacidad en los logs del sistema.


## Regla 10: Eliminar números mágicos y strings hardcodeados

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/cli/UserManagementCli.java`
* **Problema:** El método `printMenu()` contenía tres instancias hardcodeadas del patrón de borde `"  =========================================="` en lugar de reutilizar la constante `MENU_BORDER` que ya estaba definida a nivel de clase.
* **Solución:** Se reemplazaron todas las instancias hardcodeadas del borde por referencias a la constante `MENU_BORDER`, centralizando el valor en un único lugar y facilitando futuros cambios de formato.

### Violación 3
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/cli/UserManagementCli.java`
* **Problema:** El mismo método utilizaba la variable abreviada `opt` en lugar del nombre completo `option` en el bucle `for`. Esta abreviatura reduce la legibilidad y hace que el código sea menos autodocumentado.
* **Solución:** Se renombró `opt` a `option` para mejorar la claridad y descriptividad del nombre de variable.


### Violación 4
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/config/ConfigurationException.java`
* **Problema:** El método factory `becauseLoadFailed()` contenía un mensaje de error hardcodeado directamente como String literal: `"Failed to load the application configuration."`.
* **Solución:** Se extrajo el mensaje en la constante privada estática `LOAD_FAILED_MESSAGE`, centralizando el valor en un único lugar.


### Violación 8
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/adapter/persistence/config/DatabaseConnectionFactory.java`
* **Problema:** La clase `DatabaseConnectionFactory` contenía solo un método público que no utilizaba estado de instancia, pero NO estaba anotada con `@UtilityClass` de Lombok y el método NO era `static`. Esto permitía instanciación innecesaria.
* **Solución:** Se agregó la anotación `@UtilityClass` y se convirtió `createConnection()` a `static`. Se eliminó la instanciación en `DependencyContainer` y en `DatabaseConnectionFactoryTest`, reemplazando con invocaciones estáticas.

### Violación 9
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/adapter/persistence/exception/PersistenceException.java`
* **Problema:** Los métodos factory contenían mensajes de error hardcodeados directamente como String literals.
* **Solución:** Se extrajeron los mensajes en constantes privadas estáticas con nombres descriptivos.


## Regla 1: Una sola cosa por función

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/Main.java`
* **Problema:** El método `main()` realizaba múltiples responsabilidades: construir el contenedor de dependencias, crear la infraestructura de I/O (Scanner), instanciar el CLI y arrancarlo. Esto violaba el principio de responsabilidad única.
* **Solución:** Se extrajo la construcción del CLI al método privado `buildCli()`, separando claramente las responsabilidades y permitiendo que `main()` sea una orquestación simple de alto nivel.

### Violación 3
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/adapter/persistence/repository/UserRepositoryMySQL.java`
* **Problema:** El método `save()` contenía tres comentarios redundantes que simplemente repetían lo obvio del código ("transformar el modelo...", "ejecutar la consulta...", "buscar y retornar...").
* **Solución:** Se eliminaron todos los comentarios redundantes, dejando que el código y los nombres de método autodocumentado (`fromModelToDto`, `executeSave`, `findByIdOrFail`) comuniquen la intención de forma clara.

### Violación 14
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/adapter/persistence/repository/UserRepositoryMySQL.java`
* **Problema:** El método `saveWithFields()` contenía dos comentarios redundantes ("verificar que todos los parámetros...", "construir y guardar el modelo") que repetían lo obvio del código.
* **Solución:** Se eliminaron los comentarios redundantes. El código ahora se explica por sí mismo mediante nombres claros (validación nula explícita, excepción descriptiva).


## Regla 19: Evitar temporal coupling

### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/config/DependencyContainer.java`
* **Problema:** La construcción de dependencias exigía un orden implícito y frágil (`init()` antes de usar el repositorio). Este acoplamiento temporal no estaba protegido por el diseño y facilitaba usos incorrectos al depender de pasos manuales en secuencia.
* **Solución:** Se eliminó la llamada explícita a `userRepository.init()` junto con su comentario de violación asociado a Regla 19. Con esto, el flujo de inicialización deja de depender de un paso extra de orden obligatorio en el contenedor y queda más robusto frente a errores de uso.

### Violación 3
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/adapter/persistence/repository/UserRepositoryMySQL.java`
* **Problema:** La clase exponía un estado y una API de inicialización (`initialized` e `init()`) que imponían un orden implícito de uso, generando acoplamiento temporal innecesario y un contrato frágil para consumidores.
* **Solución:** Se eliminaron el flag `initialized`, el método `init()` y sus comentarios de violación asociados. El repositorio queda sin pasos previos obligatorios de inicialización manual, reduciendo el acoplamiento temporal y simplificando su uso.


## Regla 27: Código listo para leer

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/cli/io/UserResponsePrinter.java`
* **Problema:** El método `printSummary()` usaba una combinación innecesariamente compleja de `Optional`, `stream`, `reduce` y conversiones intermedias para una tarea simple (imprimir resumen o mensaje de vacío), dificultando la lectura y el mantenimiento.
* **Solución:** Se reescribió `printSummary()` con flujo imperativo claro: validación temprana de lista vacía, construcción explícita del resumen con `StringBuilder` y salida final por consola. También se eliminó la importación de `Optional` y el comentario de violación para dejar el código limpio.


## Regla 5: Pocos parámetros por función

### Violación 2
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/adapter/persistence/repository/UserRepositoryMySQL.java`
* **Problema:** La clase exponía un método alternativo `saveWithFields(...)` con múltiples parámetros primitivos relacionados, lo que rompe cohesión del contrato y dificulta evolución y validación del modelo.
* **Solución:** Se eliminó `saveWithFields(...)` junto con su comentario de violación. El repositorio conserva como API de persistencia el método `save(UserModel)`, que encapsula correctamente los datos del usuario en un único objeto de dominio.


## Regla 16: Evitar condicionales repetitivas

### Violación 3
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/infrastructure/entrypoint/desktop/cli/io/UserResponsePrinter.java`
* **Problema:** El archivo mantenía comentarios de violación de Regla 16 que describían una cadena condicional extensa, aunque la implementación real ya estaba resuelta con un `Map` de etiquetas. Esta desalineación entre comentario y código introduce ruido y confusión sobre el estado real de la regla.
* **Solución:** Se eliminaron los comentarios de violación asociados a Regla 16, dejando el código limpio y consistente con la implementación actual basada en `STATUS_LABELS`.


