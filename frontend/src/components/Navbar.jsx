import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

export default function Navbar({ showLogout, onLogout }) {
    const { currentUser } = useAuth();
    const navigate = useNavigate();

    return (
        <nav className="navbar">
            <span className="navbar-logo">Triplana.</span>
            <div className="navbar-actions">
                {showLogout ? (
                    <button className="navbar-logout-btn" onClick={onLogout}>
                        Log Out
                    </button>
                ) : (
                    <>
                        <button className="navbar-login-btn" onClick={() => navigate('/login')}>Log in</button>
                        <button className="navbar-register-btn" onClick={() => navigate('/register')}>Register</button>
                    </>
                )}
            </div>
        </nav>
    );
}