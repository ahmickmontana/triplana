import './Navbar.css';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom'

export default function Navbar() {
    const { currentUser, logout } = useAuth();
    const navigate = useNavigate();

    return (
        <nav className="navbar">
            <span className="navbar-logo">Triplana.</span>
            {currentUser && (
                <button className="navbar-profile-btn">
                    Profile
                </button>
            )}
            {logout && (
                <div className="account-btn">
                    <button className="navbar-login-btn" onClick={() => navigate('/login')}> Log in </button>
                    <button className="navbar-register-btn" onClick={() => navigate('/register')}> Register </button>
                </div>
            )}
            
            
        </nav>
    );
}