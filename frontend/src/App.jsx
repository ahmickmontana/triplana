import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';

import LandingPage from './pages/LandingPage';
import RegisterPage from './pages/auth/RegisterPage';
import LoginPage from './pages/auth/LoginPage';
import VerifyEmailPage from './pages/auth/VerifyEmailPage';
import CheckEmailPage from './pages/auth/CheckEmailPage';
import ForgotPasswordPage from './pages/auth/ForgotPasswordPage';
import ResetPasswordPage from './pages/auth/ResetPasswordPage';
import TripsPage from './pages/menu/TripsPage';
import EditProfilePage from './pages/auth/EditProfilePage';
import ChangeEmailPage from './pages/auth/ChangeEmailPage';
import ConfirmEmailPage from './pages/auth/ConfirmEmailPage';
import TripPlannerPage from './pages/trip/TripPlannerPage';

function ProtectedRoute({ children }) {
    const { currentUser } = useAuth();
    return currentUser ? children : <Navigate to="/login" />;
}

function App() {
    return (
        <BrowserRouter>
            <Routes>
                {/* Auth routes */}
                <Route path="/" element={<LandingPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/verify" element={<VerifyEmailPage />} />
                <Route path="/check-email" element={<CheckEmailPage />} />
                <Route path="/forgot-password" element={<ForgotPasswordPage />} />
                <Route path="/reset-password" element={<ResetPasswordPage />} />
                <Route path="/change-email/verify" element={<ChangeEmailPage />} />
                <Route path="/change-email/confirm" element={<ConfirmEmailPage />} />

                <Route path="/trips" element={<TripsPage />} />

                <Route path="/edit-profile" element={<EditProfilePage/>} />

                <Route path="/trips/:id/planner" element={<TripPlannerPage/>} />

                {/* Default redirect */}
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;