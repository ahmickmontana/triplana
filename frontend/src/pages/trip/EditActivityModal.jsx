import { useState } from 'react';
import './AddActivityModal.css';
import { updateActivity } from '../../api/activityApi.js';
import LocationDropdown from '../../components/LocationDropdown.jsx';

export default function EditActivityModal({ activity, tripId, dayId, onClose, onActivityEdited }) {
    const [title, setTitle] = useState(activity.title || '');
    const [description, setDescription] = useState(activity.description || '');
    const [startTime, setStartTime] = useState(activity.startTime || '');
    const [endTime, setEndTime] = useState(activity.endTime || '');
    const [locationName, setLocationName] = useState(activity.locationName || '');
    const [latitude, setLatitude] = useState(activity.latitude || null);
    const [longitude, setLongitude] = useState(activity.longitude || null);
    const [googlePlaceId, setGooglePlaceId] = useState(activity.googlePlaceId || null);

    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const handleUpdateActivity = async () => {
        setLoading(true);
        setErrors({});

        try {
            await updateActivity(tripId, dayId, activity.id, {
                title,
                description: description || null,
                startTime: startTime || null,
                endTime: endTime || null,
                locationName: locationName || null,
                latitude: latitude || null,
                longitude: longitude || null,
                googlePlaceId: googlePlaceId || null,
            });

            onActivityEdited();
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
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <p className="modal-title">Update Activity</p>
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
                    <button type="button" className="modal-btn-confirm" onClick={handleUpdateActivity} disabled={loading}>
                        {loading ? 'Updating...' : 'Update Activity'}
                    </button>
                </div>
            </div>
        </div>
    );
}