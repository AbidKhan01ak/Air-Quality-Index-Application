import React, { useEffect, useRef } from "react";
import maplibregl, { Map, Marker, Popup } from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";

export type MarkerItem = {
  city: string;
  lat: number;
  lng: number;
};

const DEFAULT_CENTER = { lat: 20, lng: 0 };
const DEFAULT_ZOOM = 2;

export default function MapView({
  markers,
  onMarkerClick,
  onMapClick,
  center,
}: {
  markers: MarkerItem[];
  onMarkerClick: (cityName: string) => void;
  onMapClick?: (lat: number, lng: number) => void;
  center?: { lat: number; lng: number };
}) {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const mapRef = useRef<Map | null>(null);
  const markerRefs = useRef<Record<string, Marker>>({});

  useEffect(() => {
    if (!containerRef.current) return;

    if (!mapRef.current) {
      const style = {
        version: 8 as const,
        sources: {
          osm: {
            type: "raster",
            tiles: ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
            tileSize: 256,
            attribution:
              '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
          },
        },
        layers: [
          {
            id: "osm-tiles",
            type: "raster",
            source: "osm",
          },
        ],
      };

      const map = new maplibregl.Map({
        container: containerRef.current,
        style: style as any, 
        center: [DEFAULT_CENTER.lng, DEFAULT_CENTER.lat],
        zoom: DEFAULT_ZOOM,
      });

      map.addControl(new maplibregl.NavigationControl(), "top-right");

      map.on("click", (e) => {
        const { lat, lng } = e.lngLat;
        if (onMapClick) onMapClick(lat, lng);
      });

      mapRef.current = map;
    }

    return () => {
    };
  }, [containerRef.current]);

  useEffect(() => {
    const map = mapRef.current;
    if (!map) return;

    const existing = markerRefs.current;
    const keepKeys = new Set(markers.map((m) => m.city));

    Object.keys(existing).forEach((k) => {
      if (!keepKeys.has(k)) {
        existing[k].remove();
        delete existing[k];
      }
    });

    markers.forEach((m) => {
      if (markerRefs.current[m.city]) return; 

      const el = document.createElement("div");
      el.className = "map-marker";
      el.style.width = "22px";
      el.style.height = "22px";
      el.style.background = "#ff6b6b";
      el.style.border = "2px solid white";
      el.style.borderRadius = "50%";
      el.style.boxShadow = "0 2px 6px rgba(0,0,0,0.4)";
      el.style.cursor = "pointer";
      el.title = m.city;

      el.addEventListener("click", (ev) => {
        ev.stopPropagation();
        onMarkerClick(m.city);

        const popup = new Popup({ offset: 10 })
          .setLngLat([m.lng, m.lat])
          .setHTML(`<strong>${m.city}</strong><div style="margin-top:6px"><button id="show-${CSS.escape(m.city)}">Show AQI</button></div>`)
          .addTo(map);

        setTimeout(() => {
          const btn = document.getElementById(`show-${m.city}`);
          if (btn) {
            btn.onclick = (e) => {
              e.preventDefault();
              onMarkerClick(m.city);
              popup.remove();
            };
          }
        }, 100);
      });

      const marker = new Marker({ element: el }).setLngLat([m.lng, m.lat]).addTo(map);
      markerRefs.current[m.city] = marker;
    });
  }, [markers, onMarkerClick]);

  useEffect(() => {
    const map = mapRef.current;
    if (!map) return;
    if (!center) return;

    map.flyTo({ center: [center.lng, center.lat], zoom: 8, speed: 0.8 });
  }, [center]);

  return (
    <div style={{ width: "100%", height: "100%" }} ref={containerRef}></div>
  );
}
