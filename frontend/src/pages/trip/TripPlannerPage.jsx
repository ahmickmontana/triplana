import Navbar from '../../components/Navbar';
import { Map, AdvancedMarker, Polyline } from '@vis.gl/react-google-maps';
import { decode } from '@googlemaps/polyline-codec';
import { getTrip, getTripDays } from '../../api/tripApi';
import { getActivities, deleteActivity } from '../../api/activityApi';
import { computeRoute } from '../../api/routingApi';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './TripPlannerPage.css';
import AddActivityModal from './AddActivityModal';
import EditActivityModal from './EditActivityModal';
import ViewAccommodations from './ViewAccommodationsModal';
import AddAccommodationModal from './AddAccommodationModal';
import EditAccommodationModal from './EditAccommodationModal';


export default function TripPlannerPage() {
    const { id } = useParams();
    const [ trip, setTrip ] = useState(null);
    const [days, setDays] = useState([]);
    const [selectedDay, setSelectedDay] = useState(null);
    const [activities, setActivities] = useState([]);
    const [selectedActivityIds, setSelectedActivityIds] = useState([]);

    const [showAddActivityModal, setShowAddActivityModal] = useState(false);
    const [editingActivity, setEditingActivity] = useState(null);
    const [deletingActivityId, setDeletingActivityId] = useState(null);

    const [viewingAccommodations, setViewingAccommodations] = useState(false);
    const [showAddAccommodation, setShowAddAccommodation] = useState(false);
    const [showEditAccommodation, setShowEditAccommodation] = useState(false);
    const [editingAccommodation, setEditingAccommodation] = useState(null);

    const [route, setRoute] = useState(null);

    const sortedActivities = [
        ...activities.filter(a => a.startTime !== null).sort((a, b) => a.startTime.localeCompare(b.startTime)),
        ...activities.filter(a => a.startTime === null)
    ];

    const selectedActivities = sortedActivities
    .filter(a => selectedActivityIds.includes(a.id) && a.latitude && a.longitude);

    useEffect(() => {
        const fetchTrip = async () => {
            const response = await getTrip(id);
            console.log(response);
            setTrip(response.data);
        };

        const fetchDays = async () => {
            const response = await getTripDays(id);
            setDays(response.data);
            setSelectedDay(response.data[0]);
        };

        fetchTrip();
        fetchDays();
    }, [id]);

    useEffect(() => {
        if (!selectedDay) return;
        fetchActivities();
    }, [selectedDay]);

    useEffect(() => {
        setRoute(null);
    }, [selectedActivityIds]);

    const fetchActivities = async () => {
        if (!selectedDay) return;
        const response = await getActivities(id, selectedDay.id);
        setActivities(response.data);
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        return date.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short', year: 'numeric' });
    };

    const getTripDateString = () => {
        const startDate = new Date(trip.startDate);
        const endDate = trip.endDate ? new Date(trip.endDate) : null;
        if (!endDate) { 
            return startDate.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short', year: 'numeric' }) + " • 1 Day";
        }

        if (startDate.getFullYear() === endDate.getFullYear()) {
            return startDate.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short' }) + 
            " → " +
            endDate.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short', year: 'numeric' }) + " • " + 
            (endDate - startDate) / (1000 * 60 * 60 * 24) + 
            " Days";
        }

        if (startDate.getFullYear() !== endDate.getFullYear()) {
            return startDate.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short', year: 'numeric' }) + 
            " → " +
            endDate.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short', year: 'numeric' }) + " • " + 
            (endDate - startDate) / (1000 * 60 * 60 * 24) + 
            " Days";
        }
    }

    const handleCreateActivityModal = async (status) => {
        setDeletingActivityId(null);
        setShowAddActivityModal(!showAddActivityModal);
        if (!selectedDay) return;
        const response = await getActivities(id, selectedDay.id);
        setActivities(response.data);
    }

    const handleEditActivity = async (activity) => {
        setDeletingActivityId(null);
        setEditingActivity(activity);
    }

    const handleAddAccommodation = async () => {
        setViewingAccommodations(false);
        setShowAddAccommodation(true);
    }

    const handleEditAccommodation = async (accommodation) => {
        setViewingAccommodations(false);
        setShowEditAccommodation(true);
        setEditingAccommodation(accommodation);
    }

    const handleAccommodationAdded = () => {
        setShowAddAccommodation(false);
        setViewingAccommodations(true);
    };

    const handleAccommodationUpdated = () => {
        setShowEditAccommodation(false);
        setViewingAccommodations(true);
    };

    const handleDelete = async (activityId) => {
        try {
            await deleteActivity(trip.id, selectedDay.id, activityId);
            fetchActivities();
        } catch (error) {
            if (error.response?.data) {
                console.log(error.response.data);
            }
        } finally {
            setDeletingActivityId(null);
        }
    }

    const toggleActivity = async (activityId) => {
        setSelectedActivityIds(prev =>
            prev.includes(activityId)
                ? prev.filter(id => id !== activityId)
                : [...prev, activityId]
        );

        console.log(selectedActivityIds);
    };

    const handleShowRoute = async () => {
        try {
            const selectedActivities = sortedActivities
                .filter(a => selectedActivityIds.includes(a.id) && a.latitude && a.longitude);

            if (selectedActivities.length < 2) return;

            const response = await computeRoute({
                originLat: selectedActivities[0].latitude,
                originLng: selectedActivities[0].longitude,
                destLat: selectedActivities[selectedActivities.length - 1].latitude,
                destLng: selectedActivities[selectedActivities.length - 1].longitude,
                travelMode: 'WALK'
            });

            console.log(response.data);
            setRoute(response.data);
        } catch (error) {
            if (error.response?.data) {
                console.log(error.response.data);
            }
        }
    }

    const formatTime = (time) => {
        if (time === null) return;
        const hour = Number(time.slice(0, 2));
        const minute = time.slice(3, 5);
        const meridiem = hour > 11 ? "pm" : "am";

        return (hour > 12 ? (hour % 12) : hour) + ":" + minute + meridiem;
    }

    const formatDuration = (durationStr) => {
        const seconds = parseInt(durationStr.replace('s', ''));
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        
        if (hours > 0) return `${hours}h ${minutes}min`;
        return `${minutes}min`;
    };

    if (!trip) return null;

    return (
        <div className="planner-page">
            <Navbar/>
            <div className="planner-content">

                {showAddActivityModal && <AddActivityModal 
                                                    tripId={trip.id}
                                                    dayId={selectedDay.id}
                                                    onClose={() => setShowAddActivityModal(false)}
                                                    onActivityCreated={fetchActivities}
                />}

                {viewingAccommodations && <ViewAccommodations 
                                                    tripId={trip.id}
                                                    selectedDay={selectedDay}
                                                    onClose={() => setViewingAccommodations(false)}
                                                    onAddAccommodation={handleAddAccommodation}
                                                    onEditAccommodation={handleEditAccommodation}
                />}

                 {showAddAccommodation && <AddAccommodationModal 
                                                    tripId={trip.id}
                                                    onClose={() => setShowAddAccommodation(false)}
                                                    onAccommodationAdded={handleAccommodationAdded}
                />}

                {showEditAccommodation && <EditAccommodationModal 
                                                    tripId={trip.id}
                                                    accommodation={editingAccommodation}
                                                    onClose={() => setShowEditAccommodation(false)}
                                                    onAccommodationUpdated={handleAccommodationAdded}
                />}

                {editingActivity && (
                    <EditActivityModal
                        activity={editingActivity}
                        tripId={id}
                        dayId={selectedDay.id}
                        onClose={() => setEditingActivity(null)}
                        onActivityEdited={fetchActivities}
                    />
                )}

                <div className="planner-header">
                    <div className="planner-header-info">
                        <h1 className="planner-title">{trip.name}</h1>
                        <p className="planner-date">{getTripDateString()}</p>
                    </div>
                    <div className="planner-header-action">
                        <button className="planner-btn-confirm" onClick={() => setViewingAccommodations(true)}>View Accommodations</button>
                    </div>
                </div>

                <div className="planner-items">
                    <div className="day-activities">
                        <div className="day-selector">
                            <button 
                                className="day-arrow"
                                onClick={() => setSelectedDay(days[days.indexOf(selectedDay) - 1])}
                                style={{ visibility: days.indexOf(selectedDay) === 0 ? 'hidden' : 'visible' }}
                            >
                                ←
                            </button>
                            <span className="day-label">
                                Day {selectedDay?.dayNumber} ({formatDate(selectedDay?.date)})
                            </span>
                            <button 
                                className="day-arrow"
                                onClick={() => setSelectedDay(days[days.indexOf(selectedDay) + 1])}
                                style={{ visibility: days.indexOf(selectedDay) === days.length - 1 ? 'hidden' : 'visible' }}
                            >
                                →
                            </button>
                        </div>
                        <div className="day-activity">
                            {activities.length === 0 ? (
                                <p className="no-activity">No activities added for this day.</p>
                            ) : (

                                sortedActivities.map(activity => (
                                    <>
                                        {deletingActivityId === activity.id ? (
                                                <div className="delete-activity-message activity-confirm-delete">
                                                    <p className="delete-title">Are you sure you want to delete this activity?</p>
                                                    <p className="delete-subtitle">This cannot be undone.</p>
                                                    <div className="delete-actions">
                                                        <button className="modal-btn-cancel" onClick={() => setDeletingActivityId(null)}>Cancel</button>
                                                        <button className="modal-btn-confirm" onClick={() => handleDelete(activity.id)}>Confirm Delete</button>
                                                    </div>
                                                </div>
                                                ) : (
                                                    <div className="activity-content" key={activity.id}>
                                                        <div className="activity-checkbox">
                                                            <input 
                                                                className="activity-route-checkbox"
                                                                type="checkbox"
                                                                id={`checkbox-${activity.id}`}
                                                                checked={selectedActivityIds.includes(activity.id)}
                                                                onChange={() => toggleActivity(activity.id)}
                                                            />
                                                            <label htmlFor={`checkbox-${activity.id}`} className="checkbox-label" />
                                                        </div>
                                                        <div className="activity-items">
                                                            {activity.startTime && activity.endTime ? (
                                                                <p className="activity-time">{formatTime(activity.startTime)} - {formatTime(activity.endTime)}</p>
                                                            ) : activity.startTime ? (
                                                                <p className="activity-time">{formatTime(activity.startTime)}</p>
                                                            ) : (
                                                                <p className="activity-time">No specified time</p>
                                                            )}

                                                            <p className="activity-title">{activity.title}</p>

                                                            <p className="activity-description">{activity.description || '\u00A0'}</p>

                                                            {activity.locationName ? (
                                                                <p className="activity-location">📍 {activity.locationName}</p>
                                                            ) : (
                                                                <p className="activity-location">📍 No location specified</p>
                                                            )}
                                                        </div>
                                                        <div className="activity-actions">
                                                            <button className="activity-action" onClick={() => handleEditActivity(activity)}>✏️</button>
                                                            <button className="activity-action" onClick={() => setDeletingActivityId(activity.id)}>🗑️</button>
                                                        </div>
                                                    </div>
                                                )
                                            }
                                        </>
                                ))
                            )
                        }
                        </div>
                        <div className="activities-action">
                            <button className="activity-btn-add" onClick={handleCreateActivityModal}>+ Add Activity</button>
                        </div>
                    </div>
                    <div className="trip-map">
                        <Map
                            defaultCenter={{ lat: 35.6595, lng: 139.7004 }}
                            defaultZoom={13}
                            style={{ width: '100%', height: '100%' }}
                            gestureHandling="greedy"
                            mapId="triplana-map"
                        >
                            {sortedActivities
                                .filter(a => a.latitude && a.longitude)
                                .map((activity, index) => (
                                    <AdvancedMarker
                                        key={activity.id}
                                        position={{ lat: activity.latitude, lng: activity.longitude }}
                                    >
                                        <div className="activity-pin">
                                            {index + 1}
                                        </div>
                                    </AdvancedMarker>
                                ))
                            }

                            {route && (
                                <Polyline
                                    path={decode(route.encodedPolyline).map(([lat, lng]) => ({ lat, lng }))}
                                    strokeColor="#3B82F6"
                                    strokeWeight={4}
                                />
                            )}
                        </Map>
                    </div>
                    <div className="trip-route">
                        {route && (
                            <div className="route-details">
                                {selectedActivities.map((activity, index) => (
                                    <div key={activity.id}>
                                        <p className="route-activity-title">{activity.title}</p>
                                        <div className="route-stop">
                                            <p>📍{activity.locationName || activity.title}</p>
                                        </div>
                                        {index < selectedActivities.length - 1 && (
                                            <div className="route-travel">
                                                <div className="route-arrow">↓</div>
                                                <div className="route-segment">
                                                    <p>{route.legs[index]?.distanceText}</p>
                                                    <p>•</p>
                                                    <p>{formatDuration(route.legs[index]?.duration)}</p>
                                                    <p>•</p>
                                                    <p>🚶 Walking</p>
                                                </div>
                                                <div className="route-arrow">↓</div>
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>
                        )}
                        {selectedActivityIds.length >= 2 && (
                            <div className="route-footer">
                                {selectedActivityIds.length >= 2 && (
                                    <button className="activity-btn-add" onClick={handleShowRoute}>
                                        Show Route
                                    </button>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}