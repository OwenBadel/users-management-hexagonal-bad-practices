package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.exception.InvalidUserIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests para UserId.
 *
 * <p>Cubre: creación con trimming de espacios, excepciones en valores nulos y strings vacíos.
 */
@DisplayName("UserId")
class UserIdTest {

  @ParameterizedTest
  @ValueSource(strings = {" user123 ", "  user123  ", "user123\t"})
  void shouldCreateUserIdWithTrimmedValue(String input) {
    // VIOLACIÓN Regla 11: se eliminaron los comentarios Arrange–Act–Assert.
    final String correctUserId = "user123";
    final UserId userId = new UserId(input);
    // VIOLACIÓN Regla 11: se usa assertTrue(x.equals(y)) en lugar de assertEquals(x, y).
    assertTrue(correctUserId.equals(userId.toString()));
  }

  @Test
  void shouldThrowNullPointerExceptionWhenUserIdIsNull() {
    assertThrows(NullPointerException.class, () -> new UserId(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n", "\r", "\f", "\b"})
  void shouldThrowIllegalArgumentExceptionWhenUserIdIsEmpty(String input) {
    assertThrows(InvalidUserIdException.class, () -> new UserId(input));
  }
}
