import './Navbar.css';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
    const { currentUser, logout } = useAuth();

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
                    <button className="navbar-login-btn"> Log in </button>
                    <button className="navbar-register-btn"> Register </button>
                </div>
            )}
            
            
        </nav>
    );
}