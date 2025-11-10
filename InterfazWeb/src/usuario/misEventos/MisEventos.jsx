import React, { useState, useEffect, useMemo } from "react";
import { Box, Typography, Card, CardContent, CardMedia, Stack, Chip, CircularProgress, Alert, Button } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import { ButtonCustom } from "../../components/Button";
import { NavbarApp } from "../../components/NavbarApp";
import { useNavigate } from "react-router-dom";
import { useGetEvents } from "../../hooks/useGetEvents";
import noImagePlaceholder from "../../assets/images/no_image.png";

export const MisEventos = () => {
  const theme = useTheme();
  const navigate = useNavigate();
  const { loading, error, events, getOrganizedEvents } = useGetEvents();

  // Cargar eventos organizados al montar el componente
  useEffect(() => {
    let isMounted = true;
    
    const loadOrganizedEvents = async () => {
      try {
        const result = await getOrganizedEvents();
        if (isMounted) {
        }
      } catch (err) {
        if (isMounted) {
        }
      }
    };
    
    loadOrganizedEvents();
    
    return () => {
      isMounted = false;
    };
  }, []); // Array vacío - solo se ejecuta al montar el componente

  // Función para recargar eventos manualmente
  const handleReload = async () => {
    try {
      await getOrganizedEvents();
    } catch (err) {
    }
  };

  // Mapear eventos de API al formato esperado (similar a Eventos.jsx)
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

    // Manejar estado
    let estado = "activo";
    if (evento.state) {
      if (typeof evento.state === 'string') {
        estado = evento.state.toLowerCase();
      } else if (typeof evento.state === 'object' && evento.state.name) {
        estado = evento.state.name.toLowerCase();
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
      imagen: evento.image || noImagePlaceholder
    };
  }).filter(evento => evento !== null), [events]);

  const handleVer = (id) => {
    navigate(`/evento/${id}`);
  };

  const handleEditar = (id) => {
    navigate(`/editar-evento/${id}`);
  };

  // Función para traducir el estado del evento al español
  const getEstadoTraducido = (estado) => {
    if (!estado) return '';
    
    const estadoUpper = estado.toUpperCase();
    switch (estadoUpper) {
      case 'EVENT_OPEN':
        return 'Abierto';
      case 'EVENT_CLOSED':
        return 'Cerrado';
      case 'EVENT_CANCELLED':
        return 'Cancelado';
      case 'EVENT_PAUSED':
        return 'Pausado';
      default:
        // Si viene solo el estado sin el prefijo EVENT_, también lo manejamos
        switch (estadoUpper) {
          case 'OPEN':
            return 'Abierto';
          case 'CLOSED':
            return 'Cerrado';
          case 'CANCELLED':
            return 'Cancelado';
          case 'PAUSED':
            return 'Pausado';
          default:
            return estado; // Devolver el estado original si no coincide
        }
    }
  };

  // Función para obtener los colores del estado
  const getEstadoColors = (estado) => {
    if (!estado) return { bgcolor: '#6B7280', color: '#fff' };
    
    const estadoUpper = estado.toUpperCase();
    
    // EVENT_OPEN o OPEN -> Verde
    if (estadoUpper.includes('OPEN')) {
      return {
        bgcolor: '#10B981', // Verde
        color: '#fff'
      };
    }
    
    // EVENT_CLOSED o CLOSED -> Rojo
    if (estadoUpper.includes('CLOSED')) {
      return {
        bgcolor: '#EF4444', // Rojo
        color: '#fff'
      };
    }
    
    // EVENT_CANCELLED o CANCELLED -> Naranja oscuro
    if (estadoUpper.includes('CANCELLED')) {
      return {
        bgcolor: '#D97706', // Naranja oscuro
        color: '#fff'
      };
    }
    
    // Por defecto (gris)
    return {
      bgcolor: '#6B7280',
      color: '#fff'
    };
  };

  // Estilos de botones igual que en CardEvento
  const sxVer = {
    border: '2px solid #8B5CF6',
    color: '#8B5CF6',
    backgroundColor: '#fff',
    fontWeight: 700,
    borderRadius: '10px',
    px: 2,
    minHeight: { xs: 34, md: 44 },
    py: { xs: 0.5, md: 1 },
    fontSize: { xs: 15, md: 16 },
    '&:hover': {
      backgroundColor: '#F3F4F6',
      color: '#7C3AED',
      borderColor: '#7C3AED'
    }
  };

  const sxEditar = {
    border: '2px solid #F59E0B',
    color: '#F59E0B',
    backgroundColor: '#fff',
    fontWeight: 700,
    borderRadius: '10px',
    px: 2,
    minHeight: { xs: 34, md: 44 },
    py: { xs: 0.5, md: 1 },
    fontSize: { xs: 15, md: 16 },
    '&:hover': {
      backgroundColor: '#FEF3C7',
      color: '#D97706',
      borderColor: '#D97706'
    }
  };

  return (
    <Box minHeight="100vh" sx={{ display: "flex", bgcolor: "#f8f8fa" }}>
      {/* Navbar lateral desktop */}
      <Box sx={{ width: { xs: 0, md: "250px" }, flexShrink: 0, display: { xs: "none", md: "block" } }}>
        <NavbarApp />
      </Box>
      {/* Contenido principal */}
      <Box
        flex={1}
        sx={{
          p: { xs: 2, md: 4 },
          width: "100%",
          maxWidth: { xs: 440, sm: 600, md: "100%" }, // Limita solo en mobile/tablet
          mx: { xs: "auto", md: 0 },
          transition: "max-width 0.2s",
        }}
      >
        {/* Navbar hamburguesa mobile (fuera del flow del contenido) */}
        <Box
          sx={{
            display: { xs: "block", md: "none" },
            position: "fixed",
            top: 0,
            left: 0,
            zIndex: 1300,
            width: "100vw",
          }}
        >
          <NavbarApp />
        </Box>
        <Typography variant="h4" fontWeight={700} sx={{ mb: 3 }}>
          Mis Eventos
        </Typography>

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
          <Stack spacing={3}>
            {eventosFormateados.length === 0 ? (
              <Typography variant="body1" color="text.secondary">
                No tenés eventos propios.
              </Typography>
            ) : (
              eventosFormateados.map((evento) => (
                <Card
                  key={evento.id}
                  sx={{
                    display: "flex",
                    flexDirection: { xs: "column", sm: "row" },
                    alignItems: { xs: "stretch", sm: "center" },
                    borderRadius: 3,
                    boxShadow: "0 2px 8px rgba(0,0,0,0.07)",
                    border: "1px solid #e0e0e0",
                    bgcolor: theme.palette.background.paper,
                    overflow: "hidden",
                  }}
                >
                  <CardMedia
                    component="img"
                    image={evento.imagen}
                    alt={evento.titulo}
                    sx={{
                      width: { xs: "100%", sm: 160 },
                      height: { xs: 160, sm: 140 },
                      objectFit: "cover"
                    }}
                  />
                  <CardContent sx={{ flex: 1, display: "flex", flexDirection: "column", justifyContent: "center" }}>
                    <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
                      <Chip
                        label={evento.categoria}
                        size="small"
                        sx={{
                          fontWeight: 500,
                          bgcolor: theme.palette.secondary.main,
                          color: theme.palette.secondary.contrastText,
                        }}
                      />
                      <Chip
                        label={getEstadoTraducido(evento.estado)}
                        size="small"
                        sx={{ 
                          fontWeight: 600,
                          ...getEstadoColors(evento.estado)
                        }}
                      />
                    </Stack>
                    <Typography variant="h6" fontWeight={700} sx={{ mb: 0.5 }}>
                      {evento.titulo}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                      {evento.descripcion}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                      <b>Fecha:</b> {new Date(evento.fechaInicio).toLocaleString("es-AR", {
                        day: "numeric",
                        month: "short",
                        year: "numeric",
                        hour: "2-digit",
                        minute: "2-digit"
                      })}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                      <b>Ubicación:</b> {evento.lugar}
                    </Typography>
                    <Stack direction="row" spacing={2} sx={{ mt: 1 }}>
                      <ButtonCustom
                        variant="outlined"
                        startIcon={<VisibilityIcon />}
                        sx={sxVer}
                        onClick={() => handleVer(evento.id)}
                      >
                        Ver
                      </ButtonCustom>
                      <ButtonCustom
                        variant="outlined"
                        startIcon={<EditIcon />}
                        sx={sxEditar}
                        onClick={() => handleEditar(evento.id)}
                      >
                        Editar
                      </ButtonCustom>
                    </Stack>
                  </CardContent>
                </Card>
              ))
            )}
          </Stack>
        )}
      </Box>
    </Box>
  );
};
