package org.weather.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weather.config.Config;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public final class WireMockServerHolder {

  private static final Logger LOG = LoggerFactory.getLogger(WireMockServerHolder.class);

  private static volatile WireMockServer server;

  private WireMockServerHolder() {}

  public static synchronized WireMockServer startIfNeeded() {
    if (server == null || !server.isRunning()) {
      server = new WireMockServer(options()
        .port(Config.wireMockPort())
        .notifier(new Slf4jNotifier(true)));

      server.start();

      WireMock.configureFor(server.port());
      LOG.info("WireMock запущен: {}", server.baseUrl());
    }

    return server;
  }

  public static String baseUrl() {
    return startIfNeeded().baseUrl();
  }

  public static synchronized void reset() {
    if (server != null && server.isRunning()) {
      server.resetAll();
    }
  }

  public static synchronized void stop() {
    if (server != null && server.isRunning()) {
      server.stop();

      LOG.info("WireMock остановлен");
    }
  }
}