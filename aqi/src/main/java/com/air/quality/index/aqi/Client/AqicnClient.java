package com.air.quality.index.aqi.Client;

import com.air.quality.index.aqi.DTO.aqicn.AqicnApiResponse;
import com.air.quality.index.aqi.Exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;


@Component
@RequiredArgsConstructor
public class AqicnClient {

    private static final Logger log = LoggerFactory.getLogger(AqicnClient.class);

    private final RestTemplate restTemplate;

    @Value("${aqicn.base-url}")
    private String baseUrl;

    @Value("${aqicn.token}")
    private String token;

    private static final String FEED_CITY_PATH = "/feed/{city}/";
    private static final String FEED_GEO_FORMAT = "/feed/geo:{lat};{lng}/";

    public AqicnApiResponse fetchByCity(String city) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(FEED_CITY_PATH)
                .queryParam("token", token)
                .buildAndExpand(city)
                .toUri();

        log.debug("Calling AQICN (city): {}", uri);
        return callApi(uri);
    }

    public AqicnApiResponse fetchByCoords(double lat, double lng) {
        String path = FEED_GEO_FORMAT.replace("{lat}", String.valueOf(lat)).replace("{lng}", String.valueOf(lng));
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("token", token)
                .build()
                .toUri();

        log.debug("Calling AQICN (coords): {}", uri);
        return callApi(uri);
    }

    private AqicnApiResponse callApi(URI uri) {
        try {
            ResponseEntity<AqicnApiResponse> resp = restTemplate.getForEntity(uri, AqicnApiResponse.class);
            return resp.getBody();
        } catch (Exception ex) {
            log.error("Error calling AQICN: {}", ex.getMessage(), ex);
            throw new ExternalApiException("Failed to call AQICN", ex);
        }
    }
}
