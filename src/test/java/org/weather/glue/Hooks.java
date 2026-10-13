package org.weather.glue;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.NearMiss;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weather.allure.AllureEnvironmentWriter;
import org.weather.wiremock.WireMockServerHolder;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.fail;

public class Hooks {

  private static final Logger LOG = LoggerFactory.getLogger(Hooks.class);

  @BeforeAll
  public static void setUpSuite() {
    WireMockServerHolder.startIfNeeded();
    AllureEnvironmentWriter.write();
  }

  @Before(order = 0)
  public void prepareWireMock() {
    WireMockServerHolder.reset();
  }

  @After
  public void finalizeScenario(Scenario scenario) {
    LOG.info("Сценарий завершён: '{}' — {}", scenario.getName(), scenario.getStatus());

    if (scenario.isFailed()) {
      attachWireMockJournal();
    }

    List<NearMiss> nearMisses = WireMock.findNearMissesForAllUnmatched();

    if (!nearMisses.isEmpty()) {
      String details = nearMisses.stream()
        .map(String::valueOf)
        .collect(Collectors.joining("\n\n"));

      LOG.error("Обнаружены запросы без подходящей заглушки:\n{}", details);

      Allure.attachment(
        "WireMock: запросы без подходящей заглушки",
        "text/plain",
        details,
        AttachmentOptions.withFileExtension(".txt")
      );

      fail("Найдены запросы, на которые не зарегистрирована заглушка:\n" + details);
    }
  }

  private void attachWireMockJournal() {
    String journal = WireMock.getAllServeEvents().stream()
      .map(event -> "%s %s -> HTTP %d".formatted(
        event.getRequest().getMethod(),
        event.getRequest().getUrl(),
        event.getResponse() == null ? -1 : event.getResponse().getStatus()))
      .collect(Collectors.joining("\n"));

    Allure.attachment(
      "WireMock: журнал запросов сценария",
      "text/plain",
      journal,
      AttachmentOptions.withFileExtension(".txt")
    );
  }

  @AfterAll
  public static void tearDownSuite() {
    WireMockServerHolder.stop();
  }
}