import {Typography} from "@mui/material";
import {useAuth} from "../auth/auth.tsx";

export default function MePage() {
const {user} = useAuth();
    return (
        <>
            <Typography variant={"h3"}>Hey {user?.username} </Typography>
        </>
    )
}