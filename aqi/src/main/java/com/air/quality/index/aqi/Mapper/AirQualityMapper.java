package com.air.quality.index.aqi.Mapper;

import com.air.quality.index.aqi.DTO.AirQualityResponse;
import com.air.quality.index.aqi.DTO.aqicn.AqicnApiResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AirQualityMapper {
    public AirQualityResponse toDto(AqicnApiResponse api) {
        if (api == null || api.getData() == null) return null;
        AqicnApiResponse.DataNode data = api.getData();

        double lat = 0, lng = 0;
        if (data.getCity() != null && data.getCity().getGeo() != null && data.getCity().getGeo().size() == 2) {
            lat = data.getCity().getGeo().get(0);
            lng = data.getCity().getGeo().get(1);
        }

        List<String> attributions = (data.getAttributions() == null) ? List.of()
                : data.getAttributions().stream()
                .map(a -> a.getName() + (a.getUrl() != null ? " (" + a.getUrl() + ")" : ""))
                .collect(Collectors.toList());

        Integer aqi = data.getAqi();

        return AirQualityResponse.builder()
                .cityName(data.getCity() != null ? data.getCity().getName() : "Unknown")
                .latitude(lat)
                .longitude(lng)
                .aqi(aqi)
                .aqiCategory(mapAqiCategory(aqi))
                .dominantPollutant(data.getDominentpol())
                .pm25(getIaqi(data, "pm25"))
                .pm10(getIaqi(data, "pm10"))
                .o3(getIaqi(data, "o3"))
                .no2(getIaqi(data, "no2"))
                .so2(getIaqi(data, "so2"))
                .co(getIaqi(data, "co"))
                .localTime(data.getTime() != null ? data.getTime().getS() : null)
                .timezone(data.getTime() != null ? data.getTime().getTz() : null)
                .attributions(attributions)
                .fetchedAt(Instant.now())
                .build();
    }

    private Double getIaqi(AqicnApiResponse.DataNode data, String key) {
        if (data.getIaqi() == null) return null;
        AqicnApiResponse.IaqiValue v = data.getIaqi().get(key);
        return v != null ? v.getV() : null;
    }

    private String mapAqiCategory(Integer aqi) {
        if (aqi == null) return "Unknown";
        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Moderate";
        if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        if (aqi <= 200) return "Unhealthy";
        if (aqi <= 300) return "Very Unhealthy";
        return "Hazardous";
    }
}
