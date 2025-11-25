import { useState } from "react"
import { AirQualityResponse, fetchAirQuality } from "./api/airQuality";
import SearchBar from "./components/searchBar";
import AirQualityCard from "./components/airQualityCard";
import Loader from "./components/Loader";
import ErrorBanner from "./components/errorBanner";

function App() {
  const [city, setCity] = useState("");
  const [data,setData] = useState<AirQualityResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSearch(submittedCity:string) {
    setCity(submittedCity);
    setLoading(true);
    setError(null);
    setData(null);

    try{
      const result = await fetchAirQuality(submittedCity);
      setData(result);
    }
    catch(e : any){
      setError(e.message || "Something went wrong");
    }finally{
      setLoading(false);
    }
  }

 return (
    <div className="app-root">
      <header className="app-header">
        <h1>City Air Quality Search</h1>
        <p>Search for any city and explore its real-time AQI details.</p>
      </header>

      <main className="app-main">
        <SearchBar onSearch={handleSearch} />

        {loading && <Loader />}

        {error && <ErrorBanner message={error} />}

        {data && !loading && (
          <AirQualityCard city={city} data={data} />
        )}
      </main>

      <footer className="app-footer">
        <small>
          Data courtesy of AQICN.org · For demo use only.
        </small>
      </footer>
    </div>
  );
}

export default App
