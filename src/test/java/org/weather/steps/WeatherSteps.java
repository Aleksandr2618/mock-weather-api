package org.weather.steps;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weather.client.WeatherApiClient;
import org.weather.client.dto.*;
import org.weather.comparison.*;
import org.weather.config.Config;
import org.weather.data.*;
import org.weather.glue.ScenarioState;
import org.weather.wiremock.stubs.CurrentWeatherStubs;

import static org.assertj.core.api.Assertions.assertThat;

public class WeatherSteps {

  private static final Logger LOG = LoggerFactory.getLogger(WeatherSteps.class);

  private final ScenarioState state;
  private final WeatherApiClient client;

  public WeatherSteps(ScenarioState state, WeatherApiClient client) {
    this.state = state;
    this.client = client;
  }

  @Дано("зарегистрирована заглушка успешного ответа для города {string}")
  public void registerSuccessStub(String city) {
    CityWeatherCase data = WeatherTestDataProvider.byCity(city);

    CurrentWeatherStubs.registerSuccess(city, Config.apiKey(), data.stubBody());
  }

  @Когда("отправлен запрос текущей погоды для города {string}")
  public void sendCurrentWeatherRequest(String city) {
    state.setCity(city);

    state.setResponse(client.execute(CurrentWeatherRequest.of(city, Config.apiKey())));
  }

  @Тогда("ответ успешно разобран в модель WeatherResponse")
  public void parseResponse() {
    WeatherResponse weather = client.parseWeather(state.getResponse());

    state.setWeather(weather);

    LOG.info("Ответ разобран: city='{}', temp_c={}, humidity={}",
      weather.location().name(), weather.current().tempC(), weather.current().humidity());
  }

  @Тогда("имя города в ответе совпадает с запрошенным")
  public void checkCityName() {
    assertThat(state.getWeather().location())
      .as("Блок location в разобранном ответе")
      .isNotNull();

    assertThat(state.getWeather().location().name())
      .as("Имя города в ответе")
      .isEqualTo(state.getCity());
  }

  @Тогда("запрос к заглушке выполнен ровно один раз")
  public void verifySingleRequest() {
    WireMock.verify(
      1,
      WireMock.requestedFor("GET", WireMock.urlPathEqualTo(CurrentWeatherStubs.CURRENT_WEATHER_PATH))
        .withQueryParam("q", WireMock.equalTo(state.getCity()))
        .withQueryParam("key", WireMock.equalTo(Config.apiKey()))
    );

    LOG.info("Подтверждён ровно один запрос к заглушке для города '{}'", state.getCity());
  }

  @Тогда("значения полей ответа совпадают с ожидаемыми из тестовых данных")
  public void checkFieldsAgainstExpected() {
    CityWeatherCase data = WeatherTestDataProvider.byCity(state.getCity());
    ComparisonReport report = WeatherComparator.compare(state.getWeather(), data.expected());

    Allure.attachment(
      "Сравнение значений ответа с ожидаемыми",
      "text/plain",
      report.toPrettyString(),
      AttachmentOptions.withFileExtension(".txt")
    );

    String mismatches = report.mismatchesAsString();
    if (!mismatches.isEmpty()) {
      LOG.error("Найдены следующие расхождения в полях:\n{}", mismatches);
    }

    SoftAssertions softly = new SoftAssertions();

    report.comparisons()
      .forEach(comparison -> softly
        .assertThat(comparison.actual())
        .as("Поле %s", comparison.field())
        .isEqualTo(comparison.expected()));

    softly.assertAll();
  }

  @Тогда("в лог выведены расхождения по результатам сравнения")
  public void logMismatches() {
    CityWeatherCase data = WeatherTestDataProvider.byCity(state.getCity());
    ComparisonReport report = WeatherComparator.compare(state.getWeather(), data.expected());

    String mismatches = report.mismatchesAsString();

    if (mismatches.isEmpty()) {
      LOG.info("[СРАВНЕНИЕ] Расхождений не обнаружено, все поля совпали.");
    } else {
      LOG.warn("[СРАВНЕНИЕ] Обнаружены расхождения:\n{}", mismatches);
    }
  }

}