import '../auth/css/VerifyEmailPage.css';
import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { verifyEmail, resendVerification, confirmChangeEmail, verifyConfirmEmailToken, resendChangeEmailConfirmation } from '../../api/authApi';
import LoadingButton from '../../components/LoadingButton';
import { useAuth } from '../../context/AuthContext';

export default function ConfirmEmailPage() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const [error, setError] = useState(null);
    const [email, setEmail] = useState('');
    const [resendSuccess, setResendSuccess] = useState(false);
    const [loading, setLoading] = useState(false);

    const token = searchParams.get('token');
    const [tokenValid, setTokenValid] = useState(null);
    const [tokenError, setTokenError] = useState(null);
    const [resendLoading, setResendLoading] = useState(false);
    const { logout } = useAuth();

    useEffect(() => {
        const verifyAndConfirm = async () => {
            try {
                await confirmChangeEmail(token);
                await logout();
                navigate('/login', { state: { message: 'Email updated successfully! Please log in with your new email.' } });
            } catch (error) {
                setTokenValid(false);
                setTokenError(error.response?.data?.message || 'This link is invalid or expired.');
            }
        };

        if (token) {
            verifyAndConfirm();
        } else {
            setTokenValid(false);
            setTokenError('This link is invalid or expired.');
        }
    }, []);

    const handleResend = async () => {
        setResendLoading(true);
        try {
            await resendChangeEmailConfirmation(token);
            setResendSuccess(true);
        } catch (error) {
            setTokenError(error.response?.data?.message || 'Failed to resend confirmation email.');
        } finally {
            setResendLoading(false);
        }
    };

    if (tokenValid === false) {
        return (
            <div>
                <div className="verify-page">
                    <div className="verify-bg" />
                    <div className="verify-content">
                        <h1 className="page-title">Triplana.</h1>
                        <p className="page-subtitle">Plan Your Trips Smarter.</p>
                        <p className="verify-error">{tokenError}</p>
                        {resendSuccess ? (
                            <p className="verify-success">Confirmation email resent. Please check your inbox.</p>
                        ) : (
                            <LoadingButton
                                onClick={handleResend}
                                className="verify-btn"
                                disabled={resendLoading}
                            >
                                Resend Confirmation Email
                            </LoadingButton>
                        )}
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="verify-page">
            <div className="verify-bg" />
            <div className="verify-content">
                <h1 className="page-title">
                    Triplana.
                </h1>
                <p className="page-subtitle">
                    Plan Your Trips Smarter.
                </p>

                {error && (
                    <>
                        <p className="verify-error">{error}</p>
                        <button onClick={() => navigate('/login')} className="verify-btn">
                                Return to Login
                            </button>
                    </>
                )}
            </div>
        </div>
    );
}