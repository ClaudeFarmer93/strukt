import {createContext} from "react";
import type {User} from "../types/types";

export interface AuthContextType {
    user: User | undefined | null;
    login: () => void;
    logout: () => void;
    loading: boolean;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);