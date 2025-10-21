import React, { useState, useEffect, useCallback } from "react";
import { Box, Typography, Chip, Divider, Stack, Button, CircularProgress, Alert } from "@mui/material";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import PlaceIcon from "@mui/icons-material/Place";
import PeopleIcon from "@mui/icons-material/People";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { useNavigate, useParams } from "react-router-dom";
import { ButtonCustom } from "../../components/Button";
import { NavbarApp } from "../../components/NavbarApp";
import { Mapa } from "./Mapa";
import { useGetEvents } from "../../hooks/useGetEvents";
import { useGetAuth } from "../../hooks/useGetAuth";

export const VerEvento = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const { getEventById, registerToEvent, loading, error: apiError } = useGetEvents();
  const { user } = useGetAuth();
  
  const [evento, setEvento] = useState(null);
  const [loadingEvent, setLoadingEvent] = useState(true);
  const [localError, setLocalError] = useState('');
  const [success, setSuccess] = useState('');
  const [registering, setRegistering] = useState(false);

  // Función para cargar evento - memoizada para evitar recreaciones
  const loadEvent = useCallback(async () => {
    console.log('🔄 loadEvent ejecutándose con ID:', id);
    
    if (!id) {
      console.log('❌ No hay ID de evento');
      setLocalError('ID de evento no encontrado');
      setLoadingEvent(false);
      return;
    }

    try {
      console.log('⏳ Iniciando carga del evento...');
      setLoadingEvent(true);
      setLocalError('');
      const eventData = await getEventById(id);
      console.log('✅ Evento cargado exitosamente:', eventData);
      console.log('🔍 Tipos de campos:', {
        title: typeof eventData.title,
        description: typeof eventData.description,
        category: typeof eventData.category,
        state: typeof eventData.state,
        tags: typeof eventData.tags,
        location: typeof eventData.location
      });
      setEvento(eventData);
      setLoadingEvent(false);
      console.log('✅ Estado actualizado, carga completada');
    } catch (err) {
      console.error('❌ Error al cargar el evento:', err);
      setLocalError('Error al cargar los datos del evento');
      setLoadingEvent(false);
    }
  }, [id, getEventById]);

  // Cargar evento al montar el componente
  useEffect(() => {
    console.log('🔄 useEffect ejecutándose para cargar evento...');
    loadEvent();
  }, [loadEvent]);

  React.useEffect(() => {
    document.body.style.overflow = "auto";
    return () => { document.body.style.overflow = "auto"; };
  }, []);

  // Manejar inscripción al evento
  const handleInscribirse = async () => {
    if (!user || !evento) return;

    try {
      setRegistering(true);
      setLocalError('');
      setSuccess('');
      
      await registerToEvent(evento.id);
      setSuccess('¡Te has inscrito exitosamente al evento!');
    } catch (err) {
      console.error('Error al inscribirse:', err);
      setLocalError(apiError || 'Error al inscribirse al evento');
    } finally {
      setRegistering(false);
    }
  };

  return (
    <Box minHeight="100vh" sx={{ display: 'flex', flexDirection: 'row', bgcolor: "#f8f8fa" }}>
      {/* Navbar lateral desktop */}
      <Box
        sx={{
          width: { xs: 0, md: '250px' },
          flexShrink: 0,
          display: { xs: 'none', md: 'block' }
        }}
      >
        <NavbarApp />
      </Box>
      {/* Navbar hamburguesa mobile */}
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
          alignItems: 'center',
          minHeight: "100vh",
        }}
      >
        <Box
          sx={{
            width: "100%",
            maxWidth: 900,
            bgcolor: "#fff",
            borderRadius: 3,
            boxShadow: "0 2px 8px rgba(0,0,0,0.07)",
            border: "1px solid #e0e0e0",
            p: { xs: 2, sm: 4 },
            display: "flex",
            flexDirection: "column",
            gap: 2,
          }}
        >
          <Button
            startIcon={<ArrowBackIcon />}
            sx={{ mb: 1, textTransform: "none", alignSelf: "flex-start" }}
            onClick={() => navigate(-1)}
          >
            Volver
          </Button>

          {/* Estados de carga y error */}
          {loadingEvent ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 400 }}>
              <CircularProgress />
              <Typography sx={{ ml: 2 }}>Cargando evento...</Typography>
            </Box>
          ) : localError && !evento ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 400 }}>
              <Alert severity="error">{localError}</Alert>
            </Box>
          ) : evento ? (
            <>
              {/* Alertas de feedback */}
              {localError && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {localError}
                </Alert>
              )}
              {success && (
                <Alert severity="success" sx={{ mb: 2 }}>
                  {success}
                </Alert>
              )}
              <Stack direction="row" spacing={1} sx={{ mb: 1 }}>
                <Chip 
                  label={
                    typeof evento.category === 'object' 
                      ? evento.category?.name || 'Sin categoría'
                      : evento.category || 'Sin categoría'
                  } 
                  color="primary" 
                />
                <Chip 
                  label={
                    typeof evento.state === 'object'
                      ? evento.state?.name || 'Activo'
                      : evento.state || 'Activo'
                  } 
                  color="success" 
                />
              </Stack>
              {/* Título */}
              <Typography variant="h4" fontWeight={700} sx={{ mb: 2 }}>
                {String(evento.title || 'Sin título')}
              </Typography>
              {/* Imagen */}
              <Box
                sx={{
                  width: "100%",
                  height: { xs: 260, sm: 340, md: 420 },
                  borderRadius: 2,
                  overflow: "hidden",
                  background: "#eee",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  mb: 2
                }}
              >
                {evento.image ? (
                  <img
                    src={evento.image}
                    alt={evento.title}
                    style={{ width: "100%", height: "100%", objectFit: "cover", display: "block" }}
                    onError={(e) => {
                      e.target.style.display = 'none';
                      e.target.parentNode.innerHTML = `
                        <div style="display: flex; align-items: center; justify-content: center; width: 100%; height: 100%; color: #666; font-size: 18px;">
                          Sin imagen disponible
                        </div>
                      `;
                    }}
                  />
                ) : (
                  <Typography variant="h6" color="text.secondary">
                    Sin imagen disponible
                  </Typography>
                )}
              </Box>
              {/* Descripción */}
              <Typography variant="h6" fontWeight={600} sx={{ mb: 1 }}>
                Descripción
              </Typography>
              <Typography variant="body1" color="text.secondary" sx={{ mb: 2 }}>
                {String(evento.description || 'Sin descripción')}
              </Typography>
              <Divider sx={{ mb: 2 }} />
              {/* Precio */}
              <Typography variant="h6" fontWeight={600} sx={{ mb: 1 }}>
                Precio
              </Typography>
              <Typography variant="body1" sx={{ mb: 2 }}>
                ${Number(evento.price || 0)}
              </Typography>
              {/* Capacidad */}
              <Typography variant="h6" fontWeight={600} sx={{ mb: 1 }}>
                Capacidad
              </Typography>
              <Stack direction="row" spacing={2} alignItems="center" sx={{ mb: 2 }}>
                <PeopleIcon color="action" />
                <Typography variant="body2" color="text.secondary">
                  {Number(evento.minParticipants || 0)} / {Number(evento.maxParticipants || 0)} asistentes
                </Typography>
              </Stack>
              {/* Fecha y hora */}
              <Typography variant="h6" fontWeight={600} sx={{ mb: 1 }}>
                Fecha y Hora
              </Typography>
              <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
                <CalendarMonthIcon color="action" />
                <Typography variant="body2" color="text.secondary">
                  {(() => {
                    try {
                      const fecha = new Date(evento.startDateTime).toLocaleString("es-AR", {
                        day: "numeric",
                        month: "short", 
                        year: "numeric",
                        hour: "2-digit",
                        minute: "2-digit"
                      });
                      const duracion = evento.durationMinutes 
                        ? ` - Duración: ${Number(evento.durationMinutes)} minutos`
                        : '';
                      return fecha + duracion;
                    } catch (err) {
                      return String(evento.startDateTime || 'Fecha no disponible');
                    }
                  })()}
                </Typography>
              </Stack>
              {/* Ubicación */}
              <Typography variant="h6" fontWeight={600} sx={{ mb: 1 }}>
                Ubicación
              </Typography>
              <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
                <PlaceIcon color="action" />
                <Typography variant="body2" color="text.secondary">
                  {String(evento.location || 'Sin ubicación')}
                </Typography>
              </Stack>
              {/* Mapa */}
              <Mapa direccion={String(evento.location || 'Sin ubicación')} />
              {/* Tags */}
              <Stack direction="row" spacing={1} sx={{ my: 2 }}>
                {evento.tags && Array.isArray(evento.tags) && evento.tags.map((tag, index) => (
                  <Chip 
                    key={index} 
                    label={
                      typeof tag === 'object' 
                        ? tag?.name || `Tag ${index + 1}` 
                        : String(tag || `Tag ${index + 1}`)
                    } 
                    variant="outlined" 
                  />
                ))}
              </Stack>
              {/* Botón inscribirse */}
              <ButtonCustom
                bgColor="#181828"
                color="#fff"
                hoverBgColor="#23234a"
                hoverColor="#fff"
                sx={{ 
                  alignSelf: "center", 
                  minWidth: 180, 
                  fontWeight: 700, 
                  fontSize: 16, 
                  mt: 1 
                }}
                onClick={handleInscribirse}
                disabled={registering || !user}
              >
                {registering ? (
                  <CircularProgress size={20} color="inherit" />
                ) : (
                  'Inscribirse'
                )}
              </ButtonCustom>
            </>
          ) : null}
        </Box>
      </Box>
    </Box>
  );
};