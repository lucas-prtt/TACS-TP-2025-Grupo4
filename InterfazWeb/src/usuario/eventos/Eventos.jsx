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
    const { loading, error, events, getEvents } = useGetEvents();

    // Estados para el buscador
    const [searchValue, setSearchValue] = useState("");
    const [categoriaSeleccionada, setCategoriaSeleccionada] = useState("");
    const [estadoSeleccionado, setEstadoSeleccionado] = useState("");

    // Opciones de ejemplo (puedes reemplazar por las reales)
    const categorias = ["Tech", "Food", "Music", "Art", "Chess", "Run"];
    const estados = ["Programado", "Activo", "Finalizado"];

    // Cargar eventos al montar el componente
    useEffect(() => {
        let isMounted = true;
        
        const loadEvents = async () => {
            try {
                const result = await getEvents();
                if (isMounted) {
                    console.log('Eventos cargados:', result?.length || 0);
                    
                    // Log específico para prueba 1 y prueba 2
                    console.log('🔍 BUSCANDO PRUEBA 1 Y PRUEBA 2:');
                    result?.forEach((evento, index) => {
                        const titulo = evento.title?.toLowerCase() || '';
                        if (titulo.includes('prueba')) {
                            console.log(`📌 EVENTO ENCONTRADO [${index}]:`, {
                                titulo: evento.title,
                                imagen: evento.image,
                                tipoImagen: typeof evento.image,
                                longitudURL: evento.image?.length || 0,
                                eventoCompleto: evento
                            });
                        }
                    });
                    
                    // Análisis general de imágenes
                    const eventosConImagen = result?.filter(evento => evento.image) || [];
                    const eventosSinImagen = result?.filter(evento => !evento.image) || [];
                    
                    console.log('📊 Análisis de imágenes:');
                    console.log('  - Eventos con imagen:', eventosConImagen.length);
                    console.log('  - Eventos sin imagen:', eventosSinImagen.length);
                    
                    if (eventosConImagen.length > 0) {
                        console.log('🔗 TODAS las URLs de imágenes:');
                        eventosConImagen.forEach((evento, index) => {
                            console.log(`  ${index + 1}. "${evento.title}": "${evento.image}"`);
                        });
                    }
                }
            } catch (err) {
                if (isMounted) {
                    console.error('Error al cargar eventos:', err);
                }
            }
        };
        
        loadEvents();
        
        return () => {
            isMounted = false;
        };
    }, []); // Solo se ejecuta una vez al montar

    // Función para recargar eventos manualmente
    const handleReload = async () => {
        try {
            await getEvents();
        } catch (err) {
            console.error('Error al recargar eventos:', err);
        }
    };

    // Mapear eventos de API al formato esperado por CardEvento (memoizado)
    const eventosFormateados = useMemo(() => (events || []).map(evento => {
        if (!evento || !evento.id) return null;
        
        // Manejar categoria de forma segura basado en EventDTO
        let categoria = "";
        if (evento.category) {
            // category es un objeto Category con propiedades
            if (typeof evento.category === 'object' && evento.category.name) {
                categoria = evento.category.name.toLowerCase();
            } else if (typeof evento.category === 'string') {
                categoria = evento.category.toLowerCase();
            }
        }

        // Manejar tags (List<Tag> en EventDTO)
        const tags = evento.tags ? evento.tags.map(tag => {
            if (typeof tag === 'string') {
                return tag;
            } else if (tag && typeof tag === 'object' && tag.name) {
                return tag.name;
            }
            return '';
        }).filter(tag => tag) : [];

        // Manejar estado (EventState enum)
        let estado = "activo";
        if (evento.state) {
            if (typeof evento.state === 'string') {
                estado = evento.state.toLowerCase();
            } else if (typeof evento.state === 'object' && evento.state.name) {
                estado = evento.state.name.toLowerCase();
            }
        }

        // Log específico para eventos "prueba"
        const esPrueba = evento.title?.toLowerCase().includes('prueba');
        if (esPrueba) {
            console.log('🎯 PROCESANDO EVENTO PRUEBA:', {
                titulo: evento.title,
                imagenOriginal: evento.image,
                tipoImagenOriginal: typeof evento.image
            });
        }

        // Debug: análisis detallado de imagen
        if (evento.image) {
            if (esPrueba) {
                console.log('🔥 PRUEBA CON IMAGEN - ANÁLISIS DETALLADO:');
                console.log('  - URL original:', evento.image);
                console.log('  - Tipo:', typeof evento.image);
                console.log('  - Longitud:', evento.image.length);
                console.log('  - Empieza con http:', evento.image.startsWith('http'));
                console.log('  - Contiene espacios:', evento.image.includes(' '));
                console.log('  - URL completa entre comillas:', `"${evento.image}"`);
            }
            
            console.log('📸 Evento con imagen:', evento.title, '-> URL:', evento.image);
            console.log('  - Tipo de imagen:', typeof evento.image);
            console.log('  - Longitud URL:', evento.image.length);
            console.log('  - Empieza con http:', evento.image.startsWith('http'));
            console.log('  - Contiene espacios:', evento.image.includes(' '));
            
            // Test básico de URL
            try {
                const url = new URL(evento.image);
                console.log('  - Protocolo:', url.protocol);
                console.log('  - Host:', url.hostname);
                console.log('  - Pathname:', url.pathname);
                if (esPrueba) {
                    console.log('✅ PRUEBA - URL VÁLIDA:', url.href);
                }
            } catch (e) {
                console.log('  - ❌ URL inválida:', e.message);
                if (esPrueba) {
                    console.log('❌ PRUEBA - URL INVÁLIDA:', e.message);
                }
            }
        } else {
            if (esPrueba) {
                console.log('⚠️ PRUEBA SIN IMAGEN:', evento.title);
            } else {
                console.log('🚫 Evento sin imagen:', evento.title);
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
            const coincideCategoria = !categoriaSeleccionada || evento.categoria === categoriaSeleccionada.toLowerCase();
            const coincideEstado = !estadoSeleccionado || evento.estado === estadoSeleccionado.toLowerCase();
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
