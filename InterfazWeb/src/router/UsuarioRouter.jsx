import { Routes, Route } from "react-router-dom"
import { DashBoard } from "../usuario/dashBoard/DashBoard"
import { Eventos } from "../usuario/eventos/Eventos"
import { Analiticas } from "../usuario/analiticas/Analiticas"
import { Calendario } from "../usuario/calendario/Calendario"
import { CrearEventos } from "../usuario/crearEventos/CrearEventos"
import { Configuracion } from "../usuario/configuracion/Configuracion"
import { Navigate } from "react-router-dom"
import { VerEvento } from "../usuario/verEvento/VerEvento"
import { EditarEvento } from "../usuario/editarEvento/EditarEvento";
import { MisIncripciones } from "../usuario/misIncripciones/MisIncripciones"
import { MisEventos } from "../usuario/misEventos/MisEventos"

export const UsuarioRouter = () => {
    return (
        <>
        <Routes>
            <Route path="/dashboard" element={<DashBoard />} />
            <Route path="/eventos" element={<Eventos />} />
            <Route path="/evento/:id" element={<VerEvento />} />
            <Route path="/analiticas" element={<Analiticas />} />
            <Route path="/calendario" element={<Calendario />} />
            <Route path="/crearEventos" element={<CrearEventos />} />
            <Route path="/configuracion" element={<Configuracion />} />
            <Route path="/editar-evento/:id" element={<EditarEvento />} />
            <Route path="/mis-inscripciones" element={<MisIncripciones />} />
            <Route path="/mis-eventos" element={<MisEventos />} />
            <Route path="/*" element={<Navigate to="/dashboard" />} />
         </Routes>
        </>
    )
}







