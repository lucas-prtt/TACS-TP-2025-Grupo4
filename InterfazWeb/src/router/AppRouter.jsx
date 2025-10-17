import { Navigate, Route, Routes } from "react-router-dom";
import { UsuarioRouter } from "./UsuarioRouter";
import Login from "../usuario/login/login";
import { useAuth } from "../contexts/AuthContext";

export const AppRouter = () => {
  const { isAuthenticated } = useAuth();

  return (
    <Routes>
      <Route 
        path="/login" 
        element={isAuthenticated ? <Navigate to="/dashboard" /> : <Login />} 
      />
      <Route 
        path="/*" 
        element={isAuthenticated ? <UsuarioRouter /> : <Navigate to="/login" />} 
      />
    </Routes>
  );
};
