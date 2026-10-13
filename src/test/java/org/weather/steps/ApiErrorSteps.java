package org.weather.steps;

import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weather.client.WeatherApiClient;
import org.weather.client.dto.CurrentWeatherRequest;
import org.weather.data.CurrentWeatherRequestFactory;
import org.weather.glue.ScenarioState;
import org.weather.model.ApiErrorScenario;
import org.weather.wiremock.stubs.ApiErrorStubs;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiErrorSteps {

  private static final Logger LOG = LoggerFactory.getLogger(ApiErrorSteps.class);

  private final ScenarioState state;
  private final WeatherApiClient client;

  public ApiErrorSteps(ScenarioState state, WeatherApiClient client) {
    this.state = state;
    this.client = client;
  }

  @Дано("зарегистрирована заглушка ошибки {string}")
  public void registerErrorStub(String scenarioId) {
    ApiErrorScenario scenario = ApiErrorScenario.byId(scenarioId);
    CurrentWeatherRequest request = CurrentWeatherRequestFactory.forScenario(scenario);
    ApiErrorStubs.registerFor(request, scenario);

    state.setRequest(request);
  }

  @Когда("отправлен запрос текущей погоды")
  public void sendRequest() {
    state.setResponse(client.execute(state.getRequest()));
  }

  @Тогда("код ошибки в теле ответа равен {int}")
  public void checkErrorCode(int expectedErrorCode) {
    var errorResponse = client.parseError(state.getResponse());
    state.setError(errorResponse);

    assertThat(errorResponse.error())
      .as("Блок error в теле ответа")
      .isNotNull();

    assertThat(errorResponse.error().code())
      .as("Код ошибки (error.code)")
      .isEqualTo(expectedErrorCode);
  }

  @Тогда("сообщение об ошибке совпадает с ожидаемым {string}")
  public void checkErrorMessage(String expectedMessage) {
    assertThat(state.getError().error().message())
      .as("Сообщение об ошибке (error.message)")
      .isEqualTo(expectedMessage);
  }
}