package org.weather.wiremock.stubs;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CurrentWeatherStubs {

  public static final String CURRENT_WEATHER_PATH = "/v1/current.json";

  private static final Logger LOG = LoggerFactory.getLogger(CurrentWeatherStubs.class);

  private CurrentWeatherStubs() {}

  public static void registerSuccess(String city, String apiKey, String responseBody) {
    WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(CURRENT_WEATHER_PATH))
      .withQueryParam("key", WireMock.equalTo(apiKey))
      .withQueryParam("q", WireMock.equalTo(city))
      .withQueryParam("aqi", WireMock.matching("yes|no"))
      .atPriority(1)
      .willReturn(WireMock.okJson(responseBody)));

    LOG.info("Заглушка текущей погоды зарегистрирована: city='{}'", city);
  }
}