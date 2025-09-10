import { Routes, Route } from "react-router-dom"
import { DashBoard } from "../usuario/dashboard/DashBoard"
export const UsuarioRouter = () => {

    return (
        <Routes>
            <Route path="/dashboard" element={<DashBoard />} />
        </Routes>
    )

}



       