import React, { FormEvent, useState } from "react";

export default function SearchBar({ onSearch }: { onSearch: (city: string) => void }) {
  const [q, setQ] = useState("");

  function submit(e: FormEvent) {
    e.preventDefault();
    const value = q.trim();
    if (!value) return;
    onSearch(value);
    setQ("");
  }

  return (
    <form onSubmit={submit} className="search-bar">
      <input
        placeholder="Enter city to see Air Quality"
        value={q}
        onChange={(e) => setQ(e.target.value)}
      />
      <button type="submit">Search</button>
    </form>
  );
}
