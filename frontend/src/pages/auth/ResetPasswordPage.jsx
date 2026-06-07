import '../auth/css/ResetPasswordPage.css';
import { resetPassword } from '../../api/authApi.js'
import { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import LoadingButton from '../../components/LoadingButton';


export default function ResetPasswordPage() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');

    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmNewPassword, setShowConfirmNewPassword] = useState(false);
    const [errors, setErrors] = useState({});
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async () => {
        setLoading(true);
        setErrors({});

        try {
            await resetPassword({ token, newPassword, confirmPassword });
            navigate('/login', { state: { message: 'Password reset successfully! You may now log in.' } });
        } catch (error) {
            if (error.response?.data) {
                setErrors(error.response.data);
            }
        } finally {
            setLoading(false);
        }
    }
    
    return (
        <div>
            <div className="reset-password-page">
                <div className="reset-password-bg" />
                <h1 className="page-title">
                    Triplana.
                </h1>
                <p className="page-subtitle">
                    Plan Your Trips Smarter.
                </p>

                <h2 className="form-title">
                    Reset Password
                </h2>

                <div className="reset-password-form">

                    <div className="form-element">
                        <label className="input-label">Password</label>
                        <div className={`input-wrapper ${errors.newPassword ? 'input-error-border' : ''}`}>
                            <input
                                type={showNewPassword ? 'text' : 'password'}
                                placeholder=""
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                className="input-field"
                            />
                            <button 
                                className="show-btn"
                                onClick={() => setShowNewPassword(!showNewPassword)}
                            >
                                {showNewPassword ? 'Hide' : 'Show'}
                            </button>
                        </div>
                        {errors.newPassword && <p className="input-error">{errors.newPassword}</p>}
                    </div>

                    <div className="form-element">
                        <label className="input-label">Confirm Password</label>
                        <div className={`input-wrapper ${errors.confirmPassword ? 'input-error-border' : ''}`}>
                            <input
                                type={showConfirmNewPassword ? 'text' : 'password'}
                                placeholder=""
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                className="input-field"
                            />
                            <button 
                                className="show-btn"
                                onClick={() => setShowConfirmNewPassword(!showConfirmNewPassword)}
                            >
                                {showConfirmNewPassword ? 'Hide' : 'Show'}
                            </button>
                        </div>
                        {errors.confirmPassword && <p className="input-error">{errors.confirmPassword}</p>}
                        {errors.message && <p className="input-error">{errors.message}</p>}
                    </div>

                    <LoadingButton 
                        onClick={handleSubmit} 
                        className="reset-password-btn" 
                        disabled={loading}
                    >
                        Confirm
                    </LoadingButton>
                </div>
                
            </div>
        </div>
    );
}