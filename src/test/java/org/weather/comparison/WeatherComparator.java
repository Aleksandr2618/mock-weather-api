package org.weather.comparison;

import org.weather.client.dto.WeatherResponse;

import java.lang.reflect.*;
import java.util.*;

public final class WeatherComparator {

  private WeatherComparator() {}

  public static ComparisonReport compare(WeatherResponse actual, WeatherResponse expected) {
    List<FieldComparator> comparisons = new ArrayList<>();

    collect("location", actual.location(), expected.location(), comparisons);
    collect("current", actual.current(), expected.current(), comparisons);

    return new ComparisonReport(comparisons);
  }

  private static void collect(String prefix, Object actual, Object expected, List<FieldComparator> out) {
    if (expected == null) {
      return;
    }

    for (RecordComponent component : expected.getClass().getRecordComponents()) {
      Object expectedValue = read(component, expected);

      if (expectedValue == null) {
        continue;
      }

      String path = prefix + "." + toSnakeCase(component.getName());
      Object actualValue = actual == null ? null : read(component, actual);

      if (expectedValue instanceof Record) {
        collect(path, actualValue, expectedValue, out);
      } else {
        out.add(new FieldComparator(path, expectedValue, actualValue));
      }
    }
  }

  private static Object read(RecordComponent component, Object target) {
    try {
      return component.getAccessor().invoke(target);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException("Не удалось прочитать поле " + component.getName(), e);
    }
  }

  private static String toSnakeCase(String camelCase) {
    return camelCase.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
  }
}