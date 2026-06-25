import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getTrips } from '../../api/tripApi';
import Navbar from '../../components/Navbar';
import CreateTripModal from './CreateTripModal';
import './TripsPage.css';
import defaultTripImg from '../../assets/images/default-trip-img.jpg';

export default function TripsPage() {
    const { currentUser, loading } = useAuth();
    const navigate = useNavigate();
    const [trips, setTrips] = useState([]);
    const [tripsLoading, setTripsLoading] = useState(true);
    const [createTrip, setCreateTrip] = useState(false);

    useEffect(() => {
        if (!loading && !currentUser) {
            navigate('/');
        }
    }, [currentUser, loading]);

    useEffect(() => {
        const fetchTrips = async () => {
            try {
                const response = await getTrips();
                setTrips(response.data);
            } catch (error) {
                console.log(error);
            } finally {
                setTripsLoading(false);
            }
        };

        if (currentUser) {
            fetchTrips();
        }
    }, [currentUser]);

    if (loading) return null;

    const handleCreateTripModal = async (status) => {
        setCreateTrip(!createTrip);
    }

    return (
        <div className="trips-page">
            <Navbar />
            <div className="main-bg" />
            <div className="trips-content">

                {createTrip && <CreateTripModal onClose={() => setCreateTrip(false)} />}

                <div className="trips-header">
                    <h1 className="trips-title">My Trips</h1>
                    <button className="create-trip-btn" onClick={handleCreateTripModal}>
                        + Create Trip
                    </button>
                </div>

                {trips.length === 0 ? (
                    <div className="trips-empty">
                        <p className="trips-empty-message">No trips created yet.</p>
                        <button className="create-trip-btn" onClick={() => navigate('/create-trip')}>
                            Create Trip
                        </button>
                    </div>
                ) : (
                    <div className="trips-grid">
                        {trips.map(trip => (
                            <div key={trip.id} className="trip-card">
                                <div className="trip-card-image">
                                    <img 
                                        src={trip.coverImagePath || defaultTripImg} 
                                        alt={trip.name} 
                                    />
                                </div>
                                <div className="trip-card-info">
                                    <h3 className="trip-card-name">{trip.name}</h3>
                                    <p className="trip-card-dates">{trip.startDate} to {trip.endDate}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}