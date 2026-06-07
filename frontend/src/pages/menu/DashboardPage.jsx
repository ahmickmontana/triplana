import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';

export default function DashboardPage() {
    const { currentUser, loading, logout } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!loading && !currentUser) {
            navigate('/');
        }
    }, [currentUser, loading]);

    if (loading) return null;

    return (
        <div>
            <Navbar showLogout={true} onLogout={async () => {
                await logout();
                navigate('/');
            }} />
            <p>Welcome, {currentUser?.username}!</p>
        </div>
    );
}