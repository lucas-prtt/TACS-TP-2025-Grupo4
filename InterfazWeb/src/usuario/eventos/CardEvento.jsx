import React, { useState, useEffect } from 'react';
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
import noImagePlaceholder from '../../assets/images/no_image.png';
import { useAuth } from '../../contexts/AuthContext';
import { useGetEvents } from '../../hooks/useGetEvents';

export const CardEvento = ({ evento, onVerEvento }) => {
  const theme = useTheme();
  const navigate = useNavigate();
  const { user } = useAuth();
  const { registerToEvent, loading: eventLoading } = useGetEvents();
  const [imageError, setImageError] = useState(false);
  const [inscribiendose, setInscribiendose] = useState(false);

  // Resetear estado de error cuando cambia la imagen del evento
  useEffect(() => {
    setImageError(false);
  }, [evento.imagen]);

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
    'isUser': isUser
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

  // Mostrar hasta 2 etiquetas y un chip "+1" si hay más
  const tags = evento.tags || [];
  const maxChips = 2;
  const extraTags = tags.length - maxChips;

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

  return (
    <Card
      sx={{
        width: 410,
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
            <Chip label={evento.estado} size="small" color="primary" sx={{ fontWeight: 500 }} />
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
            {evento.min_participantes} / {evento.max_participantes} asistentes
          </Typography>
        </Stack>
        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mb: 1 }}>
          {tags.slice(0, maxChips).map((tag, i) => (
            <Chip key={tag} label={tag.toLowerCase()} size="small" variant="outlined" />
          ))}
          {extraTags > 0 && (
            <Chip label={`+${extraTags}`} size="small" variant="outlined" />
          )}
        </Box>
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
          {isUser && (
            <>
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
              <ButtonCustom
                variant="outlined"
                startIcon={<VisibilityIcon />}
                onClick={handleVerEvento}
                sx={sxVer}
              >
                Ver
              </ButtonCustom>
            </>
          )}
        </Stack>
      </CardContent>
    </Card>
  );
};
