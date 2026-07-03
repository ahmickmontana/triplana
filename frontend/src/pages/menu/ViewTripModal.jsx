import { useState } from 'react';
import './CreateTripModal.css';
import defaultTripImg from '../../assets/images/default-trip-img.jpg';
import { deleteTrip } from '../../api/tripApi.js'

export default function ViewTripModal({ trip, onClose, onEdit, onTripDeleted }) {
    const [confirmDelete, setConfirmDelete] = useState(null);

    const formatDate = (dateStr) => {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        return date.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short', year: 'numeric' });
    };

    const handleDelete = async () => {
        try {
            await deleteTrip(trip.id);
            
            onTripDeleted();
            onClose();
        } catch (error) {
            if (error.response?.data) {
                console.log(error.response.data);
            }
        }
    }


    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="view-modal" onClick={(e) => e.stopPropagation()}>
                <div className="trip-view-image">
                    <img 
                        src={trip.coverImagePath ? `http://localhost:8080${trip.coverImagePath}` : defaultTripImg}
                        alt={trip.name} 
                    />
                </div>

                <div className="view-modal-scrollable">
                    <p className="modal-title">{trip.name}</p>
                    {trip.description && <p className="modal-subtitle">{trip.description}</p>}
                </div>

                <div className="view-modal-footer">
                    <p className="trip-card-dates">
                        {trip.startDate && trip.endDate && trip.startDate !== trip.endDate
                            ? `${formatDate(trip.startDate)} to ${formatDate(trip.endDate)}`
                            : trip.startDate 
                            ? formatDate(trip.startDate)
                            : ''}
                    </p>
                    {confirmDelete ? (
                        <div className="modal-delete-section">
                            <strong>Are you sure you want to delete this trip?</strong>
                            <strong>This action cannot be undone.</strong>
                            
                            <div className="modal-actions">
                                <button className="modal-btn-cancel" onClick={() => setConfirmDelete(false)}>Cancel</button>
                                <button className="modal-btn-confirm" onClick={handleDelete}>Confirm Delete</button>
                            </div>
                        </div>
                    ) : (
                        <div className="modal-actions">
                            <button className="modal-btn-cancel" onClick={onEdit}>Edit</button>
                            <button className="modal-btn-cancel" onClick={() => setConfirmDelete(true)}>Delete</button>
                            <button className="modal-btn-confirm" onClick={onClose}>View</button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}