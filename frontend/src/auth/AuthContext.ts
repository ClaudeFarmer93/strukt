import {createContext} from "react";
import type {User} from "../types/types";

export interface AuthContextType {
    user: User | null;
    login: () => void;
    logout: () => void;
    loading: boolean;
    refreshUser: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);