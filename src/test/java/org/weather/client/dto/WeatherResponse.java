package org.weather.client.dto;

public record WeatherResponse(Location location, Current current) {}