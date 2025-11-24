package com.air.quality.index.aqi.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class AirQualityResponse {
    private String cityName;
    private double latitude;
    private double longitude;
    private Integer aqi;
    private String aqiCategory;
    private String dominantPollutant;

    private Double pm25;
    private Double pm10;
    private Double o3;
    private Double no2;
    private Double so2;
    private Double co;

    private String localTime;
    private String timezone;

    private List<String> attributions;
    private Instant fetchedAt;
    private boolean fromCache;
}
