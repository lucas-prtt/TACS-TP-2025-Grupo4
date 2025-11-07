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
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import PersonRemoveIcon from "@mui/icons-material/PersonRemove";

export const VerEvento = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const { getEventById, registerToEvent, getUserRegistrations, cancelRegistration, loading, error: apiError } = useGetEvents();
  const { user } = useGetAuth();
  
  const [evento, setEvento] = useState(null);
  const [loadingEvent, setLoadingEvent] = useState(true);
  const [localError, setLocalError] = useState('');
  const [success, setSuccess] = useState('');
  const [registering, setRegistering] = useState(false);
  const [cancelando, setCancelando] = useState(false);
  const [estaInscrito, setEstaInscrito] = useState(false);
  const [registrationId, setRegistrationId] = useState(null);

  // Función para verificar inscripción del usuario
  const verificarInscripcion = useCallback(async () => {
    if (!user || !id) {
      console.log(`🔍 No verificando inscripción - Usuario: ${!!user}, EventoID: ${id}`);
      return;
    }
    
    console.log(`🔍 INICIANDO verificación de inscripción para evento ${id}`);
    
    try {
      const registrations = await getUserRegistrations();
      console.log(`📋 Inscripciones del usuario (${registrations?.length || 0}):`, registrations);
      
      const inscripcionActiva = registrations.find(reg => {
        const tieneEventoId = reg.eventId;
        const esEsteEvento = tieneEventoId && (reg.eventId == id);
        const noEstaCancelada = reg.state !== 'CANCELED';
        
        console.log(`  🔍 Verificando inscripción ${reg.registrationId}:`, {
          tieneEventoId,
          esEsteEvento,
          noEstaCancelada,
          regEventId: reg.eventId,
          eventoId: id,
          state: reg.state,
          MATCH: esEsteEvento && noEstaCancelada
        });
        
        return esEsteEvento && noEstaCancelada;
      });
      
      if (inscripcionActiva) {
        console.log(`✅ ENCONTRADA inscripción activa:`, inscripcionActiva);
        setEstaInscrito(true);
        setRegistrationId(inscripcionActiva.registrationId);
      } else {
        console.log(`❌ NO hay inscripción activa para evento ${id}`);
        setEstaInscrito(false);
        setRegistrationId(null);
      }
      
      console.log(`🎯 RESULTADO FINAL: estaInscrito=${inscripcionActiva ? true : false}`);
      
    } catch (error) {
      console.error('❌ Error al verificar inscripción:', error);
      setEstaInscrito(false);
      setRegistrationId(null);
    }
  }, [user?.id, id, getUserRegistrations]);

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

  // Verificar si el usuario está inscrito al evento
  useEffect(() => {
    if (user?.id && id) {
      console.log(`⚡ useEffect disparado para verificar inscripción: userId=${user.id}, eventoId=${id}`);
      verificarInscripcion();
    }
  }, [user?.id, id, verificarInscripcion]);

  React.useEffect(() => {
    document.body.style.overflow = "auto";
    return () => { document.body.style.overflow = "auto"; };
  }, []);

  // Manejar inscripción al evento
  const handleInscribirse = async () => {
    if (!user || !evento) {
      setLocalError('Debes iniciar sesión para inscribirte');
      return;
    }

    try {
      setRegistering(true);
      setLocalError('');
      setSuccess('');
      
      const result = await registerToEvent(evento.id);
      setSuccess('¡Te has inscrito exitosamente al evento!');
      console.log('Inscripción exitosa:', result);
      // Volver a verificar el estado de inscripción desde el servidor
      await verificarInscripcion();
    } catch (err) {
      console.error('Error al inscribirse:', err);
      const errorMessage = err.response?.data?.error || 
                          err.response?.data || 
                          apiError || 
                          'Error al inscribirse al evento';
      setLocalError(`Error: ${errorMessage}`);
    } finally {
      setRegistering(false);
    }
  };

  // Manejar cancelación de inscripción
  const handleCancelarInscripcion = async () => {
    if (!user || !registrationId) {
      setLocalError('No se puede cancelar la inscripción');
      return;
    }

    const confirmar = window.confirm('¿Estás seguro de que quieres cancelar tu inscripción a este evento?');
    if (!confirmar) return;
    
    try {
      setCancelando(true);
      setLocalError('');
      setSuccess('');
      
      await cancelRegistration(registrationId);
      setSuccess('Has cancelado tu inscripción exitosamente');
      // Volver a verificar el estado de inscripción desde el servidor
      await verificarInscripcion();
    } catch (err) {
      console.error('Error al cancelar inscripción:', err);
      const errorMessage = err.response?.data?.error || 
                          err.response?.data || 
                          'Error al cancelar la inscripción';
      setLocalError(`Error: ${errorMessage}`);
    } finally {
      setCancelando(false);
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
                      ? evento.category?.title || 'Sin categoría'
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
              
              {/* Tags con scroll horizontal - después de la descripción */}
              {evento.tags && Array.isArray(evento.tags) && evento.tags.length > 0 && (
                <Box sx={{ mb: 3 }}>
                  <Typography variant="h6" fontWeight={600} sx={{ mb: 1 }}>
                    Etiquetas
                  </Typography>
                  <Box 
                    sx={{ 
                      position: 'relative'
                    }}
                  >
                    <Box
                      sx={{
                        display: 'flex',
                        gap: 1,
                        overflowX: 'auto',
                        overflowY: 'hidden',
                        pb: 1,
                        '&::-webkit-scrollbar': {
                          height: '6px',
                        },
                        '&::-webkit-scrollbar-track': {
                          backgroundColor: 'transparent',
                        },
                        '&::-webkit-scrollbar-thumb': {
                          backgroundColor: '#ccc',
                          borderRadius: '3px',
                          '&:hover': {
                            backgroundColor: '#999',
                          }
                        },
                        '&::-webkit-scrollbar-thumb:active': {
                          backgroundColor: '#666',
                        }
                      }}
                    >
                      {evento.tags.map((tag, index) => (
                        <Chip 
                          key={index} 
                          label={(
                            typeof tag === 'object' 
                              ? (tag?.nombre || tag?.name || `Tag ${index + 1}`)
                              : String(tag || `Tag ${index + 1}`)
                          ).toLowerCase()} 
                          variant="outlined"
                          sx={{
                            fontSize: '0.8rem',
                            fontWeight: 600,
                            borderColor: '#666',
                            color: '#333',
                            backgroundColor: 'transparent',
                            minWidth: 'fit-content',
                            flexShrink: 0,
                            '&:hover': {
                              backgroundColor: '#f5f5f5',
                              borderColor: '#333',
                              color: '#000',
                              transform: 'translateY(-1px)',
                              boxShadow: '0 2px 4px rgba(0,0,0,0.15)'
                            },
                            transition: 'all 0.2s ease-in-out'
                          }}
                        />
                      ))}
                    </Box>
                    {/* Indicador visual de que hay más contenido */}
                    {evento.tags.length > 4 && (
                      <Box
                        sx={{
                          position: 'absolute',
                          right: 0,
                          top: 0,
                          bottom: 8,
                          width: '20px',
                          background: 'linear-gradient(to right, transparent, #fff)',
                          pointerEvents: 'none',
                          borderRadius: '0 4px 4px 0'
                        }}
                      />
                    )}
                  </Box>
                </Box>
              )}
              
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
              
              {/* Botones de inscripción dinámicos */}
              <Stack direction="row" spacing={2} sx={{ alignSelf: "center", mt: 2 }}>
                {!estaInscrito ? (
                  <ButtonCustom
                    bgColor="#181828"
                    color="#fff"
                    hoverBgColor="#23234a"
                    hoverColor="#fff"
                    startIcon={<PersonAddIcon />}
                    sx={{ 
                      minWidth: 200, 
                      fontWeight: 700, 
                      fontSize: 16,
                      opacity: registering ? 0.7 : 1
                    }}
                    onClick={handleInscribirse}
                    disabled={registering || !user}
                  >
                    {registering ? (
                      <>
                        <CircularProgress size={16} color="inherit" sx={{ mr: 1 }} />
                        Inscribiendo...
                      </>
                    ) : (
                      'Inscribirse'
                    )}
                  </ButtonCustom>
                ) : (
                  <ButtonCustom
                    variant="outlined"
                    startIcon={<PersonRemoveIcon />}
                    onClick={handleCancelarInscripcion}
                    disabled={cancelando}
                    sx={{
                      minWidth: 200,
                      fontWeight: 700,
                      fontSize: 16,
                      border: '2px solid #DC2626',
                      color: '#DC2626',
                      backgroundColor: '#fff',
                      opacity: cancelando ? 0.7 : 1,
                      '&:hover': {
                        backgroundColor: '#FEE2E2',
                        color: '#B91C1C',
                        borderColor: '#B91C1C'
                      }
                    }}
                  >
                    {cancelando ? (
                      <>
                        <CircularProgress size={16} color="inherit" sx={{ mr: 1 }} />
                        Cancelando...
                      </>
                    ) : (
                      'Cancelar Inscripción'
                    )}
                  </ButtonCustom>
                )}
              </Stack>
            </>
          ) : null}
        </Box>
      </Box>
    </Box>
  );
};