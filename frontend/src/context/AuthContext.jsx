import { getMe, logout as logoutApi } from '../api/authApi';
import { useState, useEffect, createContext, useContext } from 'react';


const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getMe()
            .then(res => setCurrentUser(res.data))
            .catch(() => setCurrentUser(null))
            .finally(() => setLoading(false));
    }, []);

    const login = (user) => setCurrentUser(user);

    const logout = async () => {
        await logoutApi();
        setCurrentUser(null);
    };

    return (
        <AuthContext.Provider value={{ currentUser, login, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}