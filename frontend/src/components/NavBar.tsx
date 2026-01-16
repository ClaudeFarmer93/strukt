import {Box, AppBar, Button, Toolbar, Typography, Avatar} from "@mui/material";
import {useAuth} from "../auth/auth.tsx";
import {useNavigate} from "react-router-dom";

export default function NavBar() {
const {user, login,logout,loading} = useAuth();
const navigate = useNavigate();



    return (
        <>
           <Box sx={{flexGrow: 1}}>
               <AppBar position={"fixed"}>
                   <Toolbar>
                       <Typography variant={"h5"} component="div" sx={{ flexGrow: 1,
                           cursor: "pointer",
                       "&:hover": {
                           opacity: 0.8}
                       }}
                                   onClick={() => navigate("/")}
                           >
                           Strukt
                       </Typography>

                       {!loading && !user && (
                   <Button color={"inherit"} onClick={login}>Login</Button>
                       )}

                       {!loading && user && (
                           <>
                               <Avatar
                                   src={user.avatarUrl}
                                   alt={user.username}
                                   sx={{ width: 32, height: 32, mr: 2 }}
                                   />
                               <Typography sx={{ms: 2}}>{user.username}</Typography>
                           <Button color={"inherit"} onClick={logout}>Logout</Button>
                           </>
                       )}
                   </Toolbar>
               </AppBar>

           </Box>

            </>
    )
}