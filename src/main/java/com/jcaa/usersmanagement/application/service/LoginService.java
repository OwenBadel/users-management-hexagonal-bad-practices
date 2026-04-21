package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.LoginUseCase;
import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.service.dto.command.LoginCommand;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.exception.InvalidCredentialsException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public final class LoginService implements LoginUseCase {

  private final GetUserByEmailPort getUserByEmailPort;
  private final Validator validator;

  @Override
  public UserModel execute(final LoginCommand command) {
    validateCommand(command);

    final UserEmail email = new UserEmail(command.email());

    // Clean Code - Regla 8: (Lo arreglaremos en el Ciclo 3)
    final UserModel user = getAndValidateUser(email, command.password());

    return user;
  }

  private UserModel getAndValidateUser(final UserEmail email, final String plainPassword) {
    final UserModel user = getUserByEmailPort.getByEmail(email).orElse(null);

    if (user == null) {
      throw InvalidCredentialsException.becauseCredentialsAreInvalid();
    }

    // Clean Code - Regla 14: (Lo arreglaremos en el Ciclo 2)
    if (!user.getPassword().verifyPlain(plainPassword)) {
      throw InvalidCredentialsException.becauseCredentialsAreInvalid();
    }

    // SOLUCIÓN Regla 17 y 12: Se extrajo la condición compleja a un método
    // con nombre significativo, simplificando la redundancia booleana.
    ensureUserIsActive(user);

    return user;
  }

  private void validateCommand(final LoginCommand command) {
    final Set<ConstraintViolation<LoginCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  // Nuevo método para ocultar la complejidad de la validación del estado
  private void ensureUserIsActive(final UserModel user) {
    if (user.getStatus() != UserStatus.ACTIVE) {
      throw InvalidCredentialsException.becauseUserIsNotActive();
    }
  }
}