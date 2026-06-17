import '../auth/css/VerifyEmailPage.css';
import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { verifyEmail, resendVerification, confirmChangeEmail } from '../../api/authApi';
import LoadingButton from '../../components/LoadingButton';

export default function ConfirmEmailPage() {
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
                await confirmChangeEmail(token);
                navigate('/login', { state: { message: 'Email updated successfully! Please log in with your new email.' } })
            } catch (err) {
                console.log(err.response?.data?.message)
                setError(err.response?.data?.message || 'This email confirmation link is invalid or expired.');
            }
        };

        if (token) {
            verify();
        } else {
            setError('No verification token provided.');
        }
    }, []);

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