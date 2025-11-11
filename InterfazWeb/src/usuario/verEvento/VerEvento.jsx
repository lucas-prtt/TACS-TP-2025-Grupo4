import React, { useState, useEffect, useCallback } from "react";
import { Box, Typography, Chip, Divider, Stack, Button, CircularProgress, Alert } from "@mui/material";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import PlaceIcon from "@mui/icons-material/Place";
import PeopleIcon from "@mui/icons-material/People";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import EditIcon from "@mui/icons-material/Edit";
import { useNavigate, useParams } from "react-router-dom";
import { ButtonCustom } from "../../components/Button";
import { ConfirmDialog } from "../../components/ConfirmDialog";
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
  
  // Estados para los diálogos
  const [openWaitlistDialog, setOpenWaitlistDialog] = useState(false);
  const [openInscripcionDialog, setOpenInscripcionDialog] = useState(false);
  const [openCancelacionDialog, setOpenCancelacionDialog] = useState(false);
  const [openErrorDialog, setOpenErrorDialog] = useState(false);
  const [errorDialogMessage, setErrorDialogMessage] = useState('');

  // Determinar si el usuario actual es el organizador del evento
  const isOrganizador = user && evento && evento.usernameOrganizer && (
    user.username === evento.usernameOrganizer || 
    user.id === evento.usernameOrganizer ||
    String(user.id) === String(evento.usernameOrganizer)
  );

  // Función para verificar inscripción del usuario
  const verificarInscripcion = useCallback(async () => {
    if (!user || !id) {
      return;
    }
    
    
    try {
      const registrations = await getUserRegistrations();
      
      
      const inscripcionActiva = registrations.find(reg => {
        const tieneEventoId = reg.eventId;
        const esEsteEvento = tieneEventoId && (reg.eventId == id);
        const noEstaCancelada = reg.state !== 'CANCELED';

        
        return esEsteEvento && noEstaCancelada;
      });
      
      if (inscripcionActiva) {
        setEstaInscrito(true);
        setRegistrationId(inscripcionActiva.registrationId);
      } else {
        setEstaInscrito(false);
        setRegistrationId(null);
      }
      
      
    } catch (error) {
      
      setEstaInscrito(false);
      setRegistrationId(null);
    }
  }, [user?.id, id, getUserRegistrations]);

  // Función para cargar evento - memoizada para evitar recreaciones
  const loadEvent = useCallback(async () => {
    
    
    if (!id) {
      setLocalError('ID de evento no encontrado');
      setLoadingEvent(false);
      return;
    }

    try {
      
      setLoadingEvent(true);
      setLocalError('');
      const eventData = await getEventById(id);
      setEvento(eventData);
      setLoadingEvent(false);
    } catch (err) {
      setLocalError('Error al cargar los datos del evento');
      setLoadingEvent(false);
    }
  }, [id, getEventById]);

  // Cargar evento al montar el componente
  useEffect(() => {
    loadEvent();
  }, [loadEvent]);

  // Verificar si el usuario está inscrito al evento
  useEffect(() => {
    if (user?.id && id) {
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

    // Verificar si el evento está lleno
    if (evento.registered >= evento.maxParticipants) {
      setOpenWaitlistDialog(true);
      return;
    }

    // Si hay espacio, mostrar diálogo de confirmación
    setOpenInscripcionDialog(true);
  };

  // Procesar inscripción (usado tanto para inscripción normal como confirmada desde diálogo)
  const proceedWithRegistration = async () => {
    try {
      setRegistering(true);
      setLocalError('');
      setSuccess('');
      setOpenWaitlistDialog(false);
      setOpenInscripcionDialog(false);
      
      const result = await registerToEvent(evento.id);
      
      // Volver a verificar el estado de inscripción desde el servidor
      await verificarInscripcion();
    } catch (err) {
      const errorMsg = err.response?.data?.error || 
                       err.response?.data?.message ||
                       err.response?.data || 
                       apiError || 
                       'Error al inscribirse al evento. Por favor, intenta nuevamente.';
      setErrorDialogMessage(errorMsg);
      setOpenErrorDialog(true);
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

    // Mostrar diálogo de confirmación
    setOpenCancelacionDialog(true);
  };

  // Procesar cancelación
  const proceedWithCancellation = async () => {
    try {
      setCancelando(true);
      setLocalError('');
      setSuccess('');
      setOpenCancelacionDialog(false);
      
      await cancelRegistration(registrationId);
      // Volver a verificar el estado de inscripción desde el servidor
      await verificarInscripcion();
    } catch (err) {
      const errorMsg = err.response?.data?.error || 
                       err.response?.data?.message ||
                       err.response?.data || 
                       'Error al cancelar la inscripción. Por favor, intenta nuevamente.';
      setErrorDialogMessage(errorMsg);
      setOpenErrorDialog(true);
    } finally {
      setCancelando(false);
    }
  };

  // Manejar edición del evento
  const handleEditarEvento = () => {
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
                    getEstadoTraducido(
                      typeof evento.state === 'object'
                        ? evento.state?.name || 'EVENT_OPEN'
                        : evento.state || 'EVENT_OPEN'
                    )
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
              
              {/* Botones dinámicos según el rol del usuario */}
              <Stack direction="row" spacing={2} sx={{ alignSelf: "center", mt: 2 }}>
                {isOrganizador ? (
                  // Botones para el organizador del evento
                  <>
                    <ButtonCustom
                      variant="outlined"
                      startIcon={<EditIcon />}
                      onClick={handleEditarEvento}
                      sx={{
                        minWidth: 200,
                        fontWeight: 700,
                        fontSize: 16,
                        border: '2px solid #F59E0B',
                        color: '#F59E0B',
                        backgroundColor: '#fff',
                        '&:hover': {
                          backgroundColor: '#FEF3C7',
                          color: '#D97706',
                          borderColor: '#D97706'
                        }
                      }}
                    >
                      Editar Evento
                    </ButtonCustom>
                  </>
                ) : (
                  // Botones para usuarios regulares (inscripción)
                  <>
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
                  </>
                )}
              </Stack>
            </>
          ) : null}
        </Box>
      </Box>

      {/* Diálogo de confirmación para lista de espera */}
      <ConfirmDialog
        open={openWaitlistDialog}
        onClose={() => setOpenWaitlistDialog(false)}
        onConfirm={proceedWithRegistration}
        title="Evento Completo"
        message={`El evento ha alcanzado su capacidad máxima de ${evento?.maxParticipants} participantes. Serás añadido a la <strong>lista de espera</strong> y podrás inscribirte si se libera un cupo.<br/><br/>¿Deseas continuar?`}
        confirmText="Confirmar"
        cancelText="Cancelar"
        loading={registering}
        loadingText="Confirmando..."
        type="warning"
      />

      {/* Diálogo de confirmación para inscripción normal */}
      <ConfirmDialog
        open={openInscripcionDialog}
        onClose={() => setOpenInscripcionDialog(false)}
        onConfirm={proceedWithRegistration}
        title="Confirmar Inscripción"
        message={`¿Estás seguro de que deseas inscribirte al evento <strong>"${evento?.title}"</strong>?`}
        confirmText="Inscribirse"
        cancelText="Cancelar"
        loading={registering}
        loadingText="Inscribiendo..."
        type="success"
      />

      {/* Diálogo de confirmación para cancelación */}
      <ConfirmDialog
        open={openCancelacionDialog}
        onClose={() => setOpenCancelacionDialog(false)}
        onConfirm={proceedWithCancellation}
        title="Cancelar Inscripción"
        message={`¿Estás seguro de que deseas cancelar tu inscripción al evento <strong>"${evento?.title}"</strong>?<br/><br/>Esta acción no se puede deshacer.`}
        confirmText="Sí, cancelar"
        cancelText="No, mantener"
        loading={cancelando}
        loadingText="Cancelando..."
        type="error"
      />

      {/* Diálogo de error */}
      <ConfirmDialog
        open={openErrorDialog}
        onClose={() => setOpenErrorDialog(false)}
        onConfirm={() => setOpenErrorDialog(false)}
        title="Error"
        message={errorDialogMessage}
        confirmText="Entendido"
        cancelText=""
        type="error"
      />
    </Box>
  );
};