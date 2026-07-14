import Navbar from '../../components/Navbar';
import { getTrip, getTripDays } from '../../api/tripApi';
import { getActivities, deleteActivity } from '../../api/activityApi';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './TripPlannerPage.css';
import AddActivityModal from './AddActivityModal';
import EditActivityModal from './EditActivityModal';


export default function TripPlannerPage() {
    const { id } = useParams();
    const [ trip, setTrip ] = useState(null);
    const [days, setDays] = useState([]);
    const [selectedDay, setSelectedDay] = useState(null);
    const [activities, setActivities] = useState([]);

    const [showAddActivityModal, setShowAddActivityModal] = useState(false);
    const [editingActivity, setEditingActivity] = useState(null);
    const [deletingActivityId, setDeletingActivityId] = useState(null);

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

    const formatTime = (time) => {
        if (time === null) return;
        const hour = Number(time.slice(0, 2));
        const minute = time.slice(3, 5);
        const meridiem = hour > 11 ? "pm" : "am";

        return (hour > 12 ? (hour % 12) : hour) + ":" + minute + meridiem;
    }

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
                        <button className="planner-btn-confirm">View Accommodations</button>
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

                                activities.map(activity => (
                                    <div>
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
                                                        <div className="activity-items">
                                                            {activity.startTime && activity.endTime ? (
                                                                <p className="activity-time">{formatTime(activity.startTime)} - {formatTime(activity.endTime)}</p>
                                                            ) : activity.startTime ? (
                                                                <p className="activity-time">{formatTime(activity.startTime)}</p>
                                                            ) : (
                                                                <p className="activity-time">No specified time</p>
                                                            )}

                                                            <p className="activity-title">{activity.title}</p>

                                                            <p className="activity-description">{activity.description}</p>

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
                                        </div>
                                ))
                            )
                        }
                        </div>
                        <div className="activities-action">
                            <button className="activity-btn-add" onClick={handleCreateActivityModal}>+ Add Activity</button>
                        </div>
                    </div>
                    <div className="trip-map">
                        Map
                    </div>
                    <div className="trip-route">
                        Route
                    </div>
                </div>
            </div>
        </div>
    )
}