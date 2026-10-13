package org.weather.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {

  private static final Properties PROPERTIES = load();

  private Config() {}

  public static String apiKey() {
    return require("api.key");
  }

  public static String invalidApiKey() {
    return get("api.key.invalid", "invalid-api-key-for-tests");
  }

  public static String apiBaseUrl() {
    return get("api.base-url", "https://api.weatherapi.com");
  }

  public static int wireMockPort() {
    return Integer.parseInt(get("wiremock.port", "0"));
  }

  private static String require(String key) {
    String value = get(key, null);

    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Обязательный параметр конфигурации не задан: " + key);
    }

    return value;
  }

  private static String get(String key, String defaultValue) {
    String value = System.getProperty(key);

    if (value == null || value.isBlank()) {
      value = System.getenv(key.toUpperCase().replace('.', '_'));
    }

    if (value == null || value.isBlank()) {
      value = PROPERTIES.getProperty(key, defaultValue);
    }

    return value;
  }

  private static Properties load() {
    try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
      Properties props = new Properties();

      if (in != null) {
        props.load(in);
      }

      return props;
    } catch (IOException e) {
      throw new IllegalStateException("Не удалось загрузить config.properties", e);
    }
  }
}