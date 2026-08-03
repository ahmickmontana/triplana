import { useState, useEffect } from 'react';
import './AddActivityModal.css';
import './ViewAccommodationsModal.css';
import { getAccommodations, deleteAccommodation } from '../../api/accommodationApi.js';

export default function ViewAccommodations({ tripId, selectedDay, onClose, onAddAccommodation, onEditAccommodation }) {
    const [accommodations, setAccommodations] = useState([]);
    const [deletingAccommodationId, setDeletingAccommodationId] = useState(null);

    useEffect(() => {
            fetchAccommodations();
        }, [tripId]);

    const getAccommodationDateString = (accommodation) => {
        const checkInDate = new Date(accommodation.checkInDate);
        const checkOutDate = accommodation.checkOutDate ? new Date(accommodation.checkOutDate) : null;

        if (checkInDate.getFullYear() === checkOutDate.getFullYear()) {
            return checkInDate.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short' }) + 
            " → " +
            checkOutDate.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short', year: 'numeric' })
        }

        if (checkInDate.getFullYear() !== checkOutDate.getFullYear()) {
            return checkInDate.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short', year: 'numeric' }) + 
            " → " +
            checkOutDate.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short', year: 'numeric' })
        }
    }

    const fetchAccommodations = async () => {
        const response = await getAccommodations(tripId);
        console.log(response);
        setAccommodations(response.data);
    };

    const handleEditAccommodation = async (accommodation) => {
        setDeletingAccommodationId(null);
        onEditAccommodation(accommodation);
    }

    const handleDelete = async (accommodationId) => {
        try {
            await deleteAccommodation(tripId, accommodationId);
            fetchAccommodations();
        } catch (error) {
            if (error.response?.data) {
                console.log(error.response.data);
            }
        } finally {
            setDeletingAccommodationId(null);
        }
    }

    const isCurrentStay = (accommodation) => {
        if (!selectedDay) return false;
        const day = new Date(selectedDay.date);
        const checkIn = new Date(accommodation.checkInDate);
        const checkOut = new Date(accommodation.checkOutDate);
        return day >= checkIn && day <= checkOut;
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <div className="accommodations-modal">
                    <div className="accommodations-title">
                        <p className="modal-title">Accommodations</p>
                    </div>

                    <div className="accommodation">
                                {accommodations.length === 0 ? (
                                    <p className="no-accommodations">No accommodations added.</p>
                                ) : (

                                    accommodations.map(accommodation => (
                                        <>
                                            {deletingAccommodationId === accommodation.id ? (
                                                    <div className="delete-activity-message activity-confirm-delete">
                                                        <p className="delete-title">Are you sure you want to delete this accommodation?</p>
                                                        <p className="delete-subtitle">This cannot be undone.</p>
                                                        <div className="delete-actions">
                                                            <button className="modal-btn-cancel" onClick={() => setDeletingAccommodationId(null)}>Cancel</button>
                                                            <button className="modal-btn-confirm" onClick={() => handleDelete(accommodation.id)}>Confirm Delete</button>
                                                        </div>
                                                    </div>
                                                    ) : (
                                                        <div className={`accommodation-content ${isCurrentStay(accommodation) ? 'current-stay' : ''} key={accommodation.id}`}>
                                                            <div className="accommodation-items">
                                                                <p className="accommodation-date">{getAccommodationDateString(accommodation)}</p>

                                                                <p className="modal-title">{accommodation.name}</p>

                                                                {accommodation.locationName ? (
                                                                    <p className="accommodation-location">📍 {accommodation.locationName}</p>
                                                                ) : (
                                                                    <p className="accommodation-location">📍 No location specified</p>
                                                                )}
                                                            </div>
                                                            <div className="accommodation-actions">
                                                                <button className="activity-action" onClick={() => handleEditAccommodation(accommodation)}>✏️</button>
                                                                <button className="activity-action" onClick={() => setDeletingAccommodationId(accommodation.id)}>🗑️</button>
                                                            </div>
                                                        </div>
                                                    )
                                                }
                                            </>
                                    ))
                                )
                            }
                            </div>

                    <div className="modal-actions" onClick={onAddAccommodation}>
                        <button className="activity-btn-add">+ Add Accommodation</button>
                    </div>
                </div>
            </div>
        </div>
    );
}