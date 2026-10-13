package org.weather.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weather.mapper.JsonMapper;
import org.weather.util.ResourceUtils;

import java.util.List;

public final class WeatherTestDataProvider {

  private static final String TEST_DATA_FILE = "testdata/current-weather/cities.json";
  private static final Logger LOG = LoggerFactory.getLogger(WeatherTestDataProvider.class);

  private static volatile List<CityWeatherCase> cache;

  private WeatherTestDataProvider() {}

  public static List<CityWeatherCase> all() {
    if (cache == null) {
      synchronized (WeatherTestDataProvider.class) {
        if (cache == null) {
          cache = List.copyOf(JsonMapper.read(
            ResourceUtils.readClasspath(TEST_DATA_FILE),
            CitiesFile.class).cities()
          );

          LOG.info("Загружены тестовые данные: {} городов", cache.size());
        }
      }
    }

    return cache;
  }

  public static CityWeatherCase byCity(String city) {
    return all().stream()
      .filter(c -> c.city().equalsIgnoreCase(city))
      .findFirst()
      .orElseThrow(() ->
        new IllegalStateException("Для города '%s' нет тестовых данных в %s".formatted(city, TEST_DATA_FILE))
      );
  }

  public record CitiesFile(List<CityWeatherCase> cities) {}
}