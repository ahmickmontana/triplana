import Navbar from '../../components/Navbar';
import { getTrip, getTripDays } from '../../api/tripApi';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './TripPlannerPage.css';


export default function TripPlannerPage() {
    const { id } = useParams();
    const [ trip, setTrip ] = useState(null);
    const [days, setDays] = useState([]);
    const [selectedDay, setSelectedDay] = useState(null);

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

    if (!trip) return null;

    return (
        <div className="planner-page">
            <Navbar/>
            <div className="planner-content">
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
                            Activities
                        </div>
                        <div className="activities-action">
                            <button className="activity-btn-add">+ Add Activity</button>
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