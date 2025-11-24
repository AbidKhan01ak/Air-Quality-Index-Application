package com.air.quality.index.aqi.Controller;

import com.air.quality.index.aqi.DTO.AirQualityResponse;
import com.air.quality.index.aqi.Service.AirQualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/air-quality")
@CrossOrigin(origins = "http://localhost:5173") // Vite default port
@RequiredArgsConstructor
public class AirQualityController {

    private final AirQualityService airQualityService;

    @GetMapping
    public ResponseEntity<AirQualityResponse> getByCity(@RequestParam("city") String city){
        if(city == null || city.trim().isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        try{
            AirQualityResponse response = airQualityService.getAirQualityByCity(city);
            return ResponseEntity.ok(response);
        }catch (RuntimeException ex){
            return ResponseEntity.notFound().build();
        }
    }
}
