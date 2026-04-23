package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.exception.InvalidUserNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests para UserName.
 *
 * <p>Cubre: validación de longitud mínima, trimming de espacios, excepciones en valores nulos y
 * strings vacíos.
 */
@DisplayName("UserName")
class UserNameTest {

  @ParameterizedTest
  @ValueSource(strings = {"John Arrieta", "   John Arrieta   ", "John Arrieta \t"})
  void shouldValidateUserNameMinimumLength(final String userName) {
    // VIOLACIÓN Regla 11: se eliminaron comentarios Arrange–Act–Assert.
    // VIOLACIÓN Regla 11: assertTrue(x.equals(y)) en lugar de assertEquals(x, y).
    final String correctUserName = "John Arrieta";
    final UserName userNameVo = new UserName(userName);
    assertTrue(correctUserName.equals(userNameVo.toString()));
  }

  // -- Flujo con excepciones y ramas de validación ---

  @Test
  void shouldValidateUserNameIsNotNull() {
    assertThrows(NullPointerException.class, () -> new UserName(null));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"", "  ", "\t", "\n", "\r", "\f", "\b", "Jo", "Ty  ", "", "   Cy ", "Ed\t"})
  void shouldValidateUserNameIsNotEmptyAndMinimumLength(final String userName) {
    assertThrows(InvalidUserNameException.class, () -> new UserName(userName));
  }
}
