package org.weather.glue;

import io.restassured.response.Response;
import org.weather.client.dto.*;

public class ScenarioState {

  private String city;
  private CurrentWeatherRequest request;
  private Response response;
  private WeatherResponse weather;
  private ApiErrorResponse error;

  public String getCity() {
    return city;
  }

  public CurrentWeatherRequest getRequest() {
    return request;
  }

  public Response getResponse() {
    return response;
  }

  public WeatherResponse getWeather() {
    return weather;
  }

  public ApiErrorResponse getError() {
    return error;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setRequest(CurrentWeatherRequest request) {
    this.request = request;
  }

  public void setResponse(Response response) {
    this.response = response;
  }

  public void setWeather(WeatherResponse weather) {
    this.weather = weather;
  }

  public void setError(ApiErrorResponse error) {
    this.error = error;
  }
}