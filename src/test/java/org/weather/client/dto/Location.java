package org.weather.client.dto;

public record Location(
  String name,
  String region,
  String country,
  Double lat,
  Double lon,
  String tzId,
  Long localtimeEpoch,
  String localtime) {}