package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.CreateUserUseCase;
import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.port.out.SaveUserPort;
import com.jcaa.usersmanagement.application.service.dto.command.CreateUserCommand;
import com.jcaa.usersmanagement.application.service.mapper.UserApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.UserAlreadyExistsException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.Set;

@Log
@RequiredArgsConstructor
public final class CreateUserService implements CreateUserUseCase {

    private final SaveUserPort saveUserPort;
    private final GetUserByEmailPort getUserByEmailPort;
    private final EmailNotificationService emailNotificationService;
    private final Validator validator;

    @Override
    public UserModel execute(final CreateUserCommand command) {
        validateCommand(command);
        
        log.info("Iniciando proceso de creación de usuario");
        
        ensureEmailIsNotRegistered(command.email());

        return createAndNotify(command);
    }

    private UserModel createAndNotify(CreateUserCommand command) {
        final UserModel userToSave = UserApplicationMapper.fromCreateCommandToModel(command);
        final UserModel savedUser = saveUserPort.save(userToSave);
        
        emailNotificationService.notifyUserCreated(savedUser, command.password());
        
        return savedUser;
    }

    private void validateCommand(final CreateUserCommand command) {
        final Set<ConstraintViolation<CreateUserCommand>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private void ensureEmailIsNotRegistered(final String emailStr) {
        final UserEmail email = new UserEmail(emailStr);
        if (getUserByEmailPort.getByEmail(email).isPresent()) {
            throw UserAlreadyExistsException.becauseEmailAlreadyExists(email.value());
        }
    }
}