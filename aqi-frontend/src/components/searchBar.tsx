import { FormEvent, useState } from "react";

interface Props{
    onSearch: (city: string) => void;
}

export default function SearchBar({onSearch} : Props){
    const [value, setValue] = useState("");

    function handleSubmit(e: FormEvent){
        e.preventDefault();
        if(!value.trim()){
            return;
        }
        onSearch(value.trim());
    }
    return (
        <form className="search-bar" onSubmit={handleSubmit}>
            <input
                type="text"
                placeholder="Enter city name to find Air Quality"
                value={value}
                onChange={(e) => setValue(e.target.value)}
            />
            <button type="submit">Search</button>
        </form>
    );
}