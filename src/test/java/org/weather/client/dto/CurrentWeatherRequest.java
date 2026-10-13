package org.weather.client.dto;

import java.util.*;

public record CurrentWeatherRequest(String city, String apiKey, String aqi) {

  public static CurrentWeatherRequest of(String city, String apiKey) {
    return new CurrentWeatherRequest(city, apiKey, "no");
  }

  public static CurrentWeatherRequest withoutCity(String apiKey) {
    return new CurrentWeatherRequest(null, apiKey, "no");
  }

  public Map<String, String> queryParams() {
    Map<String, String> params = new LinkedHashMap<>();
    params.put("key", apiKey);

    if (city != null && !city.isBlank()) {
      params.put("q", city);
    }

    params.put("aqi", aqi);

    return params;
  }
}