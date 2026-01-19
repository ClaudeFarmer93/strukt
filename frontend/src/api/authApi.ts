import axios from "axios";
import type {User} from "../types/types.ts";

const api = axios.create({
    baseURL: "/api",
    withCredentials: true,
});

export const fetchCurrentUser = (): Promise<User> =>
    api.get<User>("/auth/me").then(res => res.data);