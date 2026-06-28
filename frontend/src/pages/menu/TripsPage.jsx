import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getTrips } from '../../api/tripApi';
import Navbar from '../../components/Navbar';
import CreateTripModal from './CreateTripModal';
import ViewTripModal from './ViewTripModal';
import './TripsPage.css';
import defaultTripImg from '../../assets/images/default-trip-img.jpg';

export default function TripsPage() {
    const { currentUser, loading } = useAuth();
    const navigate = useNavigate();
    const [trips, setTrips] = useState([]);
    const [tripsLoading, setTripsLoading] = useState(true);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [successMessage, setSuccessMessage] = useState(null);

    const [selectedTrip, setSelectedTrip] = useState(null);

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
        setShowCreateModal(!showCreateModal);
    }

    const formatDate = (dateStr) => {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        return date.toLocaleDateString('en-NZ', { day: 'numeric', month: 'short', year: 'numeric' });
    };

    const handleTripCreated = () => {
        getTrips();
        setSuccessMessage('Trip created successfully!');
        setTimeout(() => setSuccessMessage(null), 3000);
    };

    return (
        <div className="trips-page">
            <Navbar />
            <div className="main-bg" />
            <div className="trips-content">

                {showCreateModal && <CreateTripModal 
                                    onClose={() => setShowCreateModal(false)}
                                    onTripCreated={handleTripCreated}
                                />}

                {selectedTrip && <ViewTripModal 
                                    trip={selectedTrip}
                                    onClose={() => setSelectedTrip(null)}
                                />}

                <div className="page-message">
                    {successMessage && <p className="success-banner">{successMessage}</p>}
                </div>

                <div className="trips-header">
                    <h1 className="trips-title">My Trips</h1>
                    <button className="create-trip-btn" onClick={handleCreateTripModal}>
                        + Create Trip
                    </button>
                </div>

                {trips.length === 0 ? (
                    <div className="trips-empty">
                        <p className="trips-empty-message">No trips created yet.</p>
                        <button className="create-trip-btn" onClick={handleCreateTripModal}>
                            Create Trip
                        </button>
                    </div>
                ) : (
                    <div className="trips-grid">
                        {trips.map(trip => (
                            <div key={trip.id} className="trip-card" onClick={() => setSelectedTrip(trip)}>
                                <div className="trip-card-image">
                                    <img 
                                        src={trip.coverImagePath ? `http://localhost:8080${trip.coverImagePath}` : defaultTripImg}
                                        alt={trip.name} 
                                    />
                                </div>
                                <div className="trip-card-info">
                                    <h3 className="trip-card-name">{trip.name}</h3>
                                    <p className="trip-card-dates">
                                        {trip.startDate && trip.endDate 
                                            ? `${formatDate(trip.startDate)} to ${formatDate(trip.endDate)}`
                                            : trip.startDate 
                                            ? formatDate(trip.startDate)
                                            : ''}
                                    </p>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}