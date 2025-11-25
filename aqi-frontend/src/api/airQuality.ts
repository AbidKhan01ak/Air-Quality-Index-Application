export interface AirQualityResponse {
    cityName: string;
    latitude: number;
    longitude: number;
    aqi: number | null;
    aqiCategory: string;
    dominantPollutant: string | null;

    pm25?: number | null;
    pm10?: number | null;
    o3?: number | null;
    no2?: number | null;
    so2?: number | null;
    co?: number | null;

    localTime?: string | null;
    timezone?: string | null;

    attributions: string[];
    fetchedAt: string;
    fromCache: boolean;
}

const BASE_URL = "http://localhost:8080";

export async function fetchAirQuality(city: string): Promise<AirQualityResponse> {

    const result = await fetch(`${BASE_URL}/api/air-quality?city=${encodeURIComponent(city)}`);

    if(!result.ok){
        throw new Error("City not found or server error");
    }
    return result.json();
}