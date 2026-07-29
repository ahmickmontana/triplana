import { useState } from 'react';
import '../menu/CreateTripModal.css';
import { updateAccommodation } from '../../api/accommodationApi.js'


export default function EditAccommodationModal({ tripId, accommodation, onClose, onAccommodationUpdated }) {
    const [accommodationName, setAccommodationName] = useState(accommodation.name || '');
    const [locationName, setLocationName] = useState(accommodation.locationName || '');
    const [checkInDate, setCheckInDate] = useState(accommodation.checkInDate || '');
    const [checkOutDate, setCheckOutDate] = useState(accommodation.checkOutDate || '');

    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const handleUpdateAccommodation = async () => {
        setLoading(true);
        setErrors({});

        try {
            const response = await updateAccommodation(tripId, accommodation.id, {
                name: accommodationName, locationName: locationName || null, checkInDate, checkOutDate
            });
                        
            onAccommodationUpdated();
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
                <p className="modal-title">Edit Accommodation</p>
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

                    <div className="form-content">
                        <label className="input-label">Accommodation Location</label>
                        <textarea
                            type="text"
                            maxLength={255}
                            placeholder="Location Address"
                            value={locationName}
                            onChange={(e) => setLocationName(e.target.value)}
                            className={'input-field description'}
                        />
                    </div>

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
                    <button type="button" className="modal-btn-confirm" onClick={handleUpdateAccommodation} disabled={loading}>
                        {loading ? 'Updating...' : 'Update Accommodation'}
                    </button>
                </div>
            </div>
        </div>
    )
}