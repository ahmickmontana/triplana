import { useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './CreateTripModal.css';
import defaultTripImg from '../../assets/images/default-trip-img.jpg';
import { FaCamera } from 'react-icons/fa';
import { createTrip, uploadCoverImage } from '../../api/tripApi.js'


export default function CreateTripModal({ onClose, onTripCreated }) {
    const navigate = useNavigate();
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');

    const fileInputRef = useRef(null);
    const [coverImg, setCoverImg] = useState(null);
    const [coverImgPreview, setCoverImgPreview] = useState(defaultTripImg)

    const [errors, setErrors] = useState({});
    const [imageError, setImageError] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleImageClick = () => {
        fileInputRef.current.click();
    };

    const handleImgChange = (e) => {
        const file = e.target.files[0];

        if (!file) return;

        const validExtensions = ['image/jpeg', 'image/png', 'image/webp'];
        if (!validExtensions.includes(file.type)) {
            setImageError("Image must be a JPG, PNG or WEBP file.");
            return;
        }

        if (file.size > 10 * 1024 * 1024) {
            setImageError("Image exceeds maximum file size (10MB).");
            return
        }

        setImageError(null);
        setCoverImg(file);
        setCoverImgPreview(URL.createObjectURL(file));
    }

    const handleTripCreation = async () => {
        setLoading(true);
        setErrors({});

        try {
            const response = await createTrip({
                name, description, startDate: startDate || null, endDate: endDate || null
            })


            const tripId = response.data.id;

            if (coverImg) {
                const formData = new FormData();
                formData.append('image', coverImg);
                await uploadCoverImage(tripId, formData);
            }
            
            onTripCreated();
            onClose();

        } catch (error) {
            if (error.response?.data) {
                setErrors(error.response.data);
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <p className="modal-title">Create New Trip</p>
                <form className="trip-form">
                    <div className="form-content">
                        <label className="input-label">Cover Image</label>
                        <div className="trip-card-image">
                            <img src={coverImgPreview} alt="cover" />
                            <div className="camera-overlay" onClick={handleImageClick}>
                                <FaCamera size={32} color="white" />
                            </div>
                            <input 
                                type="file"
                                accept="image/*"
                                ref={fileInputRef}
                                onChange={handleImgChange}
                                style={{ display: 'none' }}
                            />
                        </div>
                        {imageError && <p className="input-error">{imageError}</p>}
                    </div>

                    <div className="form-content">
                        <label className="input-label">Trip Name</label>
                        <input
                            type="text"
                            maxLength={100}
                            placeholder="Name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            className={`input-field ${errors.name ? 'input-error-border' : ''}`}
                        />
                        {errors.name && <p className="input-error">{errors.name}</p>}
                    </div>

                    <div className="form-content">
                        <label className="input-label">Trip Description</label>
                        <textarea
                            type="text"
                            maxLength={255}
                            placeholder="Description"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            className={'input-field description'}
                        />
                    </div>

                    <div className="form-content">
                        <label className="input-label">Trip Start Date</label>
                        <input
                            type="date"
                            value={startDate}
                            onChange={(e) => setStartDate(e.target.value)}
                            className={`input-field ${errors.startDate ? 'input-error-border' : ''}`}
                        />
                        {errors.startDate && <p className="input-error">{errors.startDate}</p>}
                    </div>

                    <div className="form-content">
                        <label className="input-label">Trip End Date</label>
                        <input
                            type="date"
                            value={endDate}
                            onChange={(e) => setEndDate(e.target.value)}
                            className={`input-field ${errors.endDate ? 'input-error-border' : ''}`}
                        />
                        {errors.endDate && <p className="input-error">{errors.endDate}</p>}
                    </div>
                </form>
                <div className="modal-actions">
                    <button className="modal-btn-cancel" onClick={onClose}>
                        Cancel
                    </button>
                    <button type="button" className="modal-btn-confirm" onClick={handleTripCreation} disabled={loading}>
                        {loading ? 'Creating...' : 'Create Trip'}
                    </button>
                </div>
            </div>
        </div>
    )
}