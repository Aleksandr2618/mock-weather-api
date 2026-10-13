package org.weather.allure;

import org.weather.wiremock.WireMockServerHolder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AllureEnvironmentWriter {

  private AllureEnvironmentWriter() {}

  public static void write() {
    Path allureResults = Path.of("allure-results");

    try {
      Files.createDirectories(allureResults);

      Properties env = new Properties();
      env.setProperty("Service", "WeatherAPI.com (эмуляция через WireMock)");
      env.setProperty("WireMock base URL", WireMockServerHolder.baseUrl());
      env.setProperty("Java version", System.getProperty("java.version"));
      env.setProperty("api.key", "***скрыто***");

      try (OutputStream out = Files.newOutputStream(allureResults.resolve("environment.properties"))) {
        env.store(out, "Allure Environment");
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Не удалось записать environment.properties", e);
    }
  }
}