import './LoadingButton.css';

export default function LoadingButton({ onClick, className, children, disabled }) {
    return (
        <button
            onClick={onClick}
            className={className}
            disabled={disabled}
        >
            {disabled ? (
                <span className="loading-spinner" />
            ) : (
                children
            )}
        </button>
    );
}