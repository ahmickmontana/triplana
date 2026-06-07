import '../auth/css/CheckEmailPage.css';
import { useNavigate, useLocation } from 'react-router-dom';
import { resendVerification } from '../../api/authApi';
import { useState } from 'react';

export default function CheckEmailPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const email = location.state?.email;
    const [resendSuccess, setResendSuccess] = useState(false);
    const [resendLoading, setResendLoading] = useState(false);

    const handleResend = async () => {
        setResendLoading(true);
        try {
            await resendVerification({ email });
            setResendSuccess(true);
        } catch (error) {
            console.log(error.response?.data);
        } finally {
            setResendLoading(false);
        }
    };

    return (
        <div className="check-email-page">
            <div className="check-email-bg" />
            <div className="check-email-content">
                <h1 className="page-title">Triplana.</h1>
                <p className="page-subtitle">Plan Your Trips Smarter.</p>
                <div className="check-email-text">
                    <h2 className="form-title">Check Your Email!</h2>
                    <p className="check-email-message">
                        A verification email has been sent to <strong>{email}</strong>.
                        Please check your inbox to verify your account.
                    </p>
                </div>

                {resendSuccess ? (
                    <p className="verify-success">Verification email resent. Please check your inbox.</p>
                ) : (
                    <a 
                        className={`resend-link ${resendLoading ? 'resend-disabled' : ''}`} 
                        onClick={!resendLoading ? handleResend : null}
                    >
                        {resendLoading ? 'Sending...' : 'Not receiving the email? Resend'}
                    </a>
                )}

                <a className="return-btn" onClick={() => navigate('/login')}>
                    Back to Login
                </a>
            </div>
        </div>
    );
}