import '../auth/css/ForgotPassword.css';
import { forgotPassword } from '../../api/authApi.js'
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import LoadingButton from '../../components/LoadingButton';


export default function ForgotPasswordPage() {
    const navigate = useNavigate();
    const [errors, setErrors] = useState({});
    const [email, setEmail] = useState('');
    const [success, setSuccess] = useState(false);
    const [loading, setLoading] = useState(false);


    const handleSubmit = async () => {
        setLoading(true);
        setErrors({});

        try {
            await forgotPassword({ email });
            setSuccess(true);
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
            <button className="back-btn" onClick={() => navigate('/login')}>
                    ⇦ Back
            </button>
            <div className="reset-password-page">
                <div className="reset-password-bg" />
                <h1 className="page-title">
                    Triplana.
                </h1>
                <p className="page-subtitle">
                    Plan Your Trips Smarter.
                </p>

                {success && (
                    <p className="success-banner">
                        If an account exists for this email, a password reset email will be sent.
                    </p>
                )}

                <h2 className={`form-title ${success ? 'form-title-no-margin' : 'form-title-margin'}`}>
                    Reset Password
                </h2>

                <div className="reset-password-form">
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

                    <LoadingButton 
                        onClick={handleSubmit} 
                        className="reset-password-btn" 
                        disabled={loading}
                    >
                        Send Reset Email
                    </LoadingButton>
                </div>
                
            </div>
        </div>
    );
}