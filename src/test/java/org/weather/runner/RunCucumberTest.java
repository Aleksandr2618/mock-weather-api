package org.weather.runner;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@SelectPackages("org.weather")
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
  key = GLUE_PROPERTY_NAME,
  value = "org.weather.steps"
)
@ConfigurationParameter(
  key = Constants.PLUGIN_PROPERTY_NAME,
  value = "pretty, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
)
public class RunCucumberTest {}
