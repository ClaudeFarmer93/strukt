import {Box, AppBar, Button, Toolbar, Typography, Avatar, IconButton} from "@mui/material";
import CalendarMonthRoundedIcon from '@mui/icons-material/CalendarMonthRounded';
import DashboardRoundedIcon from '@mui/icons-material/DashboardRounded';
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded';
import {useAuth} from "../auth/useAuth";
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
                           strukt
                       </Typography>

                       {!loading && !user && (
                   <Button color={"inherit"} onClick={login}>Login</Button>
                       )}

                       {!loading && user && (
                           <>
                               <IconButton size={"large"} color={"inherit"} sx={{ width: 32, height: 32, mr: 2 }} onClick={() => navigate("/dashboard")}>
                                   <DashboardRoundedIcon/>
                               </IconButton>
                               <IconButton size={"large"} color={"inherit"} sx={{ width: 32, height: 32, mr: 2 }} onClick={() => navigate("/calendar")}>
                                   <CalendarMonthRoundedIcon/>
                               </IconButton>
                               <Avatar
                                   src={user.avatarUrl}
                                   alt={user.username}
                                   sx={{ width: 32, height: 32, mr: 2 }}
                                   />
                               {/* <Typography sx={{ms: 2}}>{user.username}</Typography>*/}
                           <IconButton color={"inherit"} onClick={logout}><LogoutRoundedIcon/></IconButton>
                           </>
                       )}
                   </Toolbar>
               </AppBar>

           </Box>

            </>
    )
}