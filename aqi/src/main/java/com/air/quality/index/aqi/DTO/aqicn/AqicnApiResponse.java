package com.air.quality.index.aqi.DTO.aqicn;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AqicnApiResponse {
    private String status;
    private DataNode data;

    @Data
    public static class DataNode{
        private Integer aqi;
        private Integer index;
        private List<Attribution> attributions;
        private City city;
        private String dominentpol;
        private Map<String, IaqiValue> iaqi;
        private Time time;


    }

    @Data
    public static class Attribution{
        private String name;
        private String url;
    }

    @Data
    public static class City{
        private String name;
        private List<Double> geo;
        private String url;
    }

    @Data
    public static class IaqiValue{
        private Double v;
    }

    @Data
    public static class Time{
        private String s;
        private String tz;
        private Long v;
    }
}
