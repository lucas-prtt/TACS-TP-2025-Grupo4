import { Box, Grid } from "@mui/material";
import { CardEvento } from "./CardEvento";
import { datosEventos } from "./datosEventos";
import { NavbarApp } from "../../components/NavbarApp";
import { useTheme } from '@mui/material/styles';
import { useState } from "react";
import {Buscador} from "./Buscador"; // Asegúrate que el nombre del archivo sea correcto

export const Eventos = () => {
    const theme = useTheme();

    // Estados para el buscador
    const [searchValue, setSearchValue] = useState("");
    const [categoriaSeleccionada, setCategoriaSeleccionada] = useState("");
    const [estadoSeleccionado, setEstadoSeleccionado] = useState("");

    // Opciones de ejemplo (puedes reemplazar por las reales)
    const categorias = ["Tech", "Food", "Music", "Art", "Chess", "Run"];
    const estados = ["Programado", "Activo", "Finalizado"];

    // Filtro simple (puedes mejorarlo según tu lógica)
    const eventosFiltrados = datosEventos.filter(evento => {
        const coincideBusqueda = evento.titulo.toLowerCase().includes(searchValue.toLowerCase());
        const coincideCategoria = !categoriaSeleccionada || evento.categoria.toLowerCase() === categoriaSeleccionada.toLowerCase();
        const coincideEstado = !estadoSeleccionado || evento.estado.toLowerCase() === estadoSeleccionado.toLowerCase();
        return coincideBusqueda && coincideCategoria && coincideEstado;
    });

    return (
        <Box minHeight="100vh" sx={{ display: 'flex', flexDirection: 'row', bgcolor: theme.palette.background.primary }}>
            <Box
                sx={{
                    width: { xs: 0, md: '250px' },
                    flexShrink: 0,
                    display: { xs: 'none', md: 'block' }
                }}
            >
                <NavbarApp />
            </Box>
            <Box
                sx={{
                    display: { xs: 'block', md: 'none' },
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    zIndex: 1300
                }}
            >
                <NavbarApp />
            </Box>
            {/* Contenido principal */}
            <Box
                flex={1}
                p={3}
                sx={{
                    width: '100%',
                    pt: { xs: 4, md: 3 }, 
                    pl: { xs: 5, md: 0 }, 
                    display: 'flex',
                    justifyContent: 'center',
                }}
            >
                <Box sx={{ width: '100%', maxWidth: 1200 }}>
                    {/* Buscador arriba de las cards */}
                    <Buscador
                        categorias={categorias}
                        estados={estados}
                        categoriaSeleccionada={categoriaSeleccionada}
                        estadoSeleccionado={estadoSeleccionado}
                        onCategoriaChange={e => setCategoriaSeleccionada(e.target.value)}
                        onEstadoChange={e => setEstadoSeleccionado(e.target.value)}
                        searchValue={searchValue}
                        onSearchChange={e => setSearchValue(e.target.value)}
                        onFiltroAvanzado={() => { /* lógica de filtros avanzados */ }}
                    />
                    <Grid
                        container
                        spacing={3}
                        justifyContent="center"
                        alignItems="stretch"
                        sx={{ maxWidth: 1200 }}
                    >
                        {eventosFiltrados.map((evento) => (
                            <Grid
                                item
                                xs={12}
                                sm={6}
                                md={4}
                                lg={4}
                                key={evento.titulo}
                                display="flex"
                                justifyContent="center"
                                alignItems="stretch"
                            >
                                <CardEvento evento={evento} />
                            </Grid>
                        ))}
                    </Grid>
                </Box>
            </Box>
        </Box>
    );
};
