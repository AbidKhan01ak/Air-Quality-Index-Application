package com.air.quality.index.aqi.Service;

import com.air.quality.index.aqi.Cache.AirQualityCache;
import com.air.quality.index.aqi.DTO.AirQualityResponse;
import com.air.quality.index.aqi.DTO.aqicn.AqicnApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirQualityService {

    private final AirQualityCache cache;

    @Value("${aqicn.base-url}")
    private String baseUrl;

    @Value("${aqicn.token}")
    private String token;

    private final RestTemplate restTemplate = new RestTemplate();

    public AirQualityResponse getAirQualityByCity(String city){
        String cityKey = city.trim().toLowerCase();

        AirQualityResponse cached = cache.get(cityKey);

        if(cached != null){
            cached.setFromCache(true);
            return cached;
        }

        String url = String.format("%s/feed/%s/?token=%s", baseUrl, cityKey, token);

        ResponseEntity<AqicnApiResponse> responseEntity = restTemplate.getForEntity(url, AqicnApiResponse.class);

        AqicnApiResponse apiResponse = responseEntity.getBody();

        if(apiResponse == null || !"ok".equalsIgnoreCase(apiResponse.getStatus())){
            throw new RuntimeException("City not found or AQICN error");
        }

        AirQualityResponse mapped = mapToDto(apiResponse);
        mapped.setFromCache(false);
        cache.put(cityKey, mapped);

        return mapped;
    }
    private AirQualityResponse mapToDto(AqicnApiResponse api) {
        AqicnApiResponse.DataNode data = api.getData();

        double lat = 0;
        double lng = 0;
        if (data.getCity() != null && data.getCity().getGeo() != null
                && data.getCity().getGeo().size() == 2) {
            lat = data.getCity().getGeo().get(0);
            lng = data.getCity().getGeo().get(1);
        }

        List<String> attributions = data.getAttributions() == null ? List.of() :
                data.getAttributions().stream()
                        .map(a -> a.getName() + (a.getUrl() != null ? " (" + a.getUrl() + ")" : ""))
                        .collect(Collectors.toList());

        Integer aqi = data.getAqi();
        return AirQualityResponse.builder()
                .cityName(data.getCity() != null ? data.getCity().getName() : "Unknown")
                .latitude(lat)
                .longitude(lng)
                .aqi(aqi)
                .aqiCategory(aqiCategory(aqi))
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

    private String aqiCategory(Integer aqi) {
        if (aqi == null) return "Unknown";
        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Moderate";
        if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        if (aqi <= 200) return "Unhealthy";
        if (aqi <= 300) return "Very Unhealthy";
        return "Hazardous";
    }

}
