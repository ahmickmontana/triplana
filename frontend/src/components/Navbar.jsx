import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import ProfileDropdown from './ProfileDropdown';
import './Navbar.css';

export default function Navbar() {
    const { currentUser } = useAuth();
    const navigate = useNavigate();
    const [dropdownOpen, setDropdownOpen] = useState(false);

    return (
        <nav className="navbar">
            <span 
            className="navbar-logo" 
            onClick={() => currentUser && navigate('/trips')}
            style={{ cursor: currentUser ? 'pointer' : 'default' }}
        >
            Triplana.
        </span>
            <div className="navbar-actions">
                {currentUser ? (
                    <>
                        <button 
                            className="navbar-profile-btn"
                            onClick={() => setDropdownOpen(!dropdownOpen)}
                        >
                            Profile
                        </button>
                        {dropdownOpen && (
                            <ProfileDropdown onClose={() => setDropdownOpen(false)} />
                        )}
                    </>
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