import {Route, Routes } from "react-router-dom"
import LandingPage from "./pages/LandingPage.tsx";
import DashboardPage from "./pages/DashboardPage.tsx";
import {AuthProvider} from "./auth/auth.tsx";
import ProtectedRoute from "./components/ProtectedRoute.tsx";
import Layout from "./components/Layout.tsx";
import CalendarPage from "./pages/MyCalendarPage.tsx";

function App() {


  return (
        <AuthProvider>
        <Routes>
            <Route element={<Layout />}>
            <Route path={"/"} element={<LandingPage/>}/>

                <Route path={"/dashboard"}
                       element={
                    <ProtectedRoute><DashboardPage/></ProtectedRoute>
                }/>
                <Route path={"/calendar"} element={
                    <ProtectedRoute><CalendarPage/></ProtectedRoute>
                }/>
            </Route>

        </Routes>

        </AuthProvider>

  )
}

export default App
