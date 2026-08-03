import { useState, useRef  } from 'react';
import { getAutocompleteSuggestions, getPlaceDetails } from '../api/placesApi';
import './LocationDropdown.css';


export default function LocationDropdown({ locationValue, onSelect, types = 'establishment' }) {
    const [locationName, setLocationName] = useState(locationValue || '');
    const [suggestions, setSuggestions] = useState([]);
    const debounceTimer = useRef(null);
    const [hasSearched, setHasSearched] = useState(false);

    const handleInput = (e) => {
        const value = e.target.value;
        setLocationName(value);
        clearTimeout(debounceTimer.current);

        if (value.length < 3) {
            setSuggestions([]);
            return;
        }

        debounceTimer.current = setTimeout(async () => {
            const response = await getAutocompleteSuggestions(value, types);
            setHasSearched(true);
            setSuggestions(response.data);
        }, 500);
    }

    const handleSelect = async (suggestion) => {
        setLocationName(suggestion.mainText);
        setSuggestions([]);

        const response = await getPlaceDetails(suggestion.placeId);
        const details = response.data;

        console.log(details);

        onSelect({
            locationName: suggestion.mainText,
            latitude: details.latitude,
            longitude: details.longitude,
            googlePlaceId: suggestion.placeId
        });
    }

    return (
        <>
            <div className="form-content" style={{ position: 'relative' }}>
                <label className="input-label">Location</label>
                <input
                    type="text"
                    placeholder="Location"
                    value={locationName}
                    onChange={handleInput}
                    className="input-field"
                />

                {suggestions.length > 0 && (
                    <ul className="location-dropdown">
                        {suggestions.map(s => (
                            <li key={s.placeId} onClick={() => handleSelect(s)}>
                                <p>{s.mainText}</p>
                                <p>{s.secondaryText}</p>
                            </li>
                        ))}

                    </ul>
                )}
                {hasSearched && locationName.length >= 3 && suggestions.length === 0 && (
                    <ul className="location-dropdown">
                        <li>
                            No locations found.
                        </li>
                    </ul>
                )}
            </div>

        </>
    )
}