import './auth/css/LandingPage.css';
import Navbar from '../components/Navbar';
import tokyoImg from '../assets/images/tokyo-aerial.jpg';
import featureOne from '../assets/images/f1.jpg';
import featureTwo from '../assets/images/f2.jpg';
import featureThree from '../assets/images/f3.jpg';
import { useNavigate } from 'react-router-dom'


export default function LandingPage() {
    const navigate = useNavigate();
    return (
        <div className="landing-page">
            <div className="landing-bg" />
            <Navbar />
            <div className="landing-content">
                <section className="description">
                    <div className="description-left">
                        <h1 className="description-title">
                            Plan Your <br/> Trips Smarter.
                            </h1>
                        <p className="description-paragraph">
                            Organise your itinerary, activities, and routes in one place.
                            Spend less time planning and more time enjoying your adventure.
                        </p>
                        <button className="description-btn" onClick={() => navigate('/register')}>
                            Get Started ⇨
                        </button>
                    </div>
                    <div className="description-img">
                        <img src={tokyoImg} alt="Tokyo aerial view" />
                    </div>
                </section>


                <section class="key-features">
                    <h2 class="features-title">
                        Key Features
                    </h2>
                    <div class="features">
                        <div class="feature">
                            <div class="featureone-img">
                                <img src={featureOne} alt="Tokyo aerial view" />
                            </div>
                            <div class="feature-text">
                                <h3 class="feature-title">
                                    Structured Itinerary Planning
                                </h3>
                                <p class="feature-description">
                                    Plan your trip day-by-day with a clear and organised structure.
                                    Easily manage activities within each day so your entire itinerary
                                    stays easy to understand at a glance and on the go!
                                </p>
                            </div>
                        </div>

                        <div class="feature">
                            <div class="featuretwo-img">
                                <img src={featureTwo} alt="Tokyo aerial view" />
                            </div>
                            <div class="feature-text">
                                <h3 class="feature-title">
                                    Flexible Activity Scheduling
                                </h3>
                                <p class="feature-description">
                                    Add activities with optional start and end times, allowing you to
                                    plan freely while avoiding scheduling conflicts. Adjust and update
                                    your plan effortlessly as your trip evolves.
                                </p>
                            </div>
                        </div>

                        <div class="feature">
                            <div class="featurethree-img">
                                <img src={featureThree} alt="Tokyo aerial view" />
                            </div>
                            <div class="feature-text">
                                <h3 class="feature-title">
                                    Smart Routing and Transporting
                                </h3>
                                <p class="feature-description">
                                    Visualise routes between your activities and explore transport options
                                    for each segment. Quickly understand how to get from one place to another
                                    with clear, actionable route details.
                                </p>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="get-started">
                    <h1 class="get-started-title">
                        Start Planning Your Trip
                    </h1>
                    <p class="get-started-description">
                        Organise your itinerary, activities, and routes in one place.
                    </p>
                    <button className="get-started-btn" onClick={() => navigate('/register')}>
                            Get Started ⇨
                    </button>
                </section>

                <footer class="footer">
                    <div class="footer-main">
                        <h3 class="footer-title">
                        Triplana.
                        </h3>
                        <h3 class="footer-subtitle">
                        A trip itinerary planner.
                        </h3>
                    </div>
                    <p class="footer-content">
                        Powered by Google Maps.
                    </p>
                    <p class="footer-content footer-copyright">
                        © Ahmick Montana. 2026.
                    </p>
                </footer>
            </div>
        </div>
    );
}