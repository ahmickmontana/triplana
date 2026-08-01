import { useState } from 'react';
import './AddActivityModal.css';
import { createActivity } from '../../api/activityApi.js';
import LocationDropdown from '../../components/LocationDropdown.jsx';

export default function AddActivityModal({ tripId, dayId, onClose, onActivityCreated }) {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const [locationName, setLocationName] = useState('');
    const [latitude, setLatitude] = useState(null);
    const [longitude, setLongitude] = useState(null);
    const [googlePlaceId, setGooglePlaceId] = useState(null);

    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const handleCreateActivity = async () => {
        setLoading(true);
        setErrors({});

        try {
            await createActivity(tripId, dayId, {
                title,
                description: description || null,
                startTime: startTime || null,
                endTime: endTime || null,
                locationName: locationName || null,
                latitude: latitude || null,
                longitude: longitude || null,
                googlePlaceId: googlePlaceId || null,
            });

            onActivityCreated();
            onClose();
        } catch (error) {
            if (error.response?.data) {
                setErrors(error.response.data);
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal activity-modal" onClick={(e) => e.stopPropagation()}>
                <p className="modal-title">Add Activity</p>
                <form className="trip-form">
                    <div className="form-content">
                        <label className="input-label">Activity Name</label>
                        <input
                            type="text"
                            maxLength={100}
                            placeholder="Name"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            className={`input-field ${errors.title ? 'input-error-border' : ''}`}
                        />
                        {errors.title && <p className="input-error">{errors.title}</p>}
                    </div>

                    <div className="form-content">
                        <label className="input-label">Description</label>
                        <textarea
                            placeholder="Description"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            className="input-field description"
                        />
                    </div>

                    <div className="form-content">
                        <label className="input-label">Start Time</label>
                        <input
                            type="time"
                            value={startTime}
                            onChange={(e) => setStartTime(e.target.value)}
                            className={`input-field ${errors.startTime ? 'input-error-border' : ''}`}
                        />
                        {errors.startTime && <p className="input-error">{errors.startTime}</p>}
                    </div>

                    <div className="form-content">
                        <label className="input-label">End Time</label>
                        <input
                            type="time"
                            value={endTime}
                            onChange={(e) => setEndTime(e.target.value)}
                            className={`input-field ${errors.endTime ? 'input-error-border' : ''}`}
                        />
                        {errors.endTime && <p className="input-error">{errors.endTime}</p>}
                    </div>

                    <LocationDropdown
                        locationValue={locationName}
                        onSelect={(place) => {
                            setLocationName(place.locationName);
                            setLatitude(place.latitude);
                            setLongitude(place.longitude);
                            setGooglePlaceId(place.googlePlaceId);
                        }}
                    />

                </form>
                <div className="modal-actions">
                    <button className="modal-btn-cancel" onClick={onClose}>
                        Cancel
                    </button>
                    <button type="button" className="modal-btn-confirm" onClick={handleCreateActivity} disabled={loading}>
                        {loading ? 'Adding...' : 'Add Activity'}
                    </button>
                </div>
            </div>
        </div>
    );
}