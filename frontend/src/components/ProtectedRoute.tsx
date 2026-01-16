import {useAuth} from "../auth/auth.tsx";
import {Navigate} from "react-router-dom";
import type {JSX} from "react";

interface ProtectedRouteProps {
    children: JSX.Element;

}

export default function ProtectedRoute({children} : ProtectedRouteProps) {
    const {user, loading} = useAuth();

    if(loading) {
        return (
           <>
               <p>Loading...</p>
           </>
        )
    }

    if(!user){
        return <Navigate to={"/"} replace />
    }

    return children;
}