package org.weather.wiremock.stubs;

import com.github.tomakehurst.wiremock.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weather.client.dto.*;
import org.weather.mapper.JsonMapper;
import org.weather.model.ApiErrorScenario;

public final class ApiErrorStubs {

  private static final Logger LOG = LoggerFactory.getLogger(ApiErrorStubs.class);

  private ApiErrorStubs() {}

  public static void registerFor(CurrentWeatherRequest request, ApiErrorScenario scenario) {
    MappingBuilder mapping = WireMock.get(WireMock.urlPathEqualTo(CurrentWeatherStubs.CURRENT_WEATHER_PATH))
      .withQueryParam("key", WireMock.equalTo(request.apiKey()));

    if (request.city() == null) {
      mapping.withQueryParam("q", WireMock.absent());
    } else {
      mapping.withQueryParam("q", WireMock.equalTo(request.city()));
    }

    mapping.withQueryParam("aqi", WireMock.matching("yes|no"));

    String errorBody = JsonMapper.write(new ApiErrorResponse(
      new ErrorDetail(scenario.getErrorCode(), scenario.getMessage())
    ));

    WireMock.stubFor(mapping.willReturn(WireMock.aResponse()
      .withStatus(scenario.getHttpStatus())
      .withHeader("Content-Type", "application/json")
      .withBody(errorBody)));

    LOG.info("Заглушка ошибки зарегистрирована: id='{}', HTTP={}, error.code={}",
      scenario.getId(), scenario.getHttpStatus(), scenario.getErrorCode());
  }
}