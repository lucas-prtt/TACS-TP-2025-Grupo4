import { Routes, Route, Navigate } from "react-router-dom";
import { Categorias } from "../admin/categorias/Categorias";
import { Estadisticas } from "../admin/estadisticas/Estadisticas";

export const AdminRouter = () => {
    return (
        <Routes>
            <Route path="/estadisticas" element={<Estadisticas />} />
            <Route path="/categorias" element={<Categorias />} />
            <Route path="/*" element={<Navigate to="/admin/estadisticas" />} />
        </Routes>
    );
};
