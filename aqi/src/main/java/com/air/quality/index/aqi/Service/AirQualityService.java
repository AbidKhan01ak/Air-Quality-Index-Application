package com.air.quality.index.aqi.Service;

import com.air.quality.index.aqi.Cache.AirQualityCache;
import com.air.quality.index.aqi.Client.AqicnClient;
import com.air.quality.index.aqi.DTO.AirQualityResponse;
import com.air.quality.index.aqi.Exception.ExternalApiException;
import com.air.quality.index.aqi.Exception.NotFoundException;
import com.air.quality.index.aqi.Mapper.AirQualityMapper;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AirQualityService {
    private static final Logger log = LoggerFactory.getLogger(AirQualityService.class);

    private final AirQualityCache cache;
    private final AqicnClient aqicnClient;
    private final AirQualityMapper mapper;

    public AirQualityResponse getByCity(String city) {
        validateCity(city);
        String key = normalizeCityKey(city);
        // unified fetch-flow
        return fetchAndCache(key, () -> {
            var apiResp = aqicnClient.fetchByCity(city.trim());
            return validateAndMap(apiResp);
        });
    }

    public AirQualityResponse getByCoords(double lat, double lng) {
        validateCoords(lat, lng);
        // Round coords to 3 decimal places for caching stability (~100m)
        String roundedLat = round(lat, 3);
        String roundedLng = round(lng, 3);
        String key = String.format("coords:%s,%s", roundedLat, roundedLng);

        return fetchAndCache(key, () -> {
            var apiResp = aqicnClient.fetchByCoords(lat, lng);
            return validateAndMap(apiResp);
        });
    }



    /* ---------- Internal helpers ---------- */

    private AirQualityResponse fetchAndCache(String cacheKey, SupplierWithException<AirQualityResponse> supplier) {
        AirQualityResponse cached = cache.get(cacheKey);
        if (cached != null) {
            cached.setFromCache(true);
            log.debug("Cache hit for {}", cacheKey);
            return cached;
        }

        try {
            AirQualityResponse resp = supplier.get();
            resp.setFromCache(false);
            cache.put(cacheKey, resp);
            return resp;
        } catch (NotFoundException nf) {
            throw nf; // bubble up so controller can translate to 404
        } catch (ExternalApiException ex) {
            log.error("External API error while fetching {}: {}", cacheKey, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while fetching {}: {}", cacheKey, ex.getMessage(), ex);
            throw new ExternalApiException("Unexpected error while fetching AQI", ex);
        }
    }

    private AirQualityResponse validateAndMap(com.air.quality.index.aqi.DTO.aqicn.AqicnApiResponse apiResp) {
        if (apiResp == null || !"ok".equalsIgnoreCase(apiResp.getStatus())) {
            throw new NotFoundException("AQICN: data not found or status error");
        }
        var dto = mapper.toDto(apiResp);
        if (dto == null) throw new ExternalApiException("Failed to map AQICN response");
        return dto;
    }

    private void validateCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("city must be provided");
        }
    }

    private void validateCoords(double lat, double lng) {
        if (Double.isNaN(lat) || Double.isNaN(lng) || lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            throw new IllegalArgumentException("invalid coordinates");
        }
    }

    private String normalizeCityKey(String city) {
        return city.trim().toLowerCase();
    }

    private String round(double value, int decimals) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        return bd.toPlainString();
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
