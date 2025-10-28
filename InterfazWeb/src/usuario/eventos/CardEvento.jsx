import React, { useState, useEffect, useCallback } from 'react';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import IconButton from '@mui/material/IconButton';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import VisibilityIcon from '@mui/icons-material/Visibility';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import PlaceIcon from '@mui/icons-material/Place';
import PeopleIcon from '@mui/icons-material/People';
import { format } from 'date-fns';
import { es, tr } from 'date-fns/locale';
import { ButtonCustom } from '../../components/Button';
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

  // Debug: Log completo del evento al inicializar
  console.log(`🔍 EVENTO COMPLETO RECIBIDO - ${evento.titulo}:`, evento);
  console.log(`🔍 TAGS ESPECÍFICAMENTE:`, {
    tags: evento.tags,
    tagsType: typeof evento.tags,
    tagsIsArray: Array.isArray(evento.tags),
    tagsLength: evento.tags?.length,
    tagsJSON: JSON.stringify(evento.tags, null, 2)
  });

  // Función para verificar inscripción basada en la lógica de MisIncripciones
  const verificarInscripcion = useCallback(async () => {
    if (!user || !evento.id) {
      console.log(`🔍 No verificando inscripción - Usuario: ${!!user}, EventoID: ${evento.id}`);
      return;
    }
    
    console.log(`🔍 INICIANDO verificación para evento ${evento.id} (${evento.titulo})`);
    
    try {
      const registrations = await getUserRegistrations();
      console.log(`📋 TODAS las inscripciones del usuario (${registrations?.length || 0}):`, registrations);
      
      // IGUAL que MisIncripciones: Los datos están directamente en el registration
      const inscripcionActiva = registrations.find(reg => {
        // Los datos del evento están directamente en reg, no en reg.event
        const tieneEventoId = reg.eventId;
        const esEsteEvento = tieneEventoId && (reg.eventId == evento.id);
        const noEstaCancelada = reg.state !== 'CANCELED';
        
        console.log(`  🔍 Verificando inscripción ${reg.registrationId}:`, {
          tieneEventoId,
          esEsteEvento,
          noEstaCancelada,
          regEventId: reg.eventId,
          eventoId: evento.id,
          state: reg.state,
          MATCH: esEsteEvento && noEstaCancelada
        });
        
        return esEsteEvento && noEstaCancelada;
      });
      
      if (inscripcionActiva) {
        console.log(`✅ ENCONTRADA inscripción activa:`, inscripcionActiva);
        setEstaInscrito(true);
        setRegistrationId(inscripcionActiva.registrationId); // Usar registrationId como en MisIncripciones
      } else {
        console.log(`❌ NO hay inscripción activa para evento ${evento.id}`);
        setEstaInscrito(false);
        setRegistrationId(null);
      }
      
      console.log(`🎯 RESULTADO FINAL: estaInscrito=${inscripcionActiva ? true : false}`);
      
    } catch (error) {
      console.error('❌ Error al verificar inscripción:', error);
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
      console.log(`⚡ useEffect disparado para verificar inscripción: userId=${user.id}, eventoId=${evento.id}`);
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

  // Debug log para verificar la lógica
  console.log(`🔍 CardEvento - ${evento.titulo}:`, {
    'user.username': user?.username,
    'user.id': user?.id,
    'evento.organizador_id': evento.organizador_id,
    'isOrganizador': isOrganizador,
    'isAdmin': isAdmin,
    'isUser': isUser,
    'estaInscrito': estaInscrito,
    'registrationId': registrationId
  });

  // Funciones para manejar acciones
  const handleInscribirse = async () => {
    if (!user) {
      alert('Debes iniciar sesión para inscribirte');
      return;
    }
    
    setInscribiendose(true);
    try {
      const result = await registerToEvent(evento.id);
      alert('¡Te has inscrito exitosamente al evento!');
      console.log('Inscripción exitosa:', result);
      // Volver a verificar el estado de inscripción desde el servidor
      await verificarInscripcion();
    } catch (error) {
      console.error('Error al inscribirse:', error);
      const errorMessage = error.response?.data?.error || 
                          error.response?.data || 
                          'Error al inscribirse al evento';
      alert(`Error: ${errorMessage}`);
    } finally {
      setInscribiendose(false);
    }
  };

  const handleCancelarInscripcion = async () => {
    if (!user || !registrationId) {
      alert('No se puede cancelar la inscripción');
      return;
    }

    const confirmar = window.confirm('¿Estás seguro de que quieres cancelar tu inscripción a este evento?');
    if (!confirmar) return;
    
    setCancelando(true);
    try {
      await cancelRegistration(registrationId);
      alert('Has cancelado tu inscripción exitosamente');
      // Volver a verificar el estado de inscripción desde el servidor
      await verificarInscripcion();
    } catch (error) {
      console.error('Error al cancelar inscripción:', error);
      const errorMessage = error.response?.data?.error || 
                          error.response?.data || 
                          'Error al cancelar la inscripción';
      alert(`Error: ${errorMessage}`);
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

  const handleEliminarEvento = () => {
    if (window.confirm('¿Estás seguro de que quieres eliminar este evento?')) {
      // TODO: Implementar lógica de eliminación
      console.log('Eliminar evento:', evento.id);
    }
  };
  
  // Manejar error de carga de imagen - simplificado
  const handleImageError = (e) => {
    console.log(`❌ Error cargando imagen para ${evento.titulo}:`, evento.imagen);
    console.log('Error details:', e.type, e.target?.src);
    setImageError(true);
  };

  // Determinar qué imagen mostrar - simplificado
  const getImageSrc = () => {
    // Si hay error de carga, usar imagen por defecto
    if (imageError) {
      console.log(`❌ ${evento.titulo}: Usando fallback por error de carga`);
      return noImagePlaceholder;
    }
    
    // Si no hay URL de imagen, usar imagen por defecto
    if (!evento.imagen || evento.imagen.trim() === "") {
      console.log(`ℹ️ ${evento.titulo}: Sin imagen, usando fallback`);
      return noImagePlaceholder;
    }
    
    // Usar la imagen original
    console.log(`🖼️ ${evento.titulo}: Usando imagen ${evento.imagen}`);
    return evento.imagen;
  };

  // Log simplificado para debug
  if (evento.imagen) {
    console.log(`🖼️ CardEvento renderizando: ${evento.titulo} -> ${evento.imagen}`);
  } else {
    console.log(`⚪ CardEvento sin imagen: ${evento.titulo}`);
  }


  
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
  
  // Debug para tags - mostrar SIEMPRE el procesamiento
  console.log(`🏷️ Tags para ${evento.titulo}:`, {
    original: evento.tags,
    originalType: Array.isArray(evento.tags) ? 'array' : typeof evento.tags,
    originalLength: evento.tags?.length || 0,
    processed: tags,
    processedLength: tags.length,
    willShow: tags.length > 0,
    showScrollHint: tags.length > 4
  });

  // Debug adicional: examinar cada tag individual
  if (evento.tags && evento.tags.length > 0) {
    console.log(`🔍 Examinando cada tag individual para ${evento.titulo}:`);
    evento.tags.forEach((tag, index) => {
      console.log(`  Tag ${index}:`, {
        raw: tag,
        type: typeof tag,
        isString: typeof tag === 'string',
        hasNombre: tag && typeof tag === 'object' && 'nombre' in tag,
        hasName: tag && typeof tag === 'object' && 'name' in tag,
        nombre: tag?.nombre,
        name: tag?.name,
        final: typeof tag === 'string' ? tag : (tag?.nombre || tag?.name || null)
      });
    });
  }

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

  const sxEliminar = {
    border: '2px solid #DC2626',
    color: '#DC2626',
    backgroundColor: '#fff',
    borderRadius: '10px',
    '&:hover': {
      backgroundColor: '#FEE2E2',
      color: '#B91C1C',
      borderColor: '#B91C1C'
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
      case 'EVENT_PAUSED':
        return 'Pausado';
      default:
        // Si viene solo el estado sin el prefijo EVENT_, también lo manejamos
        switch (estadoUpper) {
          case 'OPEN':
            return 'Abierto';
          case 'CLOSED':
            return 'Cerrado';
          case 'PAUSED':
            return 'Pausado';
          default:
            return estado; // Devolver el estado original si no coincide
        }
    }
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
            console.log(`✅ Imagen cargada: ${evento.titulo}`);
            console.log(`   URL final: ${e.target.src}`);
            console.log(`   Dimensiones: ${e.target.naturalWidth}x${e.target.naturalHeight}`);
          }}
          onLoadStart={() => {
            console.log(`🔄 Iniciando carga: ${evento.titulo} -> ${getImageSrc()}`);
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
            <Chip label={getEstadoTraducido(evento.estado)} size="small" color="primary" sx={{ fontWeight: 500 }} />
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
              <IconButton
                size="small"
                onClick={handleEliminarEvento}
                sx={sxEliminar}
              >
                <DeleteIcon />
              </IconButton>
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
            console.log(`🎯 RENDERIZANDO botones para evento ${evento.id}:`, estadoRender);
            console.log(`🔴 ¿Mostrar botón CANCELAR? ${estaInscrito ? 'SÍ' : 'NO'}`);
            console.log(`🟢 ¿Mostrar botón INSCRIBIRSE? ${!estaInscrito ? 'SÍ' : 'NO'}`);
            
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
    </Card>
  );
};
