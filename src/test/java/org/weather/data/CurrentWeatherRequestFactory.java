package org.weather.data;

import org.weather.client.dto.CurrentWeatherRequest;
import org.weather.config.Config;
import org.weather.model.ApiErrorScenario;

public final class CurrentWeatherRequestFactory {

  private CurrentWeatherRequestFactory() {}

  public static CurrentWeatherRequest forScenario(ApiErrorScenario scenario) {
    return switch (scenario) {
      case MISSING_Q -> CurrentWeatherRequest.withoutCity(Config.apiKey());
      case LOCATION_NOT_FOUND -> CurrentWeatherRequest.of("Unknown", Config.apiKey());
      case INVALID_API_KEY -> CurrentWeatherRequest.of("Moscow", Config.invalidApiKey());
      case QUOTA_EXCEEDED -> CurrentWeatherRequest.of("Krasnodar", Config.apiKey());
    };
  }
}