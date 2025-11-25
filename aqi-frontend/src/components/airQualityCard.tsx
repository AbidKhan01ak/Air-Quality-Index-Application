import { AirQualityResponse } from "../api/airQuality";
import PollutantBadge from "./pollutantBadge";

interface Props{
    city: string;
    data: AirQualityResponse;
}

function getAqiClass(aqi: number | null){
    if(aqi == null || aqi == undefined) return "aqi-unknown";
    if (aqi <= 50) return "aqi-good";
    if (aqi <= 100) return "aqi-moderate";
    if (aqi <= 150) return "aqi-usg";
    if (aqi <= 200) return "aqi-unhealthy";
    if (aqi <= 300) return "aqi-very-unhealthy";
    
    return "aqi-hazardous";
}
export default function AirQualityCard({ data } : Props){
    return(
        <section className="aq-card">
            <div className="aq-summary">
                <div className={`aq-value ${getAqiClass(data.aqi)}`}>
                    <span className="aq-number">{data.aqi ?? "--"}</span>
                    <span className="aq-label">{data.aqiCategory}</span>
                </div>
                <div className="aq-meta">
                    <h2>{data.cityName}</h2>
                    <p>
                        Lat: {data.latitude.toFixed(2)}, Lng: {data.longitude.toFixed(2)}
                    </p>
                    <p>
                        Dominant Pollutant:{" "}
                        <strong>{data.dominantPollutant ?? "N/A"}</strong>
                    </p>
                    <p>
                        Local Time: {" "}
                        <strong>
                            {data.localTime ?? "Unknown"} ({data.timezone ?? "N/A"})
                        </strong>
                    </p>
                    {data.fromCache && <span className="badge">cached</span>}
                </div>
            </div>
            <div className="aq-pollutants">
                <h3>Key Pollutants (µg/m³ index)</h3>
                <div className="pollutant-grid">
                    <PollutantBadge label="PM2.5" value={data.pm25} />
                    <PollutantBadge label="PM10" value={data.pm10} />
                    <PollutantBadge label="O₃" value={data.o3} />
                    <PollutantBadge label="NO₂" value={data.no2} />
                    <PollutantBadge label="SO₂" value={data.so2} />
                    <PollutantBadge label="CO" value={data.co} />
                </div>
            </div>
            {data.attributions?.length > 0 && (
                <div className="aq-attributions">
                <h3>Data Sources</h3>
                <ul>
                    {data.attributions.map((a, idx) => (
                        <li key={idx}>{a}</li>
                    ))}
                </ul>
            </div>
            )}
        </section>
    );
}