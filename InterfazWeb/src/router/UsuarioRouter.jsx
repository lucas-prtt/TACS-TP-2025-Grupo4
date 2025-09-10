import { Routes, Route } from "react-router-dom"
import { DashBoard } from "../usuario/dashboard/DashBoard"
import { Eventos } from "../usuario/eventos/Eventos"
import { Analiticas } from "../usuario/analiticas/Analiticas"
import { Calendario } from "../usuario/calendario/Calendario"
import { NavbarApp } from "../components/NavbarApp"
import {Asistentes} from "../usuario/asistentes/Asistentes"
import { CrearEventos } from "../usuario/crearEventos/CrearEventos"
import { Configuracion } from "../usuario/configuracion/Configuracion"
import { Navigate } from "react-router-dom"

export const UsuarioRouter = () => {

    return (
        <>
        <NavbarApp/>
        <Routes>
            <Route path="/dashboard" element={<DashBoard />} />
            <Route path="/eventos" element={<Eventos />} />
            <Route path="/analiticas" element={<Analiticas />} />
            <Route path="/calendario" element={<Calendario />} />
            <Route path="/asistentes" element={<Asistentes />} />
            <Route path="/crearEventos" element={<CrearEventos />} />
            <Route path="/configuracion" element={<Configuracion />} />
            <Route path="/*" element={<Navigate to="/dashboard" />} />
         </Routes>
        </>
    )

}



       