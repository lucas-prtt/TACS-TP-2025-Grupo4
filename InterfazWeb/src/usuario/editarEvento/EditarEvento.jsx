import React, { useState, useEffect } from "react";
import { Box, Typography, Divider, Stack, Button, CircularProgress, Alert } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import { TextFieldCustom } from "../../components/TextField";
import { SelectorCustom } from "../../components/Selector";
import { NavbarApp } from "../../components/NavbarApp";
import { ButtonDate } from "../../components/ButtonDate";
import CalendarMonthOutlinedIcon from '@mui/icons-material/CalendarMonthOutlined';
import LocationOnOutlinedIcon from '@mui/icons-material/LocationOnOutlined';
import PeopleAltOutlinedIcon from '@mui/icons-material/PeopleAltOutlined';
import SaveIcon from '@mui/icons-material/Save';
import { ButtonTime } from "../../components/ButtonTime";
import { ButtonCustom } from "../../components/Button";
import { useNavigate, useParams } from "react-router-dom";
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useGetEvents } from "../../hooks/useGetEvents";

export const EditarEvento = () => {
  const theme = useTheme();
  const navigate = useNavigate();
  const { id } = useParams();
  const { getEventById, updateEvent, getCategories, loading, error: apiError } = useGetEvents();

  // Mapeo de estados técnicos a textos en español
  const estadosMap = {
    'EVENT_OPEN': 'Abierto',
    'EVENT_CLOSED': 'Cerrado',
    'EVENT_CANCELLED': 'Cancelado'
  };

  // Mapeo inverso: de español a técnico
  const estadosInversoMap = {
    'Abierto': 'EVENT_OPEN',
    'Cerrado': 'EVENT_CLOSED',
    'Cancelado': 'EVENT_CANCELLED'
  };

  // Estado del formulario siguiendo la estructura de la API
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    startDateTime: null,
    durationMinutes: '',
    location: '',
    maxParticipants: '',
    price: '',
    category: '',
    tags: '',
    imageUrl: '',
    state: ''
  });

  const [fecha, setFecha] = useState(null);
  const [horaInicio, setHoraInicio] = useState(null);
  const [localError, setLocalError] = useState('');
  const [success, setSuccess] = useState('');
  const [loadingEvent, setLoadingEvent] = useState(true);
  const [categorias, setCategorias] = useState([]);
  const [estadoVisual, setEstadoVisual] = useState(''); // Estado en español para el selector

  // Cargar categorías al montar el componente
  useEffect(() => {
    const loadCategories = async () => {
      try {
        const result = await getCategories();
        if (result) {
          // Ordenar categorías alfabéticamente
          const categoriasOrdenadas = result
            .map(cat => cat.title || cat)
            .sort((a, b) => a.localeCompare(b));
          setCategorias(categoriasOrdenadas);
        }
      } catch (err) {
        console.error('Error al cargar categorías:', err);
      }
    };
    
    loadCategories();
  }, [getCategories]);

  // Cargar evento al montar el componente
  useEffect(() => {
    const loadEvent = async () => {
      if (!id) {
        setLocalError('ID de evento no encontrado');
        setLoadingEvent(false);
        return;
      }

      try {
        setLoadingEvent(true);
        const eventData = await getEventById(id);
        
        // Mapear los datos del evento al estado del formulario
        // Extraer el título de la categoría si es un objeto
        const categoryValue = eventData.category 
          ? (typeof eventData.category === 'object' ? eventData.category.title : eventData.category)
          : '';
        
        // Extraer los nombres de los tags si son objetos
        const tagsValue = eventData.tags 
          ? eventData.tags.map(tag => typeof tag === 'object' ? (tag.nombre || tag.name) : tag).join(', ')
          : '';
        
        const estadoTecnico = eventData.state || 'EVENT_OPEN';
        
        setFormData({
          title: eventData.title || '',
          description: eventData.description || '',
          startDateTime: eventData.startDateTime,
          durationMinutes: eventData.durationMinutes?.toString() || '',
          location: eventData.location || '',
          maxParticipants: eventData.maxParticipants?.toString() || '',
          price: eventData.price?.toString() || '',
          category: categoryValue,
          tags: tagsValue,
          imageUrl: eventData.image || '',
          state: estadoTecnico
        });

        // Configurar estado visual en español
        setEstadoVisual(estadosMap[estadoTecnico] || 'Abierto');

        // Configurar fecha y hora para los componentes de UI
        if (eventData.startDateTime) {
          const startDate = new Date(eventData.startDateTime);
          setFecha(startDate);
          setHoraInicio(startDate);
          
          // Calcular hora de fin basada en la duración
          if (eventData.durationMinutes) {
            const endDate = new Date(startDate.getTime() + eventData.durationMinutes * 60000);
            // No necesitamos horaFin ya que se calcula automáticamente
          }
        }

        setLoadingEvent(false);
      } catch (err) {
        console.error('Error al cargar el evento:', err);
        setLocalError('Error al cargar los datos del evento');
        setLoadingEvent(false);
      }
    };

    loadEvent();
  }, [id, getEventById]);

  // Manejar cambios en los campos
  const handleChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    // Limpiar errores al modificar
    if (localError) setLocalError('');
    if (success) setSuccess('');
  };

  // Manejar cambio de estado (convertir de español a técnico)
  const handleEstadoChange = (estadoEnEspañol) => {
    setEstadoVisual(estadoEnEspañol);
    const estadoTecnico = estadosInversoMap[estadoEnEspañol];
    setFormData(prev => ({
      ...prev,
      state: estadoTecnico
    }));
    // Limpiar errores al modificar
    if (localError) setLocalError('');
    if (success) setSuccess('');
  };

  // Combinar fecha y hora en LocalDateTime
  const combineDateTime = (date, time) => {
    if (!date || !time) return null;
    
    // Asegurarse de que time sea un objeto Date válido
    let timeObj = time;
    if (!(time instanceof Date)) {
      // Si time es un string, intentar convertirlo a Date
      timeObj = new Date(time);
    }
    
    // Validar que timeObj es una fecha válida
    if (isNaN(timeObj.getTime())) {
      console.error('Hora inválida:', time);
      return null;
    }
    
    const combined = new Date(date);
    combined.setHours(timeObj.getHours());
    combined.setMinutes(timeObj.getMinutes());
    combined.setSeconds(0);
    combined.setMilliseconds(0);
    
    // Formato ISO string para el backend
    return combined.toISOString().slice(0, 19);
  };

  // Validar formulario
  const validateForm = () => {
    if (!formData.title.trim()) {
      setLocalError('El título es obligatorio');
      return false;
    }
    if (!formData.description.trim()) {
      setLocalError('La descripción es obligatoria');
      return false;
    }
    if (!fecha) {
      setLocalError('La fecha es obligatoria');
      return false;
    }
    if (!horaInicio) {
      setLocalError('La hora de inicio es obligatoria');
      return false;
    }
    if (!formData.durationMinutes || parseInt(formData.durationMinutes) <= 0) {
      setLocalError('La duración debe ser mayor a 0 minutos');
      return false;
    }
    if (!formData.location.trim()) {
      setLocalError('La ubicación es obligatoria');
      return false;
    }
    if (!formData.category) {
      setLocalError('La categoría es obligatoria');
      return false;
    }
    if (!formData.tags.trim()) {
      setLocalError('Debes agregar al menos una etiqueta');
      return false;
    }
    if (!formData.maxParticipants || parseInt(formData.maxParticipants) <= 0) {
      setLocalError('El número máximo de participantes debe ser mayor a 0');
      return false;
    }
    if (formData.price === '' || parseFloat(formData.price) < 0) {
      setLocalError('El precio debe ser mayor o igual a 0');
      return false;
    }

    return true;
  };

  // Manejar actualización del evento
  const handleGuardar = async () => {
    setLocalError('');
    setSuccess('');

    if (!validateForm()) {
      return;
    }

    const startDateTime = combineDateTime(fecha, horaInicio);
    if (!startDateTime) {
      setLocalError('Error al combinar fecha y hora');
      return;
    }

    // Preparar datos según el formato que espera el backend (EventDTO)
    const eventData = {
      title: formData.title.trim(),
      description: formData.description.trim(),
      startDateTime: startDateTime,
      durationMinutes: parseInt(formData.durationMinutes) || 60,
      location: formData.location.trim(),
      maxParticipants: parseInt(formData.maxParticipants),
      price: parseFloat(formData.price),
      // Enviar category como objeto si hay valor, null si no
      category: formData.category ? { title: formData.category } : null,
      // Enviar tags como array de objetos Tag
      tags: formData.tags 
        ? formData.tags.split(',').map(tag => ({ nombre: tag.trim() })).filter(tag => tag.nombre)
        : [],
      // Enviar imagen
      image: formData.imageUrl.trim() || null,
      // Enviar estado
      state: formData.state
    };

    try {
      await updateEvent(id, eventData);
      setSuccess('Evento actualizado exitosamente');
      
      // Navegar después de un breve delay para mostrar el mensaje
      setTimeout(() => {
        navigate('/mis-eventos');
      }, 2000);
    } catch (err) {
      console.error('Error al actualizar el evento:', err);
      // El error ya se maneja en el hook
    }
  };

  return (
    <Box minHeight="100vh" sx={{ display: 'flex', flexDirection: 'row', bgcolor: theme.palette.background.primary }}>
      <Box sx={{ width: '250px', flexShrink: 0, display: { xs: 'none', md: 'block' } }}>
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
          {/* Botón Volver */}
          <Button
            startIcon={<ArrowBackIcon />}
            sx={{ mb: 2, textTransform: "none", alignSelf: "flex-start" }}
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
          ) : localError && !formData.title ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 400 }}>
              <Alert severity="error">{localError}</Alert>
            </Box>
          ) : (
            <>
              {/* Alertas de error y éxito */}
              {(localError || apiError) && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {localError || apiError}
                </Alert>
              )}
              {success && (
                <Alert severity="success" sx={{ mb: 2 }}>
                  {success}
                </Alert>
              )}

              <Typography variant="h5" fontWeight={700} mb={0.5}>
                Editar Evento
              </Typography>
              <Typography variant="body1" color="text.secondary" mb={3}>
                Modificá los datos del evento y guardá los cambios
              </Typography>
          <Box sx={{
            display: 'flex',
            flexDirection: { xs: 'column', md: 'row' },
            alignItems: 'stretch',
            gap: 2,
          }}>
            {/* Columna izquierda */}
            <Box sx={{
              flex: 2,
              minWidth: 0,
              display: 'flex',
              flexDirection: 'column',
            }}>
              <Box
                sx={{
                  bgcolor: theme.palette.background.primary,
                  borderRadius: 3,
                  boxShadow: '0 2px 8px rgba(0,0,0,0.07)',
                  p: 3,
                  border: '1px solid #e0e0e0',
                  alignSelf: 'stretch',
                  height: '100%',
                  display: 'flex',
                  flexDirection: 'column',
                }}
              >
                <Typography variant="h6" fontWeight={600} mb={3}>
                  Información General
                </Typography>
                <Box mb={2}>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Título del Evento *
                  </Typography>
                  <TextFieldCustom
                    value={formData.title}
                    onChange={e => handleChange('title', e.target.value)}
                    placeholder="Ej: Conferencia de Tecnología 2025"
                  />
                </Box>
                <Box mb={2}>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Descripción *
                  </Typography>
                  <TextFieldCustom
                    value={formData.description}
                    onChange={e => handleChange('description', e.target.value)}
                    placeholder="Describe tu evento..."
                    multiline
                    minRows={3}
                    maxRows={3}
                  />
                </Box>
                <Box
                  mb={2}
                  sx={{
                    display: 'flex',
                    gap: 2,
                    flexDirection: { xs: 'column', sm: 'row' },
                  }}
                >
                  <Box sx={{ flex: 1, width: { xs: '100%', sm: 'auto' } }}>
                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                      Categoría *
                    </Typography>
                    <SelectorCustom
                      placeholder="Selecciona una categoría"
                      opciones={categorias}
                      value={formData.category}
                      onChange={e => handleChange('category', e.target.value)}
                      fullWidth
                    />
                  </Box>
                  <Box sx={{ flex: 1, width: { xs: '100%', sm: 'auto' } }}>
                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                      Duración (minutos) *
                    </Typography>
                    <TextFieldCustom
                      value={formData.durationMinutes}
                      onChange={e => handleChange('durationMinutes', e.target.value)}
                      placeholder="60"
                      onlyNumbers={true}
                      fullWidth
                    />
                  </Box>
                </Box>
                {/* Estado del evento */}
                <Box mb={2}>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Estado del Evento *
                  </Typography>
                  <SelectorCustom
                    placeholder="Selecciona el estado"
                    opciones={['Abierto', 'Cerrado', 'Cancelado']}
                    value={estadoVisual}
                    onChange={e => handleEstadoChange(e.target.value)}
                    fullWidth
                  />
                </Box>
                {/* URL de imagen */}
                <Box mb={2}>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    URL de Imagen (opcional)
                  </Typography>
                  <TextFieldCustom
                    value={formData.imageUrl}
                    onChange={e => handleChange('imageUrl', e.target.value)}
                    placeholder="https://ejemplo.com/imagen.jpg"
                  />
                </Box>
                {/* Etiquetas */}
                <Box>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Etiquetas *
                  </Typography>
                  <TextFieldCustom
                    value={formData.tags}
                    onChange={e => handleChange('tags', e.target.value)}
                    placeholder="Agregar etiqueta... (separadas por coma)"
                  />
                </Box>
              </Box>
            </Box>
            {/* Columna derecha */}
            <Box sx={{
              flex: 1,
              minWidth: 220,
              display: 'flex',
              flexDirection: 'column',
              gap: 2,
            }}>
              {/* Fecha y Hora */}
              <Box
                sx={{
                  bgcolor: theme.palette.background.primary,
                  borderRadius: 3,
                  boxShadow: '0 2px 8px rgba(0,0,0,0.07)',
                  p: 2,
                  border: '1px solid #eee'
                }}
              >
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2, gap: 1 }}>
                  <Typography variant="subtitle1" fontWeight={600}>
                    Fecha y Hora
                  </Typography>
                  <CalendarMonthOutlinedIcon sx={{ color: theme.palette.text.primary, fontSize: 24 }} />
                </Box>
                <Box mb={1}>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Fecha *
                  </Typography>
                  <ButtonDate
                    value={fecha}
                    onChange={setFecha}
                  />
                </Box>
                <Box mb={1}>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Hora de Inicio *
                  </Typography>
                  <ButtonTime
                    placeholder="Inicio *"
                    value={horaInicio}
                    onChange={setHoraInicio}
                    fullWidth
                  />
                </Box>
              </Box>
              {/* Ubicación */}
              <Box
                sx={{
                  bgcolor: theme.palette.background.primary,
                  borderRadius: 3,
                  boxShadow: '0 2px 8px rgba(0,0,0,0.07)',
                  p: 2,
                  border: '1px solid #eee'
                }}
              >
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2, gap: 1 }}>
                  <Typography variant="subtitle1" fontWeight={600}>
                    Ubicación
                  </Typography>
                  <LocationOnOutlinedIcon sx={{ color: theme.palette.text.primary, fontSize: 24 }} />
                </Box>
                <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                  Lugar *
                </Typography>
                <TextFieldCustom
                  value={formData.location}
                  onChange={e => handleChange('location', e.target.value)}
                  placeholder="Ej: Centro de Convenciones"
                  fullWidth
                />
              </Box>
              {/* Detalles Adicionales */}
              <Box
                sx={{
                  bgcolor: theme.palette.background.primary,
                  borderRadius: 3,
                  boxShadow: '0 2px 8px rgba(0,0,0,0.07)',
                  p: 2,
                  border: '1px solid #eee'
                }}
              >
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2, gap: 1 }}>
                  <Typography variant="subtitle1" fontWeight={600}>
                    Detalles Adicionales
                  </Typography>
                  <PeopleAltOutlinedIcon sx={{ color: theme.palette.text.primary, fontSize: 24 }} />
                </Box>
                <Box mb={1}>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Capacidad máxima *
                  </Typography>
                  <TextFieldCustom
                    value={formData.maxParticipants}
                    onChange={e => handleChange('maxParticipants', e.target.value)}
                    placeholder="Número máximo de participantes"
                    fullWidth
                    onlyNumbers={true}
                  />
                </Box>
                <Box>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Precio *
                  </Typography>
                  <TextFieldCustom
                    value={formData.price}
                    onChange={e => handleChange('price', e.target.value)}
                    placeholder="$ 0,00"
                    fullWidth
                    onlyNumbers={true}
                    allowFloat={true}
                  />
                </Box>
              </Box>
            </Box>
          </Box>
              {/* Botón Guardar Cambios */}
              <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                <ButtonCustom
                  bgColor={theme.palette.primary.main}
                  color={theme.palette.primary.contrastText}
                  hoverBgColor={theme.palette.primary.dark}
                  hoverColor={theme.palette.primary.contrastText}
                  startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <SaveIcon />}
                  sx={{
                    minWidth: 220,
                    fontSize: 17,
                    fontWeight: 700,
                    borderRadius: 2,
                    py: 1.5,
                    px: 4,
                    boxShadow: 'none',
                    textTransform: 'none'
                  }}
                  onClick={handleGuardar}
                  disabled={loading}
                >
                  {loading ? 'Guardando...' : 'Guardar Cambios'}
                </ButtonCustom>
              </Box>
            </>
          )}
        </Box>
      </Box>
    </Box>
  );
};