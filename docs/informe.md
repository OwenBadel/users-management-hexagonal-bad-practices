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

## Regla 3: Un solo nivel de abstracción por función

### Violación 1
* **Archivo:** `src/main/java/com/jcaa/usersmanagement/application/service/CreateUserService.java`
* **Problema:** El método `execute` mezclaba lógica de orquestación (negocio) con detalles técnicos de construcción de objetos de dominio (instanciación de Value Objects).
* **Solución:** Se delegó la creación del `UserModel` al `UserApplicationMapper`. Esto permite que el servicio opere exclusivamente en un nivel de abstracción de alto nivel, cumpliendo con la separación de responsabilidades.
