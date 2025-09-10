import { Route, Routes } from "react-router-dom"
import { UsuarioRouter } from "./UsuarioRouter"
export const AppRouter = () => {
    return (
        <Routes>
            <Route path="/*" element={<UsuarioRouter />} />
        </Routes>
    )
}
