import { Box, Typography, Grid, Card, CardContent, Stack, Chip, Divider, Button, CardMedia } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import GroupsIcon from '@mui/icons-material/Groups';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import { NavbarApp } from "../../components/NavbarApp";
import { datosEventos } from "../eventos/datosEventos";
import { useNavigate } from "react-router-dom";
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

// Datos simulados
const resumen = {
    totalEventos: 25,
    proximos: 8,
    totalAsistentes: 1245,
    promedioAsistencia: 85,
};

const actividadReciente = [
    { color: '#4ade80', texto: 'Nuevo registro para Workshop de Desarrollo Web', tiempo: 'Hace 2 horas' },
    { color: '#60a5fa', texto: 'Evento "Conferencia de Tecnología 2025" actualizado', tiempo: 'Hace 4 horas' },
    { color: '#a78bfa', texto: 'Nuevo evento "Meetup de Diseño UX" creado', tiempo: 'Ayer' },
    { color: '#fbbf24', texto: 'Recordatorio enviado para Seminario de Marketing', tiempo: 'Hace 2 días' },
];

export const DashBoard = () => {
        const theme = useTheme();
        const navigate = useNavigate();

        // Obtener próximos 3 eventos ordenados por fecha
        const eventosProximos = [...datosEventos]
            .filter(e => new Date(e.fechaInicio) > new Date())
            .sort((a, b) => new Date(a.fechaInicio) - new Date(b.fechaInicio))
            .slice(0, 3);

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
                            <Box sx={{ display: 'flex', gap: 3, mb: 4, width: '100%' }}>
                                <Card sx={{ flex: 1, borderRadius: 3, boxShadow: '0 2px 8px rgba(0,0,0,0.07)', border: '1px solid #e0e0e0', bgcolor: theme.palette.background.paper }}>
                                    <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                        <CalendarMonthIcon sx={{ color: '#6366f1', fontSize: 36, mb: 1 }} />
                                        <Typography variant="h5" fontWeight={700}>{resumen.totalEventos}</Typography>
                                        <Typography variant="body2" color="text.secondary">Total Eventos</Typography>
                                    </CardContent>
                                </Card>
                                <Card sx={{ flex: 1, borderRadius: 3, boxShadow: '0 2px 8px rgba(0,0,0,0.07)', border: '1px solid #e0e0e0', bgcolor: theme.palette.background.paper }}>
                                    <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                        <AccessTimeIcon sx={{ color: '#34d399', fontSize: 36, mb: 1 }} />
                                        <Typography variant="h5" fontWeight={700}>{resumen.proximos}</Typography>
                                        <Typography variant="body2" color="text.secondary">Próximos</Typography>
                                    </CardContent>
                                </Card>
                                <Card sx={{ flex: 1, borderRadius: 3, boxShadow: '0 2px 8px rgba(0,0,0,0.07)', border: '1px solid #e0e0e0', bgcolor: theme.palette.background.paper }}>
                                    <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                        <GroupsIcon sx={{ color: '#a78bfa', fontSize: 36, mb: 1 }} />
                                        <Typography variant="h5" fontWeight={700}>{resumen.totalAsistentes}</Typography>
                                        <Typography variant="body2" color="text.secondary">Total Asistentes</Typography>
                                    </CardContent>
                                </Card>
                                <Card sx={{ flex: 1, borderRadius: 3, boxShadow: '0 2px 8px rgba(0,0,0,0.07)', border: '1px solid #e0e0e0', bgcolor: theme.palette.background.paper }}>
                                    <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                        <TrendingUpIcon sx={{ color: '#fbbf24', fontSize: 36, mb: 1 }} />
                                        <Typography variant="h5" fontWeight={700}>{resumen.promedioAsistencia}%</Typography>
                                        <Typography variant="body2" color="text.secondary">Promedio Asistencia</Typography>
                                    </CardContent>
                                </Card>
                            </Box>
                            {/* Grillas de próximos eventos y actividad reciente */}
                                        <Box sx={{ display: 'flex', gap: 3, width: '100%' }}>
                                            <Card sx={{ flex: 1, borderRadius: 3, minHeight: 260, bgcolor: theme.palette.background.paper, boxShadow: '0 2px 8px rgba(0,0,0,0.07)', border: '1px solid #e0e0e0' }}>
                                                <CardContent>
                                                    <Typography variant="h6" fontWeight={700} sx={{ mb: 2 }}>Próximos Eventos</Typography>
                                                    {eventosProximos.length === 0 ? (
                                                        <Typography variant="body2" color="text.secondary">No hay próximos eventos.</Typography>
                                                    ) : (
                                                        <Stack spacing={2}>
                                                            {eventosProximos.map(evento => (
                                                                <Box key={evento.id} sx={{ display: 'flex', alignItems: 'center', gap: 2, bgcolor: '#f9fafb', borderRadius: 2, p: 1.5 }}>
                                                                    <CardMedia
                                                                        component="img"
                                                                        image={evento.imagen}
                                                                        alt={evento.titulo}
                                                                        sx={{ width: 56, height: 56, borderRadius: 2, objectFit: 'cover', mr: 1 }}
                                                                    />
                                                                    <Box sx={{ flex: 1, minWidth: 0 }}>
                                                                        <Typography variant="subtitle2" fontWeight={600} noWrap>{evento.titulo}</Typography>
                                                                        <Typography variant="caption" color="text.secondary">{formatFecha(evento.fechaInicio)}</Typography>
                                                                    </Box>
                                                                    <Button
                                                                        variant="outlined"
                                                                        size="small"
                                                                        sx={{ borderRadius: 2, fontWeight: 600, textTransform: 'none', minWidth: 0, px: 2 }}
                                                                        onClick={() => navigate(`/evento/${evento.id}`)}
                                                                    >
                                                                        Ver
                                                                    </Button>
                                                                </Box>
                                                            ))}
                                                        </Stack>
                                                    )}
                                                </CardContent>
                                            </Card>
                                            <Card sx={{ flex: 1, borderRadius: 3, minHeight: 260, bgcolor: theme.palette.background.paper, boxShadow: '0 2px 8px rgba(0,0,0,0.07)', border: '1px solid #e0e0e0' }}>
                                                <CardContent>
                                                    <Typography variant="h6" fontWeight={700} sx={{ mb: 2 }}>Actividad Reciente</Typography>
                                                    <Stack spacing={1}>
                                                        {actividadReciente.map((item, idx) => (
                                                            <Box key={idx} sx={{ display: 'flex', alignItems: 'center', bgcolor: '#f9fafb', borderRadius: 2, px: 2, py: 1 }}>
                                                                <Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: item.color, mr: 1.5 }} />
                                                                <Box>
                                                                    <Typography variant="body2" fontWeight={500}>{item.texto}</Typography>
                                                                    <Typography variant="caption" color="text.secondary">{item.tiempo}</Typography>
                                                                </Box>
                                                            </Box>
                                                        ))}
                                                    </Stack>
                                                </CardContent>
                                            </Card>
                                        </Box>
                        </Box>
                    </Box>
                </Box>
            );
};