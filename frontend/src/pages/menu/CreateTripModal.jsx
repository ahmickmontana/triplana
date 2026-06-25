import { useRef, useState } from 'react';
import './CreateTripModal.css';
import defaultTripImg from '../../assets/images/default-trip-img.jpg';
import { FaCamera } from 'react-icons/fa';


export default function CreateTripModal({ onClose }) {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');

    const fileInputRef = useRef(null);
    const [coverImg, setCoverImg] = useState(null);
    const [coverImgPreview, setCoverImgPreview] = useState(defaultTripImg)

    const handleImageClick = () => {
        fileInputRef.current.click();
    };

    const handleImgChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setCoverImg(file);
            setCoverImgPreview(URL.createObjectURL(file));
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
                    </div>

                    <div className="form-content">
                        <label className="input-label">Trip Title</label>
                        <input
                            type="text"
                            maxLength={100}
                            placeholder="Title"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            className={'input-field'}
                        />
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
                            className={'input-field'}
                        />
                    </div>

                    <div className="form-content">
                        <label className="input-label">Trip eND Date</label>
                        <input
                            type="date"
                            value={endDate}
                            onChange={(e) => setEndDate(e.target.value)}
                            className={'input-field'}
                        />
                    </div>
                </form>
                <div className="modal-actions">
                    <button className="modal-btn-cancel" onClick={onClose}>
                        Cancel
                    </button>
                    <button className="modal-btn-confirm">
                        Confirm
                    </button>
                </div>
            </div>
        </div>
    )
}