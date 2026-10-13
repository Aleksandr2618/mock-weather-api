package org.weather.data;

import org.weather.client.dto.WeatherResponse;
import org.weather.util.ResourceUtils;

public record CityWeatherCase(String city, String stubResponseFile, WeatherResponse expected) {

  public String stubBody() {
    return ResourceUtils.readClasspath(stubResponseFile);
  }
}