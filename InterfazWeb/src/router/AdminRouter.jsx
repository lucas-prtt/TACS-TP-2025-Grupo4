import { Routes, Route, Navigate } from "react-router-dom";
import { Categorias } from "../admin/categorias/Categorias";

export const AdminRouter = () => {
    return (
        <Routes>
            <Route path="/categorias" element={<Categorias />} />
            <Route path="/*" element={<Navigate to="/admin/categorias" />} />
        </Routes>
    );
};
