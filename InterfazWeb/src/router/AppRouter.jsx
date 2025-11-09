import { Navigate, Route, Routes } from "react-router-dom";
import { UsuarioRouter } from "./UsuarioRouter";
import { AdminRouter } from "./AdminRouter";
import Login from "../usuario/login/Login";
import { useAuth } from "../contexts/AuthContext";
import { NavbarAdmin } from "../components/NavbarAdmin";
import { Box } from "@mui/material";

export const AppRouter = () => {
  const { isAuthenticated, hasRole } = useAuth();
  const isAdmin = hasRole('ADMIN');

  return (
    <Routes>
      <Route 
        path="/login" 
        element={
          isAuthenticated 
            ? <Navigate to={isAdmin ? "/admin/categorias" : "/dashboard"} /> 
            : <Login />
        } 
      />
      <Route 
        path="/admin/*" 
        element={
          isAuthenticated && isAdmin ? (
            <Box sx={{ display: 'flex' }}>
              <NavbarAdmin />
              <Box component="main" sx={{ flexGrow: 1, p: 3, ml: { md: '250px' } }}>
                <AdminRouter />
              </Box>
            </Box>
          ) : (
            <Navigate to="/login" />
          )
        } 
      />
      <Route 
        path="/*" 
        element={
          isAuthenticated && !isAdmin 
            ? <UsuarioRouter /> 
            : isAuthenticated && isAdmin 
            ? <Navigate to="/admin/categorias" />
            : <Navigate to="/login" />
        } 
      />
    </Routes>
  );
};
