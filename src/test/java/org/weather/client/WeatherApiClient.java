package org.weather.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.weather.client.dto.*;
import org.weather.config.Config;
import org.weather.wiremock.WireMockServerHolder;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class WeatherApiClient {

  private static final String CURRENT_WEATHER_PATH = "/v1/current.json";

  private final RequestSpecification spec;

  public WeatherApiClient() {
    HttpClientFactory.configureJackson();
    this.spec = HttpClientFactory.wireMockSpec(WireMockServerHolder.baseUrl());
  }

  public Response execute(CurrentWeatherRequest request) {
    return given()
      .spec(spec)
      .queryParams(request.queryParams())

      .when()
        .get(CURRENT_WEATHER_PATH)

      .then()
        .extract()
        .response();
  }

  public WeatherResponse parseWeather(Response response) {
    return response.as(WeatherResponse.class);
  }

  public ApiErrorResponse parseError(Response response) {
    return response.as(ApiErrorResponse.class);
  }
}