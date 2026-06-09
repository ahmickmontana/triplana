import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import './ProfileDropdown.css';

export default function ProfileDropdown({ onClose }) {
    const { currentUser, logout } = useAuth();
    const navigate = useNavigate();
    const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);

    const handleLogout = async () => {
        await logout();
        onClose();
        navigate('/');
    };

    const handleEditProfile = () => {
        onClose();
        navigate('/edit-profile');
    };

    return (
        <>
            <div className="dropdown-overlay" onClick={onClose} />
            <div class="dropdown">
                <div className="dropdown-user-info">
                    <p className="dropdown-username">{currentUser?.username}</p>
                    <p className="dropdown-email">{currentUser?.email}</p>
                </div>

                <hr className="dropdown-divider" />
                <button className="dropdown-btn" onClick={handleEditProfile}>
                    Edit Profile
                </button>
                <button class="dropdown-btn dropdown-btn-logout" onClick={() => setShowLogoutConfirm(true)}>
                    Logout
                </button>
            </div>

            {showLogoutConfirm && (
                <div className="modal-overlay">
                    <div className="modal">
                        <p className="modal-message">Are you sure you want to log out?</p>
                        <div className="modal-actions">
                            <button className="modal-btn-cancel" onClick={() => setShowLogoutConfirm(false)}>
                                Cancel
                            </button>
                            <button className="modal-btn-confirm" onClick={handleLogout}>
                                Confirm
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    )
}