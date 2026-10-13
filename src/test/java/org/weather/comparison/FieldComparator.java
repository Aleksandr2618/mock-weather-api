package org.weather.comparison;

import java.util.Objects;

public record FieldComparator(String field, Object expected, Object actual) {

  public boolean matched() {
    return valuesEqual(expected, actual);
  }

  private static boolean valuesEqual(Object expected, Object actual) {
    if (expected instanceof Number e && actual instanceof Number a) {
      return e.doubleValue() == a.doubleValue();
    }

    return Objects.equals(expected, actual);
  }
}