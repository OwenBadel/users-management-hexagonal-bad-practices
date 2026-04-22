# Informe de Refactorización - Violaciones Corregidas

---

## Regla 9: Arquitectura Hexagonal — Dependencias hacia el centro

### Violación 2

* **Archivo:** `src/main/java/com/jcaa/usersmanagement/domain/model/UserModel.java`
* **Problema:** El dominio importaba directamente la clase de infraestructura `UserEntity` (línea 11) y exponía un método público `toEntity()` (líneas 51-63) que convertía el modelo de dominio a la entidad de persistencia. Esto viola el principio fundamental de Arquitectura Hexagonal donde las dependencias **siempre deben ir hacia el centro** (dominio). El dominio no debe conocer cómo ni en qué formato se persisten sus datos; ese conocimiento pertenece exclusivamente a los adapters de infraestructura.
* **Solución:** Se removieron: (1) el import de `UserEntity` ya que no es usado en ninguna parte legítima del dominio, y (2) el método `toEntity()` que era código muerto sin referencias en la base de código. Esta acción restaura el aislamiento del dominio: ahora es completamente independiente de detalles técnicos de persistencia. La conversión de `UserModel` a formato de persistencia ocurre únicamente en `UserPersistenceMapper` (capa de adapters), donde corresponde arquitectónicamente.

---
