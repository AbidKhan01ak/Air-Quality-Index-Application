package com.air.quality.index.aqi.Controller;

import com.air.quality.index.aqi.DTO.AirQualityResponse;
import com.air.quality.index.aqi.Service.AirQualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/air-quality")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AirQualityController {

    private final AirQualityService airQualityService;

    /**
     * Get AQI by city name.
     * Returns 400 for invalid input. Other errors are handled by GlobalExceptionHandler.
     */
    @GetMapping
    public ResponseEntity<?> getByCity(@RequestParam("city") String city) {
        if (city == null || city.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "city query parameter is required"));
        }
        AirQualityResponse response = airQualityService.getByCity(city.trim());
        return ResponseEntity.ok(response);
    }

    /**
     * Get AQI by geographic coordinates (lat, lng).
     * Returns 400 for invalid coords; other errors handled globally.
     */
    @GetMapping("/by-coords")
    public ResponseEntity<?> getByCoords(
            @RequestParam("lat") Double lat,
            @RequestParam("lng") Double lng) {

        if (lat == null || lng == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "lat and lng query parameters are required"));
        }

        if (lat < -90.0 || lat > 90.0 || lng < -180.0 || lng > 180.0) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid latitude or longitude"));
        }

        AirQualityResponse response = airQualityService.getByCoords(lat, lng);
        return ResponseEntity.ok(response);
    }
}
