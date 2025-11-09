import { Box, Typography, Alert } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { TextFieldCustom } from "../../components/TextField";
import { SelectorCustom } from "../../components/Selector";
import { NavbarApp } from '../../components/NavbarApp';
import { ButtonDate } from '../../components/ButtonDate';
import CalendarMonthOutlinedIcon from '@mui/icons-material/CalendarMonthOutlined';
import LocationOnOutlinedIcon from '@mui/icons-material/LocationOnOutlined';
import PeopleAltOutlinedIcon from '@mui/icons-material/PeopleAltOutlined';
import { ButtonTime } from '../../components/ButtonTime';
import { useState, useEffect } from 'react';
import SaveIcon from '@mui/icons-material/Save';
import { ButtonCustom } from '../../components/Button';
import { useGetEvents } from '../../hooks/useGetEvents';
import { useNavigate } from 'react-router-dom';

export const CrearEventos = () => {
    const theme = useTheme();
    const navigate = useNavigate();
    const { createEvent, getCategories, loading, error: apiError } = useGetEvents();

    // Estado del formulario
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        startDateTime: null,
        durationMinutes: '',
        location: '',
        maxParticipants: '',
        minParticipants: '',
        price: '',
        category: '',
        tags: '',
        imageUrl: ''
    });

    const [fecha, setFecha] = useState(null);
    const [horaInicio, setHoraInicio] = useState(null);
    const [localError, setLocalError] = useState('');
    const [success, setSuccess] = useState('');
    const [categories, setCategories] = useState([]);

    // Cargar categorías al montar el componente
    useEffect(() => {
        const loadCategories = async () => {
            try {
                const categoriesData = await getCategories();
                // Las categorías vienen con la estructura { title: "..." }
                setCategories(categoriesData.map(cat => cat.title));
            } catch (error) {
                console.error('Error al cargar categorías:', error);
                // Si falla, usar categorías por defecto
                setCategories(["Tecnología", "Música", "Deporte", "Arte", "Gastronomía", "Educación", "Negocios"]);
            }
        };
        loadCategories();
    }, [getCategories]);

    // Manejar cambios en los campos
    const handleChange = (field, value) => {
        setFormData(prev => ({
            ...prev,
            [field]: value
        }));
        setLocalError('');
        setSuccess('');
    };

    // Combinar fecha y hora en LocalDateTime
    const combineDateTime = (date, time) => {
        if (!date || !time) return null;
        
        const dateObj = new Date(date);
        const timeObj = new Date(time);
        
        dateObj.setHours(timeObj.getHours());
        dateObj.setMinutes(timeObj.getMinutes());
        dateObj.setSeconds(0);
        dateObj.setMilliseconds(0);
        
        return dateObj.toISOString();
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
        if (!fecha || !horaInicio) {
            setLocalError('La fecha y hora de inicio son obligatorias');
            return false;
        }
        if (!formData.durationMinutes || formData.durationMinutes <= 0) {
            setLocalError('La duración debe ser mayor a 0');
            return false;
        }
        if (!formData.location.trim()) {
            setLocalError('La ubicación es obligatoria');
            return false;
        }
        if (!formData.maxParticipants || formData.maxParticipants <= 0) {
            setLocalError('La capacidad máxima debe ser mayor a 0');
            return false;
        }
        // minParticipants es opcional, pero si se proporciona debe ser >= 1
        if (formData.minParticipants !== '' && (formData.minParticipants < 1)) {
            setLocalError('La capacidad mínima debe ser 1 o mayor');
            return false;
        }
        // Solo validar que minParticipants no sea mayor a maxParticipants si se proporciona
        if (formData.minParticipants !== '' && parseInt(formData.minParticipants) > parseInt(formData.maxParticipants)) {
            setLocalError('La capacidad mínima no puede ser mayor a la máxima');
            return false;
        }
        if (formData.price === '' || formData.price < 0) {
            setLocalError('El precio debe ser mayor o igual a 0');
            return false;
        }
        return true;
    };

    // Manejar creación del evento
    const handleCreateEvent = async () => {
        setLocalError('');
        setSuccess('');

        if (!validateForm()) {
            return;
        }

        try {
            const startDateTime = combineDateTime(fecha, horaInicio);
            
            // Procesar tags -> enviar como { nombre: '...' } para que el backend los mapee a org.model.events.Tag
            const processedTags = formData.tags ? formData.tags.split(',').map(tag => ({ nombre: tag.trim() })).filter(tag => tag.nombre) : [];
            
            const eventData = {
                title: formData.title.trim(),
                description: formData.description.trim(),
                startDateTime: startDateTime,
                durationMinutes: parseInt(formData.durationMinutes),
                location: formData.location.trim(),
                maxParticipants: parseInt(formData.maxParticipants),
                minParticipants: formData.minParticipants !== '' ? parseInt(formData.minParticipants) : null,
                price: parseFloat(formData.price),
                category: formData.category ? { title: formData.category } : null,
                tags: processedTags,
                image: formData.imageUrl.trim() || null
            };

            console.log('🏷️ Tags originales:', formData.tags);
            console.log('🏷️ Tags procesadas (enviadas al backend):', processedTags);
            console.log('🚀 Enviando datos del evento al backend:', eventData);
            await createEvent(eventData);
            setSuccess('¡Evento creado exitosamente!');
            
            setTimeout(() => {
                navigate('/mis-eventos');
            }, 1500);
        } catch (err) {
            const errorMessage = err.response?.data?.error 
                || apiError 
                || 'Error al crear el evento';
            setLocalError(errorMessage);
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
                    {/* Título principal */}
                    <Typography variant="h5" fontWeight={700} mb={0.5}>
                        Crear Nuevo Evento
                    </Typography>
                    <Typography variant="body1" color="text.secondary" mb={3}>
                        Completa la información para crear un nuevo evento
                    </Typography>

                    {/* Mensajes de error y éxito */}
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

                    {/* Layout principal */}
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

                                {/* Título */}
                                <Box mb={2}>
                                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                                        Título del Evento *
                                    </Typography>
                                    <TextFieldCustom
                                        placeholder="Ej: Conferencia de Tecnología 2025"
                                        value={formData.title}
                                        onChange={(e) => handleChange('title', e.target.value)}
                                    />
                                </Box>

                                {/* Descripción */}
                                <Box mb={2}>
                                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                                        Descripción *
                                    </Typography>
                                    <TextFieldCustom
                                        placeholder="Describe tu evento..."
                                        multiline
                                        minRows={3}
                                        maxRows={3}
                                        value={formData.description}
                                        onChange={(e) => handleChange('description', e.target.value)}
                                    />
                                </Box>

                                {/* Categoría */}
                                <Box mb={2}>
                                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                                        Categoría (Opcional)
                                    </Typography>
                                    <SelectorCustom
                                        placeholder="Selecciona una categoría"
                                        opciones={categories}
                                        value={formData.category}
                                        onChange={(e) => handleChange('category', e.target.value)}
                                        fullWidth
                                    />
                                </Box>

                                {/* Etiquetas */}
                                <Box mb={2}>
                                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                                        Etiquetas (Opcional)
                                    </Typography>
                                    <TextFieldCustom
                                        placeholder="Ej: tecnología, innovación, startup"
                                        value={formData.tags}
                                        onChange={(e) => handleChange('tags', e.target.value)}
                                        helperText="Separar múltiples etiquetas con comas"
                                    />
                                </Box>

                                {/* URL de imagen */}
                                <Box>
                                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                                        URL de Imagen (Opcional)
                                    </Typography>
                                    <TextFieldCustom
                                        placeholder="https://ejemplo.com/imagen.jpg"
                                        value={formData.imageUrl}
                                        onChange={(e) => handleChange('imageUrl', e.target.value)}
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

                                {/* Fecha */}
                                <Box mb={1}>
                                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                                        Fecha *
                                    </Typography>
                                    <ButtonDate
                                        value={fecha}
                                        onChange={setFecha}
                                    />
                                </Box>

                                {/* Hora de inicio */}
                                <Box mb={1}>
                                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                                        Hora de Inicio *
                                    </Typography>
                                    <ButtonTime
                                        placeholder="Hora de inicio"
                                        value={horaInicio}
                                        onChange={setHoraInicio}
                                        fullWidth
                                    />
                                </Box>

                                {/* Duración */}
                                <Box>
                                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                                        Duración (minutos) *
                                    </Typography>
                                    <TextFieldCustom
                                        placeholder="Ej: 120"
                                        fullWidth
                                        onlyNumbers={true}
                                        value={formData.durationMinutes}
                                        onChange={(e) => handleChange('durationMinutes', e.target.value)}
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
                                    placeholder="Ej: Centro de Convenciones"
                                    fullWidth
                                    value={formData.location}
                                    onChange={(e) => handleChange('location', e.target.value)}
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

                                {/* Capacidad mínima */}
                                <Box mb={1}>
                                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                                        Capacidad Mínima (Opcional)
                                    </Typography>
                                    <TextFieldCustom
                                        placeholder="Número mínimo de participantes"
                                        fullWidth
                                        onlyNumbers={true}
                                        value={formData.minParticipants}
                                        onChange={(e) => handleChange('minParticipants', e.target.value)}
                                    />
                                </Box>

                                {/* Capacidad máxima */}
                                <Box mb={1}>
                                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                                        Capacidad Máxima *
                                    </Typography>
                                    <TextFieldCustom
                                        placeholder="Número máximo de participantes"
                                        fullWidth
                                        onlyNumbers={true}
                                        value={formData.maxParticipants}
                                        onChange={(e) => handleChange('maxParticipants', e.target.value)}
                                    />
                                </Box>

                                {/* Precio */}
                                <Box>
                                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                                        Precio *
                                    </Typography>
                                    <TextFieldCustom
                                        placeholder="$ 0.00"
                                        fullWidth
                                        onlyNumbers={true}
                                        allowFloat={true}
                                        value={formData.price}
                                        onChange={(e) => handleChange('price', e.target.value)}
                                    />
                                </Box>
                            </Box>
                        </Box>
                    </Box>

                    {/* Botón Crear Evento */}
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                        <ButtonCustom
                            bgColor={theme.palette.primary.main}
                            color={theme.palette.primary.contrastText}
                            hoverBgColor={theme.palette.primary.dark}
                            hoverColor={theme.palette.primary.contrastText}
                            startIcon={<SaveIcon />}
                            disabled={loading}
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
                            onClick={handleCreateEvent}
                        >
                            {loading ? 'Creando...' : 'Crear Evento'}
                        </ButtonCustom>
                    </Box>
                </Box>
            </Box>
        </Box>
    );
};

