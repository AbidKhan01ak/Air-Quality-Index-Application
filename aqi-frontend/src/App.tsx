import React, { useState } from "react";
import "./index.css";
import AirQualityCard from "./components/airQualityCard";
import SearchBar from "./components/searchBar";
import MapView, { MarkerItem } from "./components/MapView";
import { fetchAirQuality, fetchAirQualityByCoords, AirQualityResponse } from "./api/airQuality";

export default function App() {
  const [selectedAQ, setSelectedAQ] = useState<AirQualityResponse | null>(null);
  const [markers, setMarkers] = useState<MarkerItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [history, setHistory] = useState<string[]>([]);

  async function handleSearch(city: string) {
    setLoading(true);
    setError(null);
    try {
      const res = await fetchAirQuality(city);
      setSelectedAQ(res);
      addMarkerIfMissing(res.cityName, res.latitude, res.longitude);
      setHistory((h) => [res.cityName, ...h.filter((c) => c !== res.cityName)].slice(0, 10));
    } catch (e: any) {
      setError(e.message || "Failed to fetch");
    } finally {
      setLoading(false);
    }
  }

  function addMarkerIfMissing(city: string, lat: number, lng: number) {
    setMarkers((prev) => {
      const exists = prev.find((m) => m.city === city);
      if (exists) return prev;
      return [...prev, { city, lat, lng }];
    });
  }

  async function handleMarkerClick(cityName: string) {
    setLoading(true);
    setError(null);
    try {
      const res = await fetchAirQuality(cityName);
      setSelectedAQ(res);
    } catch (e: any) {
      setError(e.message || "Failed to fetch");
    } finally {
      setLoading(false);
    }
  }

  async function handleMapClick(lat: number, lng: number) {
    setLoading(true);
    setError(null);
    try {
      const res = await fetchAirQualityByCoords(lat, lng);
      setSelectedAQ(res);
      const label = res.cityName || `(${lat.toFixed(2)}, ${lng.toFixed(2)})`;
      addMarkerIfMissing(label, res.latitude, res.longitude);
      setHistory((h) => [label, ...h.filter((c) => c !== label)].slice(0, 10));
    } catch (e: any) {
      setError(e.message || "Failed to fetch from coords");
    } finally {
      setLoading(false);
    }
  }

  function handleHistoryClick(cityName: string) {
    handleMarkerClick(cityName);
  }

  return (
    <div className="app-root two-column">
      <aside className="left-pane">
        <h1 className="app-name">CITY AQI EXPLORER</h1>
        <SearchBar onSearch={handleSearch} />
        <div className="left-body">
          {loading && <div className="loader">Loading...</div>}
          {error && <div className="error">{error}</div>}

          <div className="card-area">
  {selectedAQ ? (
    <AirQualityCard data={selectedAQ} />
  ) : (
    <div className="empty">Search a city or click map to see AQI details</div>
  )}
</div>

<div className="history">
  <h3>Recent</h3>
  <ul>
    {history.map((c) => (
      <li key={c}>
        <button className="link-btn" onClick={() => handleHistoryClick(c)}>{c}</button>
      </li>
    ))}
  </ul>
</div>

        </div>
      </aside>

      <main className="right-pane">
        <MapView
          markers={markers}
          onMarkerClick={handleMarkerClick}
          onMapClick={handleMapClick}  
          center={
            markers.length > 0
              ? { lat: markers[markers.length - 1].lat, lng: markers[markers.length - 1].lng }
              : undefined
          }
        />
      </main>
    </div>
  );
}

