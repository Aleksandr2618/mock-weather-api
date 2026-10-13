package org.weather.allure;

import io.qameta.allure.*;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.regex.Pattern;

public class AllureAttachmentFilter implements Filter {

  private static final Pattern API_KEY_IN_URL = Pattern.compile("(key=)([^&\\s]+)");

  @Override
  public Response filter(FilterableRequestSpecification requestSpec,
                         FilterableResponseSpecification responseSpec,
                         FilterContext context) {
    Response response = context.next(requestSpec, responseSpec);

    Allure.attachment(
      "HTTP запрос",
      "text/plain",
      "Метод: %s%nURL: %s%n".formatted(requestSpec.getMethod(), mask(requestSpec.getURI())),
      AttachmentOptions.withFileExtension(".txt")
    );

    Allure.attachment(
      "HTTP ответ",
      "text/plain",
      "Статус: %d%nТело:%n%s".formatted(
        response.getStatusCode(),
        response.getBody() == null ? "" : response.getBody().asString()
      ),
      AttachmentOptions.withFileExtension(".txt")
    );

    return response;
  }

  private String mask(String url) {
    return url == null ? null : API_KEY_IN_URL.matcher(url).replaceAll("$1***");
  }
}