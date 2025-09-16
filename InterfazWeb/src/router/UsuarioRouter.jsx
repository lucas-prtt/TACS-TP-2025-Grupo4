import { Routes, Route } from "react-router-dom"
import { DashBoard } from "../usuario/dashBoard/DashBoard"
import { Eventos } from "../usuario/eventos/Eventos"
import { Analiticas } from "../usuario/analiticas/Analiticas"
import { Calendario } from "../usuario/calendario/Calendario"
import { CrearEventos } from "../usuario/crearEventos/CrearEventos"
import { Configuracion } from "../usuario/configuracion/Configuracion"
import { Navigate } from "react-router-dom"
import { VerEvento } from "../usuario/verEvento/VerEvento"
import { EditarEvento } from "../usuario/editarEvento/editarEvento";
import { useParams } from "react-router-dom"
import { datosEventos } from "../usuario/eventos/datosEventos"
import { MisIncripciones } from "../usuario/misIncripciones/MisIncripciones"
import { MisEventos } from "../usuario/misEventos/MisEventos"
export const UsuarioRouter = () => {

    return (
        <>
        <Routes>
            <Route path="/dashboard" element={<DashBoard />} />
            <Route path="/eventos" element={<Eventos />} />
            <Route path="/evento/:id" element={<VerEventoWrapper />} />
            <Route path="/analiticas" element={<Analiticas />} />
            <Route path="/calendario" element={<Calendario />} />
            <Route path="/crearEventos" element={<CrearEventos />} />
            <Route path="/configuracion" element={<Configuracion />} />
            <Route path="/editar-evento/:id" element={<EditarEventoWrapper />} />
            <Route path="/mis-inscripciones" element={<MisIncripciones />} />
            <Route path="/*" element={<Navigate to="/dashboard" />} />
            <Route path="/mis-eventos" element={<MisEventos />} />
         </Routes>
        </>
    )

}

// Wrapper para buscar el evento por id y pasarlo como prop a VerEvento
const VerEventoWrapper = () => {
    const { id } = useParams();
    // Asegúrate de que cada evento en datosEventos tenga un campo id único y bien definido
    const evento = datosEventos.find(ev => String(ev.id) === String(id));
    // Si no se encuentra el evento, podrías redirigir o mostrar un mensaje
    if (!evento) return <Navigate to="/eventos" />;
    return <VerEvento evento={evento} />;
};

// Wrapper para buscar el evento por id y pasarlo como prop a EditarEvento
const EditarEventoWrapper = () => {
    const { id } = useParams();
    const evento = datosEventos.find(ev => String(ev.id) === String(id));
    if (!evento) return <Navigate to="/eventos" />;
    return <EditarEvento evento={evento} />;
};



