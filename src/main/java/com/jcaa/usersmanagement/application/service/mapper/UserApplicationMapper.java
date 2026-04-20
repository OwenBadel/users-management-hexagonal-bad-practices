package com.jcaa.usersmanagement.application.service.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.CreateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetUserByIdQuery;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import java.util.Objects;

public class UserApplicationMapper {

  public static UserModel fromCreateCommandToModel(final CreateUserCommand command) {
    final String userId    = command.id();
    final String userName  = command.name();
    final String userEmail = command.email();
    final String userPass  = command.password();
    final String userRole  = command.role();

    return UserModel.create(
        new UserId(userId),
        new UserName(userName),
        new UserEmail(userEmail),
        UserPassword.fromPlainText(userPass),
        UserRole.fromString(userRole));
  }

  public static UserModel fromUpdateCommandToModel(
      final UpdateUserCommand command, final UserPassword currentPassword) {

    UserPassword passwordToUse;
    if (command.password() == null || command.password().isBlank()) {
      passwordToUse = currentPassword;
    } else {
      passwordToUse = UserPassword.fromPlainText(command.password());
    }

    final String userEmail = command.email();

    return new UserModel(
        new UserId(command.id()),
        new UserName(command.name()),
        new UserEmail(userEmail),
        passwordToUse,
        UserRole.fromString(command.role()),
        UserStatus.fromString(command.status()));
  }

  public static UserId fromGetUserByIdQueryToUserId(final GetUserByIdQuery query) {
    return new UserId(query.id());
  }

  public static UserId fromDeleteCommandToUserId(final DeleteUserCommand command) {
    return new UserId(command.id());
  }

  public static int roleToCode(final String role) {
    if (Objects.isNull(role) || role.isBlank()) {
      return -1;
    }
    if ("ADMIN".equalsIgnoreCase(role)) {
      return 1;
    } else if ("MEMBER".equalsIgnoreCase(role)) {
      return 2;
    } else if ("REVIEWER".equalsIgnoreCase(role)) {
      return 3;
    }
    return -1;
  }
}