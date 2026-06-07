import '../auth/css/RegisterPage.css';
import { register } from '../../api/authApi.js'
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import LoadingButton from '../../components/LoadingButton';


export default function RegisterPage() {
    const navigate = useNavigate();
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [errors, setErrors] = useState({});
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async () => {
        setLoading(true);
        setErrors({});

        try {
            await register({ username, email, password, confirmPassword });
            navigate('/check-email', { state: { email: email } });
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
            <div className="register-page">
                <div className="register-bg" />
                <h1 className="page-title">
                    Triplana.
                </h1>
                <p className="page-subtitle">
                    Plan Your Trips Smarter.
                </p>

                <h2 className="form-title">
                    Register
                </h2>

                <div className="register-form">
                    <div className="form-element">
                        <label className="input-label">Username</label>
                        <input
                            type="text"
                            placeholder="John Doe"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            className={`input-field ${errors.username ? 'input-error-border' : ''}`}
                        />
                        {errors.username && <p className="input-error">{errors.username}</p>}
                    </div>

                    <div className="form-element">
                        <label className="input-label">Email</label>
                        <input
                            type="text"
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
                    </div>

                    <div className="form-element">
                        <label className="input-label">Confirm Password</label>
                        <div className={`input-wrapper ${errors.confirmPassword ? 'input-error-border' : ''}`}>
                            <input
                                type={showConfirmPassword ? 'text' : 'password'}
                                placeholder=""
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                className="input-field"
                            />
                            <button 
                                className="show-btn"
                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                            >
                                {showConfirmPassword ? 'Hide' : 'Show'}
                            </button>
                        </div>
                        {errors.confirmPassword && <p className="input-error">{errors.confirmPassword}</p>}
                        {errors.message && <p className="input-error">{errors.message}</p>}
                    </div>

                    <LoadingButton 
                        onClick={handleSubmit} 
                        className="register-btn" 
                        disabled={loading}
                    >
                        Register
                    </LoadingButton>
                </div>
                
            </div>
        </div>
    );
}