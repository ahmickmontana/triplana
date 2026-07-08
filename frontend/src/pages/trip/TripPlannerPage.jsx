import Navbar from '../../components/Navbar';
import { getTrip } from '../../api/tripApi';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './TripPlannerPage.css';


export default function TripPlannerPage() {
    const { id } = useParams();
    const [ trip, setTrip ] = useState(null);

    useEffect(() => {
        const fetchTrip = async () => {
            const response = await getTrip(id);
            console.log(response);
            setTrip(response.data);
        };
        fetchTrip();
    }, [id]);

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
            </div>
        </div>
    )
}