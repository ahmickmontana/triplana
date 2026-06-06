import '../auth/css/LoginPage.css';
import { login } from '../../api/authApi.js'
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';


export default function LoginPage() {
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [errors, setErrors] = useState({});
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const { login: setUser } = useAuth();

    const handleSubmit = async () => {
        setErrors({});

        try {
            const response = await login({ email, password });
            const data = response.data;

            if (data.status === 'success') {
                console.log("hello it succeed.")
                setUser(data.user);
            } else if (data.status === 'unverified') {
                console.log("hello check email.")
                navigate('/check-email');
            }
        } catch (error) {
            console.log(error.response?.data);
            if (error.response?.data) {
                setErrors(error.response.data);
            }
        }
    }
    
    return (
        <div className="login-page">
            <div className="login-bg" />
            <h1 className="page-title">
                Triplana.
            </h1>
            <p className="page-subtitle">
                Plan Your Trips Smarter.
            </p>

            <h2 className="form-title">
                Login
            </h2>

            <div className="login-form">
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
                    {errors.message && <p className="input-error">{errors.message}</p>}
                </div>

                <a className="forgot-link" onClick={() => navigate('/forgot-password')}>
                    Forgot password?
                </a>

                <button className="login-btn" onClick={handleSubmit}>
                    Log in
                </button>
            </div>
            
        </div>
    );
}