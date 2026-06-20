import '../auth/css/LoginPage.css'; // Pretty much the same page as the login page, only difference is the behaviour.
import { submitChangeEmail, verifyChangeEmailToken } from '../../api/authApi.js'
import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import LoadingButton from '../../components/LoadingButton';


export default function ChangeEmailPage() {
    const navigate = useNavigate();
    const [showPassword, setShowPassword] = useState(false);
    const [errors, setErrors] = useState({});
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');

    const [tokenValid, setTokenValid] = useState(null);
    const [tokenError, setTokenError] = useState(null);

    useEffect(() => {
        const verify = async () => {
            try {
                await verifyChangeEmailToken(token);
                setTokenValid(true);
            } catch (error) {
                setTokenValid(false);
                setTokenError(error.response?.data?.message || 'This link is invalid or expired.');
            }
        };

        if (token) {
            verify();
        } else {
            setTokenValid(false);
            setTokenError('This link is invalid or expired.');
        }
    }, []);

    const handleSubmit = async () => {
    setLoading(true);
    setErrors({});

        try {
            await submitChangeEmail({ token, newEmail: email, password });
            setSuccess(true);
        } catch (error) {
            if (error.response?.data) {
                setErrors(error.response.data);
            }
        } finally {
            setLoading(false);
        }
    }

    if (tokenValid === false) {
        return (
            <div>
                <div className="login-page">
                    <div className="login-bg" />
                    <h1 className="page-title">Triplana.</h1>
                    <p className="page-subtitle">Plan Your Trips Smarter.</p>
                    <h2 className="form-title">Change Email</h2>
                    <p className="verify-error">{tokenError}</p>
                    <button className="login-btn" onClick={() => navigate('/edit-profile')}>
                        Back to Edit Profile
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div>
            <div className="login-page">
                <div className="login-bg" />
                <h1 className="page-title">
                    Triplana.
                </h1>
                <p className="page-subtitle">
                    Plan Your Trips Smarter.
                </p>
        
                {success && <p className="success-banner">A confirmation email has been sent to {email}. Please check your inbox to confirm the change.</p>}

                <h2 className={`form-title ${success ? 'form-title-no-margin' : 'form-title-margin'}`}>
                    Change Email
                </h2>

                <div className="login-form">
                    <div className="form-element">
                        <label className="input-label">Email</label>
                        <input
                            type="text"
                            maxLength={255}
                            placeholder="example@email.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className={`input-field ${errors.newEmail ? 'input-error-border' : ''}`}
                        />
                        {errors.newEmail && <p className="input-error">{errors.newEmail}</p>}
                    </div>

                    <div className="form-element">
                        <label className="input-label">Current Password</label>
                        <div className={`input-wrapper ${errors.password ? 'input-error-border' : ''}`}>
                            <input
                                type={showPassword ? 'text' : 'password'}
                                placeholder=""
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="input-field"
                            />
                            <button 
                                className="show-btn"
                                onClick={() => setShowPassword(!showPassword)}
                            >
                                {showPassword ? 'Hide' : 'Show'}
                            </button>
                        </div>
                        {errors.password && <p className="input-error">{errors.password}</p>}
                        {errors.message && <p className="input-error">{errors.message}</p>}
                    </div>

                    <LoadingButton 
                        onClick={handleSubmit} 
                        className="login-btn" 
                        disabled={loading}
                    >
                        Submit
                    </LoadingButton>
                </div>
            </div>
        </div>
    )
}