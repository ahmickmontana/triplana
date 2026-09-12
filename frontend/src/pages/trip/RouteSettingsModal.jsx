import { useState, useEffect } from 'react';
import './AddActivityModal.css';

export default function RouteSettingsModal({ tripId, onClose, onConfirm }) {
    const [strategy, setStrategy] = useState("fastest");
    const [allowedModes, setAllowedModes] = useState(['BUS', 'SUBWAY', 'TRAIN', 'LIGHT_RAIL']);

    useEffect(() => {
        const saved = localStorage.getItem(`triplana-route-settings-${tripId}`);
        if (saved) {
            const settings = JSON.parse(saved);
            setAllowedModes(settings.allowedModes || ['BUS', 'SUBWAY', 'TRAIN', 'LIGHT_RAIL']);
            
            if (settings.travelMode === 'WALK') setStrategy('walking');
            else if (settings.travelMode === 'DRIVE') setStrategy('driving');
            else if (settings.travelMode === 'BICYCLE') setStrategy('cycling');
            else if (settings.routingPreference === 'LESS_WALKING') setStrategy('lesswalking');
            else if (settings.routingPreference === 'FEWER_TRANSFERS') setStrategy('fewertransfers');
            else setStrategy('fastest');
        }
    }, [tripId]);

    const toggleMode = (mode) => {
        setAllowedModes(prev =>
            prev.includes(mode) ? prev.filter(m => m != mode) : [...prev, mode]
        );
    };

    const handleConfirm = () => {
        const settings = {
            strategy: strategy,
            travelMode: strategy === 'walking' ? 'WALK' : strategy === 'driving' ? 'DRIVE' : 'TRANSIT',
            routingPreference: strategy === 'lesswalking' ? 'LESS_WALKING' : strategy === 'fewertransfers' ? 'FEWER_TRANSFERS' : null,
            allowedModes: ['fastest', 'lesswalking', 'fewertransfers'].includes(strategy) ? allowedModes : null
        };
        localStorage.setItem(`triplana-route-settings-${tripId}`, JSON.stringify(settings));
        onConfirm(settings);
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal activity-modal" onClick={(e) => e.stopPropagation()}>
                <p className="modal-title">Route Settings</p>
                <form className="trip-form">
                    <div className="form-content">
                        <label className="input-label">Transport Strategy</label>
                        <select name="strategies" id="strategies" value={strategy} onChange={(e) => setStrategy(e.target.value)}>
                            <option value="fastest">Fastest (default)</option>
                            <option value="lesswalking">Least Walking</option>
                            <option value="fewertransfers">Fewer Transfers</option>
                            <option value="walking">Walking Only</option>
                            <option value="transitonly">Transit Only</option>
                            <option value="driving">Driving Only</option>
                        </select>
                    </div>

                    <div className="form-content">
                        <label className="input-label">Transport Constraints</label>
                        <div className="constraint-options">
                            <div className="activity-checkbox">
                                <input type="checkbox" id="drive" className="activity-route-checkbox" checked={allowedModes.includes('DRIVE')} onChange={() => toggleMode('DRIVE')}/>
                                <label htmlFor="drive" className="checkbox-label"/>
                                <p className="checkbox-label-text">Drive</p>
                            </div>
                            <div className="activity-checkbox">
                                <input type="checkbox" id="bus" className="activity-route-checkbox" checked={allowedModes.includes('BUS')} onChange={() => toggleMode('BUS')}/>
                                <label htmlFor="bus" className="checkbox-label"/>
                                <p className="checkbox-label-text">Bus</p>
                            </div>

                            <div className="activity-checkbox">
                                <input type="checkbox" id="subway" className="activity-route-checkbox" checked={allowedModes.includes('SUBWAY')} onChange={() => toggleMode('SUBWAY')}/>
                                <label htmlFor="subway" className="checkbox-label"/>
                                <p className="checkbox-label-text">Subway</p>
                            </div>

                            <div className="activity-checkbox">
                                <input type="checkbox" id="train" className="activity-route-checkbox" checked={allowedModes.includes('TRAIN')} onChange={() => toggleMode('TRAIN')}/>
                                <label htmlFor="train" className="checkbox-label"/>
                                <p className="checkbox-label-text">Train</p>
                            </div>

                            <div className="activity-checkbox">
                                <input type="checkbox" id="light-rail" className="activity-route-checkbox" checked={allowedModes.includes('LIGHT_RAIL')} onChange={() => toggleMode('LIGHT_RAIL')}/>
                                <label htmlFor="light-rail" className="checkbox-label"/>
                                <p className="checkbox-label-text">Light Rail</p>
                            </div>
                        </div>
                    </div>
                </form>
                <div className="modal-actions">
                    <button className="modal-btn-cancel" onClick={onClose}>
                        Cancel
                    </button>
                    <button type="button" className="modal-btn-confirm" onClick={handleConfirm}>
                        Save
                    </button>
                </div>
            </div>
        </div>
    );
}