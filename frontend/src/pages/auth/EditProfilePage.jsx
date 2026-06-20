import '../auth/css/EditProfilePage.css';
import { updateProfile } from '../../api/userApi.js'
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import LoadingButton from '../../components/LoadingButton';
import { forgotPassword, initiateChangeEmail } from '../../api/authApi';

export default function EditProfilePage() {
    const { currentUser, login: updateUser } = useAuth();
    const navigate = useNavigate();

    const [errors, setErrors] = useState({});
    const [saveSuccess, setSaveSuccess] = useState(false);
    const [passwordSuccess, setPasswordSuccess] = useState(false);
    const [initiateChangeEmailSuccess, setInitiateChangeEmailSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const [username, setUsername] = useState('');

    useEffect(() => {
        if (currentUser) {
            setUsername(currentUser.username || '');
        }
    }, [currentUser]);

    const handleSave = async () => {
        setLoading(true);
        setErrors({});
        setSaveSuccess(false);
        setPasswordSuccess(false);
        setInitiateChangeEmailSuccess(false);

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

    const handleChangeEmail = async () => {
        setLoading(true);
        setSaveSuccess(false);
        setPasswordSuccess(false);
        setInitiateChangeEmailSuccess(false);
        try {
            await initiateChangeEmail();
            setInitiateChangeEmailSuccess(true);
        } catch (error) {
            console.log(error);
        } finally {
            setLoading(false);
        }
    };

    const handleChangePassword = async () => {
        setLoading(true);
        setSaveSuccess(false);
        setPasswordSuccess(false);
        setInitiateChangeEmailSuccess(false);
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
                    {initiateChangeEmailSuccess && <p className="success-banner">Change email address email sent!</p>}
                    <div className="form-element">
                        <label className="input-label">Username</label>
                        <div className="username-field">
                            <input
                                type="text"
                                maxLength={50}
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
                        onClick={handleChangeEmail} 
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