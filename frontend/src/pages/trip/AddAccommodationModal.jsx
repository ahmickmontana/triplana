import { useState } from 'react';
import '../menu/CreateTripModal.css';
import { createAccommodation } from '../../api/accommodationApi.js'
import LocationDropdown from '../../components/LocationDropdown.jsx';


export default function AddAccommodationModal({ tripId, onClose, onAccommodationAdded }) {
    const [accommodationName, setAccommodationName] = useState('');
    const [locationName, setLocationName] = useState('');
    const [checkInDate, setCheckInDate] = useState('');
    const [checkOutDate, setCheckOutDate] = useState('');
    const [latitude, setLatitude] = useState(null);
    const [longitude, setLongitude] = useState(null);
    const [googlePlaceId, setGooglePlaceId] = useState(null);

    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const handleAddAccommodation = async () => {
        setLoading(true);
        setErrors({});

        try {
            const response = await createAccommodation(tripId, {
                name: accommodationName, 
                locationName: locationName || null, 
                checkInDate, 
                checkOutDate,
                latitude: latitude || null,
                longitude: longitude || null,
                googlePlaceId: googlePlaceId || null
            });
                        
            onAccommodationAdded();
            onClose();

        } catch (error) {
            if (error.response?.data) {
                setErrors(error.response.data);
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <p className="modal-title">Add Accommodation</p>
                <form className="trip-form">
                    <div className="form-content">
                        <label className="input-label">Accommodation Name</label>
                        <input
                            type="text"
                            maxLength={100}
                            placeholder="Accommodation Name"
                            value={accommodationName}
                            onChange={(e) => setAccommodationName(e.target.value)}
                            className={`input-field ${errors.name ? 'input-error-border' : ''}`}
                        />
                        {errors.name && <p className="input-error">{errors.name}</p>}
                    </div>

                    <LocationDropdown
                        types="lodging"
                        onSelect={(place) => {
                            setLocationName(place.locationName);
                            setLatitude(place.latitude);
                            setLongitude(place.longitude);
                            setGooglePlaceId(place.googlePlaceId);
                        }}
                    />

                    <div className="form-content">
                        <label className="input-label">Check-In Date</label>
                        <input
                            type="date"
                            value={checkInDate}
                            onChange={(e) => setCheckInDate(e.target.value)}
                            className={`input-field ${errors.checkInDate ? 'input-error-border' : ''}`}
                        />
                        {errors.checkInDate && <p className="input-error">{errors.checkInDate}</p>}
                    </div>

                    <div className="form-content">
                        <label className="input-label">Check-Out Date</label>
                        <input
                            type="date"
                            value={checkOutDate}
                            onChange={(e) => setCheckOutDate(e.target.value)}
                            className={`input-field ${errors.checkOutDate ? 'input-error-border' : ''}`}
                        />
                        {errors.checkOutDate && <p className="input-error">{errors.checkOutDate}</p>}
                    </div>
                </form>
                <div className="modal-actions">
                    <button className="modal-btn-cancel" onClick={onClose}>
                        Cancel
                    </button>
                    <button type="button" className="modal-btn-confirm" onClick={handleAddAccommodation} disabled={loading}>
                        {loading ? 'Adding...' : 'Add Accommodation'}
                    </button>
                </div>
            </div>
        </div>
    )
}