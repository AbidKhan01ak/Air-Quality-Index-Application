# 🌍 Air Quality Index (AQI) Search Engine  
**Java 17 • Spring Boot 4.0.0 • React • JavaScript**

A full-stack application that allows users to search global Air Quality Index (AQI) by **city** or **map location**, view detailed environmental metrics, and visualize locations on an interactive map (MapLibre GL).

This project demonstrates:
- Clean REST API design (Java + Spring Boot)
- Caching layer for performance
- Modern frontend built with React + Vite
- Interactive map-based AQI lookup
- DRY, modular, production-ready architecture

---

## 🚀 Features

### **Backend (Spring Boot)**
- ✔ Search AQI by city
- ✔ Search AQI by latitude/longitude
- ✔ Custom caching with TTL + eviction
- ✔ Global exception handling
- ✔ Vendor API integration (AQICN)
- ✔ Java 17 + Spring Boot 4.0.0

### **Frontend (React + Vite)**
- ✔ Modern minimalist UI  
- ✔ Left panel (40%): search, AQI details, history  
- ✔ Right panel (60%): interactive MapLibre GL map  
- ✔ Click on map → display AQI  
- ✔ Click on markers → fetch AQI  
- ✔ Beautiful theme and UX  

---

## 📦 Tech Stack

### **Backend**
- Java 17  
- Spring Boot 4.0.0  
- RestTemplate  
- Lombok  
- SLF4J Logging  

### **Frontend**
- React (Vite)  
- JavaScript / TypeScript  
- MapLibre GL  
- Custom CSS (Tailwind-style)  

---

## 🛠 Project Structure
```
backend/
 ├── src/main/java/com/air/quality/index/aqi
 │     ├── Controller
 │     │     └── AirQualityController.java
 │     ├── Service
 │     │     └── AirQualityService.java
 │     ├── client
 │     │     └── AqicnClient.java
 │     ├── mapper
 │     │     └── AirQualityMapper.java
 │     ├── Cache
 │     │     └── AirQualityCache.java
 │     ├── exception
 │     │     ├── NotFoundException.java
 │     │     └── ExternalApiException.java
 │     └── config
 │           └── AppConfig.java
 └── application.yml

frontend/
 ├── src/
 │    ├── api/
 │    ├── components/
 │    │     ├── AirQualityCard.jsx
 │    │     ├── SearchBar.jsx
 │    │     └── MapView.jsx
 │    ├── App.jsx
 │    └── index.css
 └── vite.config.js
```


---

## 🔧 Setup Instructions

### **1. Clone the repository**
```bash
git clone https://github.com/<your-repo>.git
cd aqi-search-engine
```
## ⚙️ Backend Setup (Spring Boot)

### **2. Navigate into backend**

```cd aqi```

### **3. Configure AQICN API token**

Create ```application.yml```:

```
server:
  port: 8080

aqicn:
  base-url: "https://api.waqi.info"
  token: "${AQICN_TOKEN:your_token_here}"

cache:
  ttl-seconds: 300
  max-entries: 100
```

### **4. Run the backend**
```
./mvnw spring-boot:run
```

Backend is available at:
```http://localhost:8080```

## 🌐 Frontend Setup (React + Vite)

### **1. Navigate into frontend**
```cd aqi-frontend```

### **2. Install dependencies**
```npm install```

### **3. Run development server**
```npm run dev```


Frontend is available at:
```http://localhost:5173```

## 📡 API Endpoints

**GET AQI by city**
```GET /api/v1/air-quality?city=Tokyo```

**GET AQI by coordinates**
```GET /api/v1/air-quality/by-coords?lat=35.67&lng=139.65```

Example API Response
```
{
  "cityName": "Tokyo",
  "latitude": 35.67,
  "longitude": 139.65,
  "aqi": 70,
  "aqiCategory": "Moderate",
  "dominantPollutant": "pm25",
  "pm25": 70,
  "localTime": "2025-11-24 21:00",
  "fromCache": false
}
```
## 🧠 Architecture Overview

### **Backend Flow**
1. **Controller** receives request  
2. **Service** validates input  
3. **Cache** checked → return instantly if **HIT**  
4. If **MISS** → `AqicnClient` makes external API call  
5. **Mapper** converts vendor DTO → internal DTO  
6. **Response cached** and returned to the client  

---

### **Frontend Flow**
1. User **searches** or **clicks on map**  
2. **API request** sent to backend  
3. **AQI details** displayed in left panel  
4. **Map centers** & marker added  
5. **Search history** updated  

---

## 📸 UI Experience
- Clean and modern **light theme**  
- Smooth **map zooming & panning**  
- Elegant **AQI cards** with pollutant metrics  
- **History** section pinned at bottom  
- Fully **responsive layout**  


💬 Support / Contact

If you’d like improvements, open an issue or submit a pull request.
