package org.weather.comparison;

import java.util.List;
import java.util.stream.Collectors;

public record ComparisonReport(List<FieldComparator> comparisons) {

  public ComparisonReport {
    comparisons = List.copyOf(comparisons);
  }

  public List<FieldComparator> mismatches() {
    return comparisons.stream()
      .filter(c -> !c.matched())
      .toList();
  }

  public String toPrettyString() {
    StringBuilder table = new StringBuilder("Сравнение значений ответа с ожидаемыми:\n\n");
    table.append(String.format("%-26s | %-18s | %-18s | %s%n", "Поле", "Ожидаемое", "Фактическое", "Статус"));

    for (FieldComparator c : comparisons) {
      table.append(
        String.format("%-26s | %-18s | %-18s | %s%n",
        c.field(), c.expected(), c.actual(), c.matched() ? "OK" : "MISMATCH")
      );
    }

    return table.toString();
  }

  public String mismatchesAsString() {
    return mismatches().stream()
      .map(c -> "%s: ожидалось='%s', фактически='%s'".formatted(c.field(), c.expected(), c.actual()))
      .collect(Collectors.joining("\n"));
  }
}