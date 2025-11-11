import { Box, Grid, Typography, CircularProgress, Alert, Paper, Button } from "@mui/material";
import { CardEvento } from "./CardEvento";
import { NavbarApp } from "../../components/NavbarApp";
import { useTheme } from '@mui/material/styles';
import { useState, useEffect, useMemo } from "react";
import {Buscador} from "./Buscador"; // Asegúrate que el nombre del archivo sea correcto
import { useNavigate } from "react-router-dom";
import { useGetEvents } from "../../hooks/useGetEvents";
import noImagePlaceholder from "../../assets/images/no_image.png";

export const Eventos = () => {
    const theme = useTheme();
    const navigate = useNavigate();
    const { loading, error, events, getEvents, getCategories } = useGetEvents();

    // Estados para el buscador
    const [searchValue, setSearchValue] = useState("");
    const [categoriaSeleccionada, setCategoriaSeleccionada] = useState("");
    const [estadoSeleccionado, setEstadoSeleccionado] = useState("");
    const [categorias, setCategorias] = useState([]);

    // Mapeo de estados en español a valores técnicos del backend
    const estadosMap = {
        'Abierto': 'EVENT_OPEN',
        'Cerrado': 'EVENT_CLOSED',
        'Cancelado': 'EVENT_CANCELLED'
    };

    // Estados predefinidos para el filtro
    const estados = ["Abierto", "Cerrado", "Cancelado"];

    // Cargar eventos y categorías al montar el componente
    useEffect(() => {
        let isMounted = true;
        
        const loadEvents = async () => {
            try {
                const result = await getEvents();
            } catch (err) {
                if (isMounted) {
                }
            }
        };
        
        const loadCategories = async () => {
            try {
                const result = await getCategories();
                if (isMounted && result) {
                    // Ordenar categorías alfabéticamente por título
                    const categoriasOrdenadas = result
                        .map(cat => cat.title || cat) // Extraer título si es objeto
                        .sort((a, b) => a.localeCompare(b)); // Ordenar alfabéticamente
                    setCategorias(categoriasOrdenadas);
                }
            } catch (err) {
                if (isMounted) {
                }
            }
        };
        
        loadEvents();
        loadCategories();
        
        return () => {
            isMounted = false;
        };
    }, [getEvents, getCategories]); // Solo se ejecuta una vez al montar

    // Función para recargar eventos manualmente
    const handleReload = async () => {
        try {
            await getEvents();
        } catch (err) {
        }
    };

    // Mapear eventos de API al formato esperado por CardEvento (memoizado)
    const eventosFormateados = useMemo(() => (events || []).map(evento => {
        if (!evento || !evento.id) return null;
        
        // Manejar categoria de forma segura
        let categoria = "";
        if (evento.category) {
            // category es un objeto Category con propiedad 'title'
            if (typeof evento.category === 'object' && evento.category.title) {
                categoria = evento.category.title;
            } else if (typeof evento.category === 'string') {
                categoria = evento.category;
            }
        }

        // Manejar tags (List<Tag> en EventDTO)
        const tags = evento.tags ? evento.tags.map(tag => {
            if (typeof tag === 'string') {
                return tag;
            } else if (tag && typeof tag === 'object') {
                // El backend usa 'nombre' en la clase Tag
                return tag.nombre || tag.name || '';
            }
            return '';
        }).filter(tag => tag) : [];

        // Manejar estado (EventState enum)
        let estado = "";
        if (evento.state) {
            if (typeof evento.state === 'string') {
                estado = evento.state;
            } else if (typeof evento.state === 'object' && evento.state.name) {
                estado = evento.state.name;
            }
        }

        return {
            id: evento.id,
            titulo: evento.title || "Sin título",
            descripcion: evento.description || "Sin descripción",
            organizador_id: evento.usernameOrganizer || "Sin organizador",
            fechaInicio: evento.startDateTime || new Date().toISOString(),
            duracion: evento.durationMinutes || 0,
            lugar: evento.location || "Sin ubicación",
            max_participantes: evento.maxParticipants || 0,
            min_participantes: evento.minParticipants || 0,
            participantes_registrados: evento.registered || 0,
            precio: evento.price || 0,
            tags: tags,
            estado: estado,
            categoria: categoria,
            imagen: evento.image // Pasamos la URL tal como viene, CardEvento manejará el fallback
        };
    }).filter(evento => evento !== null), [events]);

    // Filtro usando los eventos formateados (memoizado)
    const eventosFiltrados = useMemo(() => {
        return eventosFormateados.filter(evento => {
            const coincideBusqueda = evento.titulo?.toLowerCase().includes(searchValue.toLowerCase()) || false;
            // Comparar categorías sin distinguir mayúsculas/minúsculas
            const coincideCategoria = !categoriaSeleccionada || 
                evento.categoria?.toLowerCase() === categoriaSeleccionada.toLowerCase();
            
            // Comparar estados: mapear el estado seleccionado (español) al valor técnico del backend
            const coincideEstado = !estadoSeleccionado || 
                evento.estado?.toUpperCase() === estadosMap[estadoSeleccionado];
            
            return coincideBusqueda && coincideCategoria && coincideEstado;
        });
    }, [eventosFormateados, searchValue, categoriaSeleccionada, estadoSeleccionado]);
    
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
                <Box sx={{ width: '100%', maxWidth: 1000 }}>
                    {/* Título y subtítulo */}
                    <Typography variant="h4" fontWeight={700} sx={{ mb: 0.5 }}>
                        Lista de Eventos
                    </Typography>
                    <Typography variant="subtitle1" color="text.secondary" sx={{ mb: 3 }}>
                        Gestiona todos tus eventos desde un solo lugar
                    </Typography>
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
                    {/* Indicador de loading */}
                    {loading && (
                        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
                            <CircularProgress />
                        </Box>
                    )}

                    {/* Mensaje de error */}
                    {error && (
                        <Alert 
                            severity="error" 
                            sx={{ mb: 3 }}
                            action={
                                <Button color="inherit" size="small" onClick={handleReload}>
                                    Reintentar
                                </Button>
                            }
                        >
                            {error}
                            {error.includes('401') || error.includes('Unauthorized') ? (
                                <Typography variant="body2" sx={{ mt: 1 }}>
                                    Por favor, inicia sesión nuevamente.
                                </Typography>
                            ) : null}
                        </Alert>
                    )}

                    {/* Contenido cuando no está cargando */}
                    {!loading && !error && (
                        <>
                            {/* Mostrar cantidad de eventos */}
                            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                                Mostrando {eventosFiltrados.length} de {(events || []).length} eventos
                            </Typography>

                            {/* Mensaje cuando no hay eventos */}
                            {eventosFiltrados.length === 0 ? (
                                <Paper sx={{ p: 4, textAlign: 'center', mb: 3 }}>
                                    <Typography variant="h6" color="text.secondary" gutterBottom>
                                        No se encontraron eventos
                                    </Typography>
                                    <Typography variant="body2" color="text.secondary">
                                        {(events || []).length === 0 
                                            ? "No hay eventos disponibles en este momento." 
                                            : "Intenta ajustar los filtros de búsqueda."
                                        }
                                    </Typography>
                                </Paper>
                            ) : (
                                /* Grid de eventos */
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
                                            key={evento.id}
                                            display="flex"
                                            justifyContent="center"
                                            alignItems="stretch"
                                        >
                                            <CardEvento
                                                evento={evento}
                                                onVerEvento={() => navigate(`/evento/${evento.id}`)}
                                            />
                                        </Grid>
                                    ))}
                                </Grid>
                            )}
                        </>
                    )}
                </Box>
            </Box>
        </Box>
    );
};
