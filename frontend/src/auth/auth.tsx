import {type ReactNode, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import type {User} from "../types/types";
import {fetchCurrentUser} from "../api/authApi";
import {AuthContext} from "./AuthContext";

export function AuthProvider({children}: {children: ReactNode}) {

    const [user, setUser] = useState<User | undefined |null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const navigate = useNavigate();

    function login()  {
        const host : string = window.location.host === "localhost:5173" ?
            "http://localhost:8080" :
            window.location.origin;
        window.open(host + "/oauth2/authorization/github", "_self")
    }

    function logout() {
        setLoading(true);
        setUser(null);
        const host : string = window.location.host === "localhost:5173" ?
            "http://localhost:8080" :
            window.location.origin;
        window.open(host + "/logout", "_self");
    }


    function loadUser() {
        fetchCurrentUser()
            .then(setUser)
            .catch(() => setUser(null))
            .finally(() => setLoading(false));
    }

    useEffect(() => {
        if(user) {
            navigate("/dashboard")
        }
    }, [user, navigate]);

    useEffect(() => {
        loadUser();
    }, []);

    return (
        <AuthContext.Provider value={{user, login, logout, loading}}>
            {children}
        </AuthContext.Provider>
    )
}