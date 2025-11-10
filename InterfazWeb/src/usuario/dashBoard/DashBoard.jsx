import { Box, Typography, Grid, Card, CardContent, Stack, Chip, Divider, Button, CardMedia, CircularProgress } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import GroupsIcon from '@mui/icons-material/Groups';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { NavbarApp } from "../../components/NavbarApp";
import { useNavigate } from "react-router-dom";
import { format } from 'date-fns';
import { es } from 'date-fns/locale';
import { useGetEvents } from "../../hooks/useGetEvents";
import { useGetAuth } from "../../hooks/useGetAuth";
import { useState, useEffect, useMemo } from "react";
import noImagePlaceholder from "../../assets/images/no_image.png";

export const DashBoard = () => {
        const theme = useTheme();
        const navigate = useNavigate();
        const { loading, events, getEvents, getOrganizedEvents, getUserRegistrations } = useGetEvents();
        const { user } = useGetAuth();
        const [eventosProximos, setEventosProximos] = useState([]);
        const [misEventos, setMisEventos] = useState([]);
        const [misInscripciones, setMisInscripciones] = useState([]);

        // Cargar todos los datos del dashboard
        useEffect(() => {
            let isMounted = true;
            
            const loadDashboardData = async () => {
                try {
                    // Cargar todos los eventos
                    const allEvents = await getEvents();
                    
                    if (isMounted && allEvents) {
                        // Filtrar y ordenar próximos 3 eventos
                        const proximos = allEvents
                            .filter(e => new Date(e.startDateTime) > new Date())
                            .sort((a, b) => new Date(a.startDateTime) - new Date(b.startDateTime))
                            .slice(0, 3)
                            .map(evento => ({
                                id: evento.id,
                                titulo: evento.title || "Sin título",
                                fechaInicio: evento.startDateTime,
                                imagen: evento.image || noImagePlaceholder,
                                categoria: evento.category?.title || evento.category || "Sin categoría"
                            }));
                        setEventosProximos(proximos);
                    }

                    // Si el usuario está autenticado, cargar sus eventos y inscripciones
                    if (user) {
                        // Cargar eventos organizados por el usuario
                        try {
                            const organized = await getOrganizedEvents();
                            if (isMounted && organized) {
                                setMisEventos(organized);
                            }
                        } catch (err) {
                        }

                        // Cargar inscripciones del usuario
                        try {
                            const registrations = await getUserRegistrations();
                            if (isMounted && registrations) {
                                // Filtrar solo inscripciones activas
                                const activas = registrations.filter(reg => 
                                    reg.state !== 'CANCELED' && reg.state?.name !== 'CANCELED'
                                );
                                setMisInscripciones(activas);
                            }
                        } catch (err) {
                        }
                    }
                } catch (err) {
                }
            };
            
            loadDashboardData();
            
            return () => {
                isMounted = false;
            };
        }, [getEvents, getOrganizedEvents, getUserRegistrations, user]);

        // Calcular resumen dinámico basado en eventos reales
        const resumen = useMemo(() => {
            const ahora = new Date();
            const eventosActivos = events?.filter(e => {
                const fechaEvento = new Date(e.startDateTime);
                return fechaEvento > ahora;
            }) || [];

            return {
                totalEventos: events?.length || 0,
                proximosEventos: eventosActivos.length,
                misEventosCreados: misEventos?.length || 0,
                misInscripcionesActivas: misInscripciones?.length || 0,
            };
        }, [events, misEventos, misInscripciones]);

        // Función para formatear fecha
        const formatFecha = (fecha) => {
            try {
                return format(new Date(fecha), "d 'de' MMM yyyy · HH:mm", { locale: es });
            } catch {
                return fecha;
            }
        };

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
                                Dashboard de Eventos
                            </Typography>
                            <Typography variant="subtitle1" color="text.secondary" sx={{ mb: 3 }}>
                                Resumen general de tus eventos y estadísticas
                            </Typography>
                            {/* Resumen de métricas */}
                            <Box sx={{ display: 'flex', gap: 3, mb: 4, width: '100%', flexWrap: 'wrap' }}>
                                <Card 
                                    sx={{ 
                                        flex: 1, 
                                        minWidth: 200,
                                        borderRadius: 3, 
                                        boxShadow: '0 2px 8px rgba(0,0,0,0.07)', 
                                        border: '1px solid #e0e0e0', 
                                        bgcolor: theme.palette.background.paper,
                                        cursor: 'pointer',
                                        transition: 'transform 0.2s',
                                        '&:hover': {
                                            transform: 'translateY(-4px)',
                                            boxShadow: '0 4px 12px rgba(0,0,0,0.12)'
                                        }
                                    }}
                                    onClick={() => navigate('/eventos')}
                                >
                                    <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                        <CalendarMonthIcon sx={{ color: '#6366f1', fontSize: 36, mb: 1 }} />
                                        <Typography variant="h5" fontWeight={700}>{resumen.totalEventos}</Typography>
                                        <Typography variant="body2" color="text.secondary" textAlign="center">
                                            Eventos Disponibles
                                        </Typography>
                                    </CardContent>
                                </Card>
                                <Card 
                                    sx={{ 
                                        flex: 1,
                                        minWidth: 200, 
                                        borderRadius: 3, 
                                        boxShadow: '0 2px 8px rgba(0,0,0,0.07)', 
                                        border: '1px solid #e0e0e0', 
                                        bgcolor: theme.palette.background.paper,
                                        cursor: 'pointer',
                                        transition: 'transform 0.2s',
                                        '&:hover': {
                                            transform: 'translateY(-4px)',
                                            boxShadow: '0 4px 12px rgba(0,0,0,0.12)'
                                        }
                                    }}
                                    onClick={() => navigate('/eventos')}
                                >
                                    <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                        <AccessTimeIcon sx={{ color: '#34d399', fontSize: 36, mb: 1 }} />
                                        <Typography variant="h5" fontWeight={700}>{resumen.proximosEventos}</Typography>
                                        <Typography variant="body2" color="text.secondary" textAlign="center">
                                            Eventos Próximos
                                        </Typography>
                                    </CardContent>
                                </Card>
                                {user && (
                                    <>
                                        <Card 
                                            sx={{ 
                                                flex: 1,
                                                minWidth: 200, 
                                                borderRadius: 3, 
                                                boxShadow: '0 2px 8px rgba(0,0,0,0.07)', 
                                                border: '1px solid #e0e0e0', 
                                                bgcolor: theme.palette.background.paper,
                                                cursor: 'pointer',
                                                transition: 'transform 0.2s',
                                                '&:hover': {
                                                    transform: 'translateY(-4px)',
                                                    boxShadow: '0 4px 12px rgba(0,0,0,0.12)'
                                                }
                                            }}
                                            onClick={() => navigate('/mis-eventos')}
                                        >
                                            <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                                <EventAvailableIcon sx={{ color: '#a78bfa', fontSize: 36, mb: 1 }} />
                                                <Typography variant="h5" fontWeight={700}>{resumen.misEventosCreados}</Typography>
                                                <Typography variant="body2" color="text.secondary" textAlign="center">
                                                    Mis Eventos Creados
                                                </Typography>
                                            </CardContent>
                                        </Card>
                                        <Card 
                                            sx={{ 
                                                flex: 1,
                                                minWidth: 200, 
                                                borderRadius: 3, 
                                                boxShadow: '0 2px 8px rgba(0,0,0,0.07)', 
                                                border: '1px solid #e0e0e0', 
                                                bgcolor: theme.palette.background.paper,
                                                cursor: 'pointer',
                                                transition: 'transform 0.2s',
                                                '&:hover': {
                                                    transform: 'translateY(-4px)',
                                                    boxShadow: '0 4px 12px rgba(0,0,0,0.12)'
                                                }
                                            }}
                                            onClick={() => navigate('/mis-inscripciones')}
                                        >
                                            <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                                <CheckCircleIcon sx={{ color: '#fbbf24', fontSize: 36, mb: 1 }} />
                                                <Typography variant="h5" fontWeight={700}>{resumen.misInscripcionesActivas}</Typography>
                                                <Typography variant="body2" color="text.secondary" textAlign="center">
                                                    Inscripciones Activas
                                                </Typography>
                                            </CardContent>
                                        </Card>
                                    </>
                                )}
                            </Box>
                            {/* Próximos eventos destacados */}
                            <Card sx={{ borderRadius: 3, bgcolor: theme.palette.background.paper, boxShadow: '0 2px 8px rgba(0,0,0,0.07)', border: '1px solid #e0e0e0' }}>
                                <CardContent>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                                        <Typography variant="h6" fontWeight={700}>Próximos Eventos Destacados</Typography>
                                        <Button 
                                            size="small" 
                                            onClick={() => navigate('/eventos')}
                                            sx={{ textTransform: 'none', fontWeight: 600 }}
                                        >
                                            Ver todos
                                        </Button>
                                    </Box>
                                    {loading ? (
                                        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                                            <CircularProgress size={40} />
                                        </Box>
                                    ) : eventosProximos.length === 0 ? (
                                        <Box sx={{ textAlign: 'center', py: 4 }}>
                                            <CalendarMonthIcon sx={{ fontSize: 48, color: 'text.secondary', mb: 2 }} />
                                            <Typography variant="body2" color="text.secondary">
                                                No hay eventos próximos programados
                                            </Typography>
                                            {user && (
                                                <Button 
                                                    variant="contained" 
                                                    sx={{ mt: 2 }}
                                                    onClick={() => navigate('/crear-evento')}
                                                >
                                                    Crear Evento
                                                </Button>
                                            )}
                                        </Box>
                                    ) : (
                                        <Stack spacing={2}>
                                            {eventosProximos.map(evento => (
                                                <Box 
                                                    key={evento.id} 
                                                    sx={{ 
                                                        display: 'flex', 
                                                        alignItems: 'center', 
                                                        gap: 2, 
                                                        bgcolor: '#f9fafb', 
                                                        borderRadius: 2, 
                                                        p: 1.5,
                                                        cursor: 'pointer',
                                                        transition: 'all 0.2s',
                                                        '&:hover': {
                                                            bgcolor: '#f3f4f6',
                                                            transform: 'translateX(4px)',
                                                            boxShadow: '0 2px 8px rgba(0,0,0,0.08)'
                                                        }
                                                    }}
                                                    onClick={() => navigate(`/evento/${evento.id}`)}
                                                >
                                                    <CardMedia
                                                        component="img"
                                                        image={evento.imagen}
                                                        alt={evento.titulo}
                                                        sx={{ 
                                                            width: 80, 
                                                            height: 80, 
                                                            borderRadius: 2, 
                                                            objectFit: 'cover',
                                                            flexShrink: 0
                                                        }}
                                                    />
                                                    <Box sx={{ flex: 1, minWidth: 0 }}>
                                                        <Typography variant="subtitle1" fontWeight={600} noWrap>
                                                            {evento.titulo}
                                                        </Typography>
                                                        <Stack direction="row" spacing={1} alignItems="center" sx={{ mt: 0.5 }}>
                                                            <AccessTimeIcon fontSize="small" sx={{ color: 'text.secondary' }} />
                                                            <Typography variant="caption" color="text.secondary">
                                                                {formatFecha(evento.fechaInicio)}
                                                            </Typography>
                                                        </Stack>
                                                        <Chip 
                                                            label={evento.categoria}
                                                            size="small"
                                                            sx={{ 
                                                                mt: 1,
                                                                fontWeight: 500,
                                                                bgcolor: theme.palette.secondary.main,
                                                                color: theme.palette.secondary.contrastText,
                                                                height: 24
                                                            }}
                                                        />
                                                    </Box>
                                                    <Button
                                                        variant="outlined"
                                                        size="small"
                                                        sx={{ 
                                                            borderRadius: 2, 
                                                            fontWeight: 600, 
                                                            textTransform: 'none', 
                                                            minWidth: 80,
                                                            flexShrink: 0
                                                        }}
                                                    >
                                                        Ver detalles
                                                    </Button>
                                                </Box>
                                            ))}
                                        </Stack>
                                    )}
                                </CardContent>
                            </Card>
                        </Box>
                    </Box>
                </Box>
            );
};
