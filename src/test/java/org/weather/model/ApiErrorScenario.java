package org.weather.model;

import java.util.Arrays;

public enum ApiErrorScenario {

  MISSING_Q("missing_q", 400, 1003, "Parameter q is missing."),
  LOCATION_NOT_FOUND("location_not_found", 400, 1006, "No matching location found"),
  INVALID_API_KEY("invalid_api_key", 401, 2006, "API key provided is invalid"),
  QUOTA_EXCEEDED("quota_exceeded", 403, 2007, "API key has exceeded calls per month quota");

  private final String id;
  private final int httpStatus;
  private final int errorCode;
  private final String message;

  ApiErrorScenario(String id, int httpStatus, int errorCode, String message) {
    this.id = id;
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.message = message;
  }

  public static ApiErrorScenario byId(String id) {
    return Arrays.stream(values())
      .filter(scenario -> scenario.id.equals(id))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Неизвестный сценарий ошибки: " + id));
  }

  public String getId() {
    return id;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }
}