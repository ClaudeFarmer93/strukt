import {Outlet} from "react-router-dom";
import NavBar from "./NavBar.tsx";

export default function Layout() {


    return(
        <>
            <NavBar/>
            <main style={{paddingTop: "80px"}}>
                <Outlet/>
            </main>
        </>
    )

}