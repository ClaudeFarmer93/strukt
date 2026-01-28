import {type ReactNode, useEffect, useState} from "react";

import type {User} from "../types/types";
import {fetchCurrentUser} from "../api/authApi";
import {AuthContext} from "./AuthContext";

export function AuthProvider({children}: { children: ReactNode }) {

    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState<boolean>(true);


    function login() {
        const host: string = window.location.host === "localhost:5173" ?
            "http://localhost:8080" :
            window.location.origin;
        window.open(host + "/oauth2/authorization/github", "_self")
    }

    function logout() {
        setLoading(true);
        setUser(null);
        const host: string = window.location.host === "localhost:5173" ?
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

    function refreshUser() {
        fetchCurrentUser().then((u) => setUser(u))
            .catch(() => setUser(null))


    }


    useEffect(() => {
        loadUser();
    }, []);

    return (
        <AuthContext.Provider value={{user, refreshUser, login, logout, loading}}>
            {children}
        </AuthContext.Provider>
    )
}