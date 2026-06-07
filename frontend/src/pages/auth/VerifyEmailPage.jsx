import '../auth/css/VerifyEmailPage.css';
import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { verifyEmail } from '../../api/authApi';
import LoadingButton from '../../components/LoadingButton';

export default function VerifyEmailPage() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const [error, setError] = useState(null);
    const [email, setEmail] = useState('');
    const [resendSuccess, setResendSuccess] = useState(false);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const token = searchParams.get('token');
        const verify = async () => {
            try {
                await verifyEmail(token);
                navigate('/login', { state: { message: 'Account verified! You may now log in.' } })
            } catch (err) {
                setError(err.response?.data?.message || 'Verification link is invalid.');
            }
        };

        if (token) {
            verify();
        } else {
            setError('No verification token provided.');
        }
    }, []);

    const handleResend = async () => {
        setLoading(true);
        try {
            await resendVerification({ email });
            setResendSuccess(true);
        } catch(err) {
            setError(err.response?.data?.message || 'Failed to resend verification email.');
        } finally {
            setLoading(false);
        }
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
                        {error === 'Account is already verified.' ? (
                            <button onClick={() => navigate('/login')} className="verify-btn">
                                Go to Login
                            </button>
                        ) : !resendSuccess ? (
                            <>
                                <div className="verify-form">
                                    <label className="input-label">Enter email to send a new verification email</label>
                                    <input
                                        type="email"
                                        placeholder="example@email.com"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        className="input-field"
                                    />
                                </div>
                                <LoadingButton 
                                    onClick={handleResend} 
                                    className="verify-btn" 
                                    disabled={loading}
                                >
                                    Resend Verification Email
                                </LoadingButton>
                            </>
                        ) : (
                            <p className="verify-success">Verification email resent. Please check your inbox.</p>
                        )}
                    </>
                )}
            </div>
        </div>
    );
}