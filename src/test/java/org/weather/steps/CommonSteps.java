package org.weather.steps;

import io.cucumber.java.ru.Тогда;
import org.weather.glue.ScenarioState;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

  private final ScenarioState state;

  public CommonSteps(ScenarioState state) {
    this.state = state;
  }

  @Тогда("HTTP статус ответа равен {int}")
  public void checkHttpStatus(int expectedStatus) {
    assertThat(state.getResponse().getStatusCode())
      .as("HTTP статус ответа")
      .isEqualTo(expectedStatus);
  }
}