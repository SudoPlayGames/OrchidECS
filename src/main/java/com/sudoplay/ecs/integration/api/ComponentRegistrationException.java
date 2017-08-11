package com.sudoplay.ecs.integration.api;

public class ComponentRegistrationException extends RuntimeException {

  public ComponentRegistrationException() {

  }

  public ComponentRegistrationException(String message) {

    super(message);
  }

  public ComponentRegistrationException(String message, Throwable cause) {

    super(message, cause);
  }

  public ComponentRegistrationException(Throwable cause) {

    super(cause);
  }

  public ComponentRegistrationException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace
  ) {

    super(message, cause, enableSuppression, writableStackTrace);
  }

}
