package org.weather.client;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.weather.allure.AllureAttachmentFilter;
import org.weather.mapper.JsonMapper;

public final class HttpClientFactory {

  private static volatile boolean jacksonConfigured = false;

  private HttpClientFactory() {}

  public static void configureJackson() {
    if (jacksonConfigured) {
      return;
    }

    synchronized (HttpClientFactory.class) {
      if (jacksonConfigured) {
        return;
      }

      RestAssured.config = RestAssuredConfig.config()
        .objectMapperConfig(
          new ObjectMapperConfig()
            .jackson2ObjectMapperFactory((type, charset) -> JsonMapper.MAPPER)
        );

      jacksonConfigured = true;
    }
  }

  public static RequestSpecification wireMockSpec(String baseUrl) {
    return RestAssured.given()
      .baseUri(baseUrl)
      .accept(ContentType.JSON)
      .filter(new AllureAttachmentFilter());
  }
}