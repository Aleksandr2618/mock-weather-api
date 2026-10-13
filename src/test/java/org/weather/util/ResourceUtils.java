package org.weather.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public final class ResourceUtils {

  private ResourceUtils() {}

  public static String readClasspath(String path) {
    try (InputStream in = ResourceUtils.class.getClassLoader().getResourceAsStream(path)) {
      if (in == null) {
        throw new IllegalStateException("Ресурс не найден в classpath: " + path);
      }

      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException("Не удалось прочитать ресурс: " + path, e);
    }
  }
}