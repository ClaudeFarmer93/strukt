import {Route, Routes } from "react-router-dom"
import HomePage from "./pages/HomePage.tsx";
import MePage from "./pages/MePage.tsx";
import {AuthProvider} from "./auth/auth.tsx";
import ProtectedRoute from "./components/ProtectedRoute.tsx";
import Layout from "./components/Layout.tsx";

function App() {


  return (
        <AuthProvider>
        <Routes>
            <Route element={<Layout />}>
            <Route path={"/"} element={<HomePage/>}/>

                <Route path={"/me"} element={<ProtectedRoute><MePage/></ProtectedRoute>}/>
            </Route>

        </Routes>

        </AuthProvider>

  )
}

export default App
