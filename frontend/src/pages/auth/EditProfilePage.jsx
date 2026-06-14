import '../auth/css/EditProfilePage.css';
import { updateProfile } from '../../api/userApi.js'
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import LoadingButton from '../../components/LoadingButton';
import { forgotPassword } from '../../api/authApi';

export default function EditProfilePage() {
    const { currentUser, login: updateUser } = useAuth();
    const navigate = useNavigate();

    const [username, setUsername] = useState(currentUser?.username || '');
    const [errors, setErrors] = useState({});
    const [saveSuccess, setSaveSuccess] = useState(false);
    const [passwordSuccess, setPasswordSuccess] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleSave = async () => {
        setPasswordSuccess(false);
        setLoading(true);
        setErrors({});
        setSaveSuccess(false);

        try {
            const response = await updateProfile({ username });
            updateUser(response.data);
            setSaveSuccess(true);
        } catch (error) {
            if (error.response?.data) {
                setErrors(error.response.data);
            }
        } finally {
            setLoading(false);
        }
    };

    const handleChangePassword = async () => {
        setLoading(true);
        setSaveSuccess(false);
        try {
            await forgotPassword({ email: currentUser.email });
            setPasswordSuccess(true);
        } catch (error) {
            console.log(error);
        } finally {
            setLoading(false);
        }
    };
    
    return (
        <div>
            <button className="back-btn" onClick={() => navigate(-1)}>
                    ⇦ Back
            </button>
            <div className="edit-profile-page">
                <div className="edit-profile-bg" />
                <h1 className="page-title">
                    Triplana.
                </h1>
                <p className="page-subtitle">
                    Plan Your Trips Smarter.
                </p>

                <h2 className="form-title">
                    Edit Profile
                </h2>

                <div className="edit-profile-form">
                    {saveSuccess && <p className="success-banner">Profile updated successfully!</p>}
                    {passwordSuccess && <p className="success-banner">Password reset email sent!</p>}
                    <div className="form-element">
                        <label className="input-label">Username</label>
                        <div className="username-field">
                            <input
                                type="text"
                                maxLength={255}
                                placeholder="John Doe"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                className={`input-field ${errors.username ? 'input-error-border' : ''}`}
                            />
                            
                            <LoadingButton 
                                onClick={handleSave}
                                className="save-username-btn" 
                                disabled={loading}
                            >
                                Save
                            </LoadingButton>
                        </div>

                        {errors.username && <p className="input-error">{errors.username}</p>}
                        
                    </div>

                    <LoadingButton 
                        onClick={() => navigate('/change-email')} 
                        className="edit-profile-btn" 
                        disabled={loading}
                    >
                        Change Email
                    </LoadingButton>

                    <LoadingButton 
                        onClick={handleChangePassword}
                        className="edit-profile-btn" 
                        disabled={loading}
                    >
                        Change Password
                    </LoadingButton>
                </div>
                
            </div>
        </div>
    );
}