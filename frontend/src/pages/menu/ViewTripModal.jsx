import { useState } from 'react';
import './CreateTripModal.css';
import defaultTripImg from '../../assets/images/default-trip-img.jpg';

export default function ViewTripModal({ trip, onClose, onEdit }) {

    const formatDate = (dateStr) => {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        return date.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short', year: 'numeric' });
    };

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
                        {trip.startDate && trip.endDate 
                            ? `${formatDate(trip.startDate)} to ${formatDate(trip.endDate)}`
                            : trip.startDate 
                            ? formatDate(trip.startDate)
                            : ''}
                    </p>
                    <div className="modal-actions">
                        <button className="modal-btn-cancel" onClick={onEdit}>
                            Edit
                        </button>
                        <button className="modal-btn-confirm" onClick={onClose}>
                            View
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}