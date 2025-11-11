import React, { useState, useEffect, useCallback } from 'react';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import EditIcon from '@mui/icons-material/Edit';
import VisibilityIcon from '@mui/icons-material/Visibility';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import PlaceIcon from '@mui/icons-material/Place';
import PeopleIcon from '@mui/icons-material/People';
import { format } from 'date-fns';
import { es, tr } from 'date-fns/locale';
import { ButtonCustom } from '../../components/Button';
import { ConfirmDialog } from '../../components/ConfirmDialog';
import { useNavigate } from "react-router-dom";
import { useTheme } from '@mui/material/styles';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import PersonRemoveIcon from '@mui/icons-material/PersonRemove';
import noImagePlaceholder from '../../assets/images/no_image.png';
import { useAuth } from '../../contexts/AuthContext';
import { useGetEvents } from '../../hooks/useGetEvents';

export const CardEvento = ({ evento, onVerEvento }) => {
  const theme = useTheme();
  const navigate = useNavigate();
  const { user } = useAuth();
  const { registerToEvent, getUserRegistrations, cancelRegistration, loading: eventLoading } = useGetEvents();
  const [imageError, setImageError] = useState(false);
  const [inscribiendose, setInscribiendose] = useState(false);
  const [cancelando, setCancelando] = useState(false);
  const [estaInscrito, setEstaInscrito] = useState(false);
  const [registrationId, setRegistrationId] = useState(null);
  const [verificandoInscripcion, setVerificandoInscripcion] = useState(false);
  
  // Estados para los diálogos
  const [openWaitlistDialog, setOpenWaitlistDialog] = useState(false);
  const [openInscripcionDialog, setOpenInscripcionDialog] = useState(false);
  const [openCancelacionDialog, setOpenCancelacionDialog] = useState(false);
  const [openErrorDialog, setOpenErrorDialog] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  // Función para verificar inscripción basada en la lógica de MisIncripciones
  const verificarInscripcion = useCallback(async () => {
    if (!user || !evento.id) {
      return;
    }
    
    try {
      const registrations = await getUserRegistrations();
      
      // IGUAL que MisIncripciones: Los datos están directamente en el registration
      const inscripcionActiva = registrations.find(reg => {
        // Los datos del evento están directamente en reg, no en reg.event
        const tieneEventoId = reg.eventId;
        const esEsteEvento = tieneEventoId && (reg.eventId == evento.id);
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
  }, [user?.id, evento.id, getUserRegistrations]);

  // Resetear estado de error cuando cambia la imagen del evento
  useEffect(() => {
    setImageError(false);
  }, [evento.imagen]);

  // Verificar si el usuario está inscrito al evento
  useEffect(() => {
    if (user?.id && evento.id) {
      verificarInscripcion();
    }
  }, [user?.id, evento.id, verificarInscripcion]);

  // Determinar si el usuario actual es el organizador del evento
  const isOrganizador = user && evento.organizador_id && (
    user.username === evento.organizador_id || 
    user.id === evento.organizador_id ||
    String(user.id) === String(evento.organizador_id)
  );
  const isAdmin = user && user.roles && user.roles.includes('ADMIN');
  const isUser = !isOrganizador && !isAdmin;

  // Funciones para manejar acciones
  const handleInscribirse = async () => {
    if (!user) {
      alert('Debes iniciar sesión para inscribirte');
      return;
    }
    
    // Verificar si el evento está lleno
    if (evento.participantes_registrados >= evento.max_participantes) {
      setOpenWaitlistDialog(true);
      return;
    }

    // Si hay espacio, mostrar diálogo de confirmación
    setOpenInscripcionDialog(true);
  };

  // Procesar inscripción
  const proceedWithRegistration = async () => {
    try {
      setInscribiendose(true);
      setOpenWaitlistDialog(false);
      setOpenInscripcionDialog(false);
      
      await registerToEvent(evento.id);
      
      // Volver a verificar el estado de inscripción desde el servidor
      await verificarInscripcion();
    } catch (error) {
      const errorMsg = error.response?.data?.error || 
                       error.response?.data?.message ||
                       error.response?.data || 
                       'Error al inscribirse al evento. Por favor, intenta nuevamente.';
      setErrorMessage(errorMsg);
      setOpenErrorDialog(true);
    } finally {
      setInscribiendose(false);
    }
  };

  const handleCancelarInscripcion = async () => {
    if (!user || !registrationId) {
      alert('No se puede cancelar la inscripción');
      return;
    }

    // Mostrar diálogo de confirmación
    setOpenCancelacionDialog(true);
  };

  // Procesar cancelación
  const proceedWithCancellation = async () => {
    try {
      setCancelando(true);
      setOpenCancelacionDialog(false);
      
      await cancelRegistration(registrationId);
      // Volver a verificar el estado de inscripción desde el servidor
      await verificarInscripcion();
    } catch (error) {
      const errorMsg = error.response?.data?.error || 
                       error.response?.data?.message ||
                       error.response?.data || 
                       'Error al cancelar la inscripción. Por favor, intenta nuevamente.';
      setErrorMessage(errorMsg);
      setOpenErrorDialog(true);
    } finally {
      setCancelando(false);
    }
  };

  const handleVerEvento = () => {
    if (onVerEvento) {
      onVerEvento();
    } else {
      navigate(`/evento/${evento.id}`);
    }
  };

  const handleEditarEvento = () => {
    navigate(`/editar-evento/${evento.id}`);
  };
  
  // Manejar error de carga de imagen - simplificado
  const handleImageError = (e) => {
    setImageError(true);
  };

  // Determinar qué imagen mostrar - simplificado
  const getImageSrc = () => {
    // Si hay error de carga, usar imagen por defecto
    if (imageError) {
      return noImagePlaceholder;
    }
    
    // Si no hay URL de imagen, usar imagen por defecto
    if (!evento.imagen || evento.imagen.trim() === "") {
      return noImagePlaceholder;
    }
    
    // Usar la imagen original
    return evento.imagen;
  };


  
  // Formatear fecha y hora
  let fecha = evento.fechaInicio;
  let fechaFormateada = '';
  try {
    fechaFormateada = format(new Date(fecha), "d 'de' MMM yyyy · HH:mm", { locale: es });
  } catch {
    fechaFormateada = fecha;
  }

  // Procesar tags del evento
  const tags = (evento.tags || []).map(tag => {
    if (!tag) return null;
    if (typeof tag === 'string') return tag;
    // El backend usa 'nombre' en Tag, el frontend en otros lugares puede usar 'name'
    return tag.nombre || tag.name || null;
  }).filter(tag => tag);

  // Estilos para los botones según tu paleta
  const sxVer = {
    border: '2px solid #8B5CF6',
    color: '#8B5CF6',
    backgroundColor: '#fff',
    fontWeight: 700,
    borderRadius: '10px',
    px: 2,
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
    '&:hover': {
      backgroundColor: '#FEF3C7',
      color: '#D97706',
      borderColor: '#D97706'
    }
  };

  const sxCancelarInscripcion = {
    border: '2px solid #DC2626',
    color: '#DC2626',
    backgroundColor: '#fff',
    fontWeight: 700,
    borderRadius: '10px',
    px: 2,
    '&:hover': {
      backgroundColor: '#FEE2E2',
      color: '#B91C1C',
      borderColor: '#B91C1C'
    }
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

  return (
    <Card
      sx={{
        width: 485,
        minHeight: 520,
        display: 'flex',
        flexDirection: 'column',
        borderRadius: 3,
        boxShadow: 2,
        p: 1,
        bgcolor: theme.palette.background.paper // <-- color de fondo de la card
      }}
    >
      <Box sx={{ position: 'relative' }}>
        <CardMedia
          component="img"
          height="170"
          image={getImageSrc()}
          alt={evento.titulo}
          onError={handleImageError}
          onLoad={(e) => {
          }}
          onLoadStart={() => {
          }}
          sx={{ 
            borderRadius: 2, 
            objectFit: 'cover',
            backgroundColor: '#f5f5f5' // Color de fondo mientras carga
          }}
        />
        <Box sx={{ position: 'absolute', top: 12, left: 12, display: 'flex', gap: 1 }}>
          <Chip
            label={evento.categoria}
            size="small"
            sx={{
              fontWeight: 500,
              bgcolor: theme.palette.secondary.main, // Fondo secundario
              color: theme.palette.secondary.contrastText
            }}
          />
        </Box>
        <Box sx={{ position: 'absolute', top: 12, right: 12, display: 'flex', gap: 1 }}>
          {evento.estado && (
            <Chip 
              label={getEstadoTraducido(evento.estado)} 
              size="small" 
              sx={{ 
                fontWeight: 600,
                ...getEstadoColors(evento.estado)
              }} 
            />
          )}
        </Box>
      </Box>
      <CardContent sx={{ flex: 1, display: 'flex', flexDirection: 'column', pb: 0 }}>
        <Typography gutterBottom variant="h6" component="div" sx={{ fontWeight: 600 }}>
          {evento.titulo}
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
          {evento.descripcion}
        </Typography>
        <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 0.5 }}>
          <CalendarMonthIcon fontSize="small" color="action" />
          <Typography variant="body2" color="text.secondary">
            {fechaFormateada}
          </Typography>
        </Stack>
        <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 0.5 }}>
          <PlaceIcon fontSize="small" color="action" />
          <Typography variant="body2" color="text.secondary">
            {evento.lugar}
          </Typography>
        </Stack>
        <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
          <PeopleIcon fontSize="small" color="action" />
          <Typography variant="body2" color="text.secondary">
            {evento.participantes_registrados} / {evento.max_participantes} asistentes
          </Typography>
        </Stack>
        {/* Tags del evento con scroll horizontal */}
        {tags.length > 0 && (
          <Box 
            sx={{ 
              mb: 1,
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
                  backgroundColor: theme.palette.grey[300],
                  borderRadius: '3px',
                  '&:hover': {
                    backgroundColor: theme.palette.grey[400],
                  }
                },
                '&::-webkit-scrollbar-thumb:active': {
                  backgroundColor: theme.palette.grey[500],
                }
              }}
            >
              {tags.map((tag, i) => (
                <Chip 
                  key={`${tag}-${i}`} 
                  label={tag.toLowerCase()} 
                  size="small" 
                  variant="outlined"
                  sx={{
                    fontSize: '0.8rem',
                    fontWeight: 600,
                    borderColor: theme.palette.grey[600],
                    color: theme.palette.grey[800],
                    backgroundColor: 'transparent',
                    minWidth: 'fit-content',
                    flexShrink: 0,
                    '&:hover': {
                      backgroundColor: theme.palette.grey[100],
                      borderColor: theme.palette.grey[800],
                      color: theme.palette.grey[900],
                      transform: 'translateY(-1px)',
                      boxShadow: `0 2px 4px ${theme.palette.grey[400]}40`
                    },
                    transition: 'all 0.2s ease-in-out'
                  }}
                />
              ))}
            </Box>
            {/* Indicador visual de que hay más contenido */}
            {tags.length > 4 && (
              <Box
                sx={{
                  position: 'absolute',
                  right: 0,
                  top: 0,
                  bottom: 8,
                  width: '20px',
                  background: `linear-gradient(to right, transparent, ${theme.palette.background.paper})`,
                  pointerEvents: 'none',
                  borderRadius: '0 4px 4px 0'
                }}
              />
            )}
          </Box>
        )}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Box />
          <Typography variant="h6" sx={{ fontWeight: 700 }}>
            ${evento.precio}
          </Typography>
        </Box>
        {/* Este Box empuja los botones hacia abajo */}
        <Box sx={{ flexGrow: 1 }} />
        <Stack direction="row" spacing={1} sx={{ pb: 2, pt: 0, mb: 0 }}>
          {(isAdmin || isOrganizador) && (
            <>
              <ButtonCustom
                variant="outlined"
                startIcon={<VisibilityIcon />}
                onClick={handleVerEvento}
                sx={sxVer}
              >
                Ver
              </ButtonCustom>
              <ButtonCustom
                variant="outlined"
                startIcon={<EditIcon />}
                onClick={handleEditarEvento}
                sx={sxEditar}
              >
                Editar
              </ButtonCustom>
            </>
          )}
          {isUser && (() => {
            const estadoRender = {
              isUser,
              estaInscrito,
              registrationId,
              userId: user?.id,
              userUsername: user?.username,
              eventoId: evento.id,
              eventoTitulo: evento.titulo
            };
            
            return (
              <>
                {!estaInscrito ? (
                <ButtonCustom
                  bgColor={theme.palette.primary.main}
                  color={theme.palette.primary.contrastText}
                  hoverBgColor={theme.palette.primary.dark}
                  hoverColor={theme.palette.primary.contrastText}
                  onClick={handleInscribirse}
                  startIcon={<PersonAddIcon />}
                  disabled={inscribiendose}
                  sx={{
                    opacity: inscribiendose ? 0.7 : 1
                  }}
                >
                  {inscribiendose ? 'Inscribiendo...' : 'Inscribirse'}
                </ButtonCustom>
              ) : (
                <ButtonCustom
                  variant="outlined"
                  startIcon={<PersonRemoveIcon />}
                  onClick={handleCancelarInscripcion}
                  disabled={cancelando}
                  sx={{
                    ...sxCancelarInscripcion,
                    opacity: cancelando ? 0.7 : 1
                  }}
                >
                  {cancelando ? 'Cancelando...' : 'Cancelar Inscripción'}
                </ButtonCustom>
              )}
                <ButtonCustom
                  variant="outlined"
                  startIcon={<VisibilityIcon />}
                  onClick={handleVerEvento}
                  sx={sxVer}
                >
                  Ver
                </ButtonCustom>
              </>
            );
          })()}
        </Stack>
      </CardContent>

      {/* Diálogo de confirmación para lista de espera */}
      <ConfirmDialog
        open={openWaitlistDialog}
        onClose={() => setOpenWaitlistDialog(false)}
        onConfirm={proceedWithRegistration}
        title="Evento Completo"
        message={`El evento ha alcanzado su capacidad máxima de ${evento?.max_participantes} participantes. Serás añadido a la <strong>lista de espera</strong> y podrás inscribirte si se libera un cupo.<br/><br/>¿Deseas continuar?`}
        confirmText="Confirmar"
        cancelText="Cancelar"
        loading={inscribiendose}
        loadingText="Confirmando..."
        type="warning"
      />

      {/* Diálogo de confirmación para inscripción normal */}
      <ConfirmDialog
        open={openInscripcionDialog}
        onClose={() => setOpenInscripcionDialog(false)}
        onConfirm={proceedWithRegistration}
        title="Confirmar Inscripción"
        message={`¿Estás seguro de que deseas inscribirte al evento <strong>"${evento?.titulo}"</strong>?`}
        confirmText="Inscribirse"
        cancelText="Cancelar"
        loading={inscribiendose}
        loadingText="Inscribiendo..."
        type="success"
      />

      {/* Diálogo de confirmación para cancelación */}
      <ConfirmDialog
        open={openCancelacionDialog}
        onClose={() => setOpenCancelacionDialog(false)}
        onConfirm={proceedWithCancellation}
        title="Cancelar Inscripción"
        message={`¿Estás seguro de que deseas cancelar tu inscripción al evento <strong>"${evento?.titulo}"</strong>?<br/><br/>Esta acción no se puede deshacer.`}
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
        message={errorMessage}
        confirmText="Entendido"
        cancelText=""
        type="error"
      />
    </Card>
  );
};
