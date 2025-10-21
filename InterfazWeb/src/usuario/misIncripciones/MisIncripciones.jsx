import React, { useState, useEffect, useMemo } from "react";
import { Box, Typography, Card, CardContent, CardMedia, Stack, Chip, CircularProgress, Alert, Button } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import VisibilityIcon from "@mui/icons-material/Visibility";
import LogoutIcon from "@mui/icons-material/Logout";
import { ButtonCustom } from "../../components/Button";
import { NavbarApp } from "../../components/NavbarApp";
import { useNavigate } from "react-router-dom";
import { useGetEvents } from "../../hooks/useGetEvents";
import { useGetAuth } from "../../hooks/useGetAuth";
import noImagePlaceholder from "../../assets/images/no_image.png";

export const MisIncripciones = () => {
  const theme = useTheme();
  const navigate = useNavigate();
  const { loading, error, getUserRegistrations, cancelRegistration } = useGetEvents();
  const { user, isAuthenticated, loading: authLoading } = useGetAuth();
  
  const [registrations, setRegistrations] = useState([]);
  const [cancelandoId, setCancelandoId] = useState(null);

  // Debug de estados de autenticación
  console.log('🔍 MisIncripciones - Estados:', {
    isAuthenticated,
    authLoading,
    user: user?.username,
    hasToken: !!localStorage.getItem('authToken')
  });

  // Cargar inscripciones al montar el componente
  useEffect(() => {
    let isMounted = true;
    
    const loadRegistrations = async () => {
      // Esperar a que termine la carga de autenticación
      if (authLoading) {
        console.log('⏳ Esperando verificación de autenticación...');
        return;
      }
      
      // Verificar que el usuario esté autenticado
      if (!isAuthenticated || !user) {
        console.log('❌ Usuario no autenticado, no se pueden cargar inscripciones');
        return;
      }

      try {
        console.log('🔄 Cargando inscripciones para usuario:', user.username);
        
        // Verificar token antes de hacer la petición
        const token = localStorage.getItem('authToken');
        console.log('🔑 Token disponible:', !!token);
        console.log('🔑 Token (primeros 20 chars):', token?.substring(0, 20) + '...');
        
        const result = await getUserRegistrations();
        if (isMounted) {
          console.log('✅ Inscripciones cargadas:', result?.length || 0);
          console.log('📋 DATOS CRUDOS DE INSCRIPCIONES:', result);
          setRegistrations(result || []);
        }
      } catch (err) {
        if (isMounted) {
          console.error('❌ Error al cargar inscripciones:', err);
          console.error('❌ Detalles del error:', {
            status: err.response?.status,
            data: err.response?.data,
            message: err.message
          });
        }
      }
    };
    
    loadRegistrations();
    
    return () => {
      isMounted = false;
    };
  }, [isAuthenticated, user, authLoading, getUserRegistrations]);

  // Función para recargar inscripciones manualmente
  const handleReload = async () => {
    if (!isAuthenticated || !user) {
      console.log('❌ Usuario no autenticado para recargar');
      return;
    }

    try {
      console.log('🔄 Recargando inscripciones...');
      const result = await getUserRegistrations();
      setRegistrations(result || []);
      console.log('✅ Inscripciones recargadas');
    } catch (err) {
      console.error('Error al recargar inscripciones:', err);
    }
  };

  // Mapear inscripciones de API al formato esperado
  const inscripcionesFormateadas = useMemo(() => {
    console.log('🔄 MAPEANDO INSCRIPCIONES:', registrations);
    
    const mapped = (registrations || []).map((registration, index) => {
      console.log(`📌 PROCESANDO INSCRIPCIÓN ${index}:`, registration);
      
      if (!registration) {
        console.log(`❌ Inscripción ${index} es null/undefined`);
        return null;
      }
      
      // Los datos del evento están directamente en el registration, no en registration.event
      if (!registration.title || !registration.eventId) {
        console.log(`❌ Inscripción ${index} no tiene datos del evento:`, registration);
        return null;
      }
      
      console.log(`✅ Datos del evento encontrados para inscripción ${index}`);
      
      // Manejar categoria de forma segura
      let categoria = "Sin categoría";
      if (registration.category) {
        if (typeof registration.category === 'object' && registration.category.name) {
          categoria = registration.category.name;
        } else if (typeof registration.category === 'string') {
          categoria = registration.category;
        }
      }

      // Manejar estado de inscripción
      let estadoInscripcion = "Activa";
      if (registration.state) {
        if (typeof registration.state === 'string') {
          estadoInscripcion = registration.state;
        } else if (typeof registration.state === 'object' && registration.state.name) {
          estadoInscripcion = registration.state.name;
        }
      }

      const mappedItem = {
        id: registration.eventId, // Usar eventId en lugar de evento.id
        titulo: registration.title || "Sin título",
        descripcion: registration.description || "Sin descripción",
        fechaInicio: registration.startDateTime || new Date().toISOString(),
        lugar: registration.location || "Sin ubicación",
        categoria: categoria,
        imagen: registration.image || noImagePlaceholder,
        registrationId: registration.registrationId,
        estadoInscripcion: estadoInscripcion,
        fechaInscripcion: registration.dateTime // Usar dateTime en lugar de registrationDate
      };
      
      console.log(`🎯 ITEM MAPEADO ${index}:`, mappedItem);
      return mappedItem;
    }).filter(item => {
      const isValid = item !== null;
      console.log('🔍 FILTRO - Item válido:', isValid, item);
      return isValid;
    });
    
    console.log('📊 RESULTADO FINAL MAPEADO:', mapped);
    console.log('📊 CANTIDAD FINAL:', mapped.length);
    
    return mapped;
  }, [registrations]);

  const handleVer = (id) => {
    navigate(`/evento/${id}`);
  };

  const handleBaja = async (registrationId, eventoTitulo) => {
    if (!window.confirm(`¿Estás seguro de que quieres darte de baja del evento "${eventoTitulo}"?`)) {
      return;
    }
    
    setCancelandoId(registrationId);
    
    try {
      await cancelRegistration(registrationId);
      alert('Te has dado de baja del evento exitosamente');
      
      // Recargar inscripciones después de cancelar
      await handleReload();
    } catch (error) {
      console.error('Error al cancelar inscripción:', error);
      const errorMessage = error.response?.data?.error || 
                          error.response?.data || 
                          'Error al cancelar la inscripción';
      alert(`Error: ${errorMessage}`);
    } finally {
      setCancelandoId(null);
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
          Mis Inscripciones
        </Typography>

        {/* Verificación de autenticación */}
        {authLoading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
            <CircularProgress />
            <Typography sx={{ ml: 2 }}>Verificando autenticación...</Typography>
          </Box>
        ) : !isAuthenticated || !user ? (
          <Alert severity="warning" sx={{ mb: 3 }}>
            <Typography variant="h6" gutterBottom>
              Acceso restringido
            </Typography>
            <Typography>
              Debes iniciar sesión para ver tus inscripciones.
            </Typography>
            <Button 
              variant="contained" 
              sx={{ mt: 2 }}
              onClick={() => navigate('/login')}
            >
              Iniciar Sesión
            </Button>
          </Alert>
        ) : (
          <>
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
          <>
            {/* Debug logs */}
            {console.log('🖥️ RENDERIZANDO - Estado:', { loading, error, registrationsLength: registrations.length, formateadasLength: inscripcionesFormateadas.length })}
            
            {/* Mostrar cantidad de inscripciones */}
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Tienes {inscripcionesFormateadas.length} inscripción{inscripcionesFormateadas.length !== 1 ? 'es' : ''}
            </Typography>

            <Stack spacing={3}>
              {inscripcionesFormateadas.length === 0 ? (
                <>
                  <Typography variant="body1" color="text.secondary">
                    No estás inscripto en ningún evento.
                  </Typography>
                  {/* Debug info cuando está vacío */}
                  <Typography variant="caption" color="text.disabled" sx={{ mt: 2 }}>
                    Debug: registrations.length = {registrations.length}, 
                    formateadas.length = {inscripcionesFormateadas.length}
                  </Typography>
                </>
              ) : (
                inscripcionesFormateadas.map((inscripcion) => (
                  <Card
                    key={inscripcion.registrationId}
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
                      image={inscripcion.imagen}
                      alt={inscripcion.titulo}
                      sx={{
                        width: { xs: "100%", sm: 160 },
                        height: { xs: 160, sm: 140 },
                        objectFit: "cover"
                      }}
                    />
                    <CardContent sx={{ flex: 1, display: "flex", flexDirection: "column", justifyContent: "center" }}>
                      <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
                        <Chip
                          label={inscripcion.categoria}
                          size="small"
                          sx={{
                            fontWeight: 500,
                            bgcolor: theme.palette.secondary.main,
                            color: theme.palette.secondary.contrastText,
                          }}
                        />
                        <Chip
                          label={inscripcion.estadoInscripcion}
                          size="small"
                          color="primary"
                          sx={{ fontWeight: 500 }}
                        />
                      </Stack>
                      <Typography variant="h6" fontWeight={700} sx={{ mb: 0.5 }}>
                        {inscripcion.titulo}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                        {inscripcion.descripcion}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                        <b>Fecha del evento:</b> {new Date(inscripcion.fechaInicio).toLocaleString("es-AR", {
                          day: "numeric",
                          month: "short",
                          year: "numeric",
                          hour: "2-digit",
                          minute: "2-digit"
                        })}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                        <b>Ubicación:</b> {inscripcion.lugar}
                      </Typography>
                      {inscripcion.fechaInscripcion && (
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 1, fontSize: '0.8rem' }}>
                          <b>Inscripto el:</b> {new Date(inscripcion.fechaInscripcion).toLocaleDateString("es-AR")}
                        </Typography>
                      )}
                      <Stack direction="row" spacing={2} sx={{ mt: 1 }}>
                        <ButtonCustom
                          variant="outlined"
                          startIcon={<VisibilityIcon />}
                          sx={{
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
                          }}
                          onClick={() => handleVer(inscripcion.id)}
                        >
                          Ver
                        </ButtonCustom>
                        <ButtonCustom
                          variant="outlined"
                          startIcon={<LogoutIcon />}
                          disabled={cancelandoId === inscripcion.registrationId}
                          sx={{
                            border: '2px solid #DC2626',
                            color: '#DC2626',
                            backgroundColor: '#fff',
                            fontWeight: 700,
                            borderRadius: '10px',
                            px: 2,
                            minHeight: { xs: 34, md: 44 },
                            py: { xs: 0.5, md: 1 },
                            fontSize: { xs: 15, md: 16 },
                            opacity: cancelandoId === inscripcion.registrationId ? 0.7 : 1,
                            '&:hover': {
                              backgroundColor: '#FEE2E2',
                              color: '#B91C1C',
                              borderColor: '#B91C1C'
                            }
                          }}
                          onClick={() => handleBaja(inscripcion.registrationId, inscripcion.titulo)}
                        >
                          {cancelandoId === inscripcion.registrationId ? 'Cancelando...' : 'Darse de baja'}
                        </ButtonCustom>
                      </Stack>
                    </CardContent>
                  </Card>
                ))
              )}
            </Stack>
          </>
        )}
        </>
        )}
      </Box>
    </Box>
  );
};