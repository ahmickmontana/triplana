import './auth/css/LandingPage.css';
import Navbar from '../components/Navbar';

export default function LandingPage() {
    return (
        <div className="landing-page">
            <div className="landing-bg" />
            <Navbar />
            <div className="landing-content">
                <h1 className="landing-title">Triplana.</h1>
                <p className="landing-subtitle">Plan Your Trips Smarter.</p>
            </div>
        </div>
    );
}