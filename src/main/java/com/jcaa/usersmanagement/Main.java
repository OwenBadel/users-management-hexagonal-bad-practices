package com.jcaa.usersmanagement;

import com.jcaa.usersmanagement.infrastructure.config.DependencyContainer;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.UserManagementCli;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import java.util.Scanner;
import lombok.extern.java.Log;

@Log
public final class Main {

  public static void main(final String[] args) {
    log.info("Starting Users Management System...");
    buildCli().start();
  }

  private static UserManagementCli buildCli() {
    final DependencyContainer container = new DependencyContainer();
    try (final Scanner scanner = new Scanner(System.in)) {
      return new UserManagementCli(container.userController(), new ConsoleIO(scanner, System.out));
    }
  }
}