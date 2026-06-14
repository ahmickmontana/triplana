import '../auth/css/LoginPage.css';
import { login } from '../../api/authApi.js'
import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import LoadingButton from '../../components/LoadingButton';


export default function LoginPage() {
    const navigate = useNavigate();
    const [showPassword, setShowPassword] = useState(false);
    const [errors, setErrors] = useState({});
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const location = useLocation();
    const successMessage = location.state?.message;
    const [loading, setLoading] = useState(false);

    const { login: setUser } = useAuth();

    const handleSubmit = async () => {
        setLoading(true);
        setErrors({});

        try {
            const response = await login({ email, password });
            const data = response.data;

            if (data.status === 'success') {
                setUser(data.user);
                navigate('/trips');
            } else if (data.status === 'unverified') {
                navigate('/check-email');
            }
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
            <button className="back-btn" onClick={() => navigate('/')}>
                    ⇦ Back
            </button>
            <div className="login-page">
                <div className="login-bg" />
                <h1 className="page-title">
                    Triplana.
                </h1>
                <p className="page-subtitle">
                    Plan Your Trips Smarter.
                </p>

                {successMessage && <p className="success-banner">{successMessage}</p>}

                <h2 className={`form-title ${successMessage ? 'form-title-no-margin' : 'form-title-margin'}`}>
                    Login
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
                            className={`input-field ${errors.email ? 'input-error-border' : ''}`}
                        />
                        {errors.email && <p className="input-error">{errors.email}</p>}
                    </div>

                    <div className="form-element">
                        <label className="input-label">Password</label>
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

                    <a className="forgot-link" onClick={() => navigate('/forgot-password')}>
                        Forgot password?
                    </a>

                    <LoadingButton 
                        onClick={handleSubmit} 
                        className="login-btn" 
                        disabled={loading}
                    >
                        Log In
                    </LoadingButton>
                </div>
                
            </div>
        </div>
    );
}