import React, { useState, useEffect, useMemo } from "react";
import { Box, Typography, Card, CardContent, CardMedia, Stack, Chip, CircularProgress, Alert, Button } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import VisibilityIcon from "@mui/icons-material/Visibility";
import LogoutIcon from "@mui/icons-material/Logout";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CancelIcon from "@mui/icons-material/Cancel";
import AccessTimeIcon from "@mui/icons-material/AccessTime";
import { ButtonCustom } from "../../components/Button";
import { ConfirmDialog } from "../../components/ConfirmDialog";
import { NavbarApp } from "../../components/NavbarApp";
import { useNavigate } from "react-router-dom";
import { useGetEvents } from "../../hooks/useGetEvents";
import { useGetAuth } from "../../hooks/useGetAuth";
import noImagePlaceholder from "../../assets/images/no_image.png";

export const MisIncripciones = () => {
  const theme = useTheme();
  const navigate = useNavigate();
  const { loading, error, getUserRegistrations, cancelRegistration, getEventById } = useGetEvents();
  const { user, isAuthenticated, loading: authLoading } = useGetAuth();
  
  const [registrations, setRegistrations] = useState([]);
  const [eventosCompletos, setEventosCompletos] = useState({});
  const [cancelandoId, setCancelandoId] = useState(null);
  
  // Estados para los diálogos
  const [openCancelacionDialog, setOpenCancelacionDialog] = useState(false);
  const [openErrorDialog, setOpenErrorDialog] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [inscripcionACancelar, setInscripcionACancelar] = useState(null);

  // Debug de estados de autenticación
  

  // Cargar inscripciones al montar el componente
  useEffect(() => {
    let isMounted = true;
    
    const loadRegistrations = async () => {
      // Esperar a que termine la carga de autenticación
      if (authLoading) {
        
        return;
      }
      
      // Verificar que el usuario esté autenticado
      if (!isAuthenticated || !user) {
        
        return;
      }

      try {
        
        const result = await getUserRegistrations();
        if (isMounted) {
          
          setRegistrations(result || []);
          
          // Cargar datos completos de cada evento
          if (result && result.length > 0) {
            const eventosMap = {};
            for (const registration of result) {
              if (registration.eventId && !eventosMap[registration.eventId]) {
                try {
                  const evento = await getEventById(registration.eventId);
                  if (evento) {
                    eventosMap[registration.eventId] = evento;
                  }
                } catch (err) {
                
                }
              }
            }
            if (isMounted) {
              setEventosCompletos(eventosMap);
            }
          }
        }
      } catch (err) {
        if (isMounted) {
         
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
     
      return;
    }

    try {
    
      const result = await getUserRegistrations();
      setRegistrations(result || []);
      
    } catch (err) {
     
    }
  };

  // Mapear inscripciones combinando datos del RegistrationDTO con datos completos del evento
  const inscripcionesFormateadas = useMemo(() => {
    const mapped = (registrations || []).map((registration) => {
      if (!registration || !registration.eventId) {
        return null;
      }
      
      // Obtener datos completos del evento si están disponibles
      const eventoCompleto = eventosCompletos[registration.eventId];
      
      // Manejar estado de inscripción
      let estadoInscripcion = "Activa";
      if (registration.state) {
        if (typeof registration.state === 'string') {
          estadoInscripcion = registration.state;
        } else if (typeof registration.state === 'object' && registration.state.name) {
          estadoInscripcion = registration.state.name;
        }
      }

      // Manejar categoría
      let categoria = "Sin categoría";
      if (eventoCompleto?.category) {
        if (typeof eventoCompleto.category === 'object' && eventoCompleto.category.title) {
          categoria = eventoCompleto.category.title;
        } else if (typeof eventoCompleto.category === 'string') {
          categoria = eventoCompleto.category;
        }
      }

      return {
        id: registration.eventId,
        titulo: registration.title || "Sin título",
        descripcion: registration.description || "Sin descripción",
        fechaInicio: eventoCompleto?.startDateTime || null,
        lugar: eventoCompleto?.location || null,
        categoria: categoria,
        imagen: eventoCompleto?.image || noImagePlaceholder,
        registrationId: registration.registrationId,
        estadoInscripcion: estadoInscripcion,
        fechaInscripcion: registration.dateTime
      };
    }).filter(item => item !== null)
      .sort((a, b) => {
        const fechaA = new Date(a.fechaInscripcion || 0);
        const fechaB = new Date(b.fechaInscripcion || 0);
        return fechaB.getTime() - fechaA.getTime();
      });
    
    return mapped;
  }, [registrations, eventosCompletos]);

  const handleVer = (id) => {
    navigate(`/evento/${id}`);
  };

  const handleBaja = (registrationId, eventoTitulo) => {
    // Guardar datos de la inscripción a cancelar y abrir diálogo
    setInscripcionACancelar({ registrationId, eventoTitulo });
    setOpenCancelacionDialog(true);
  };

  // Procesar cancelación después de confirmar
  const proceedWithCancellation = async () => {
    if (!inscripcionACancelar) return;
    
    const { registrationId } = inscripcionACancelar;
    setCancelandoId(registrationId);
    setOpenCancelacionDialog(false);
    
    try {
      await cancelRegistration(registrationId);
      
      // Recargar inscripciones después de cancelar
      await handleReload();
    } catch (error) {
    
      const errorMsg = error.response?.data?.error || 
                       error.response?.data?.message ||
                       error.response?.data || 
                       'Error al cancelar la inscripción. Por favor, intenta nuevamente.';
      setErrorMessage(errorMsg);
      setOpenErrorDialog(true);
    } finally {
      setCancelandoId(null);
      setInscripcionACancelar(null);
    }
  };

  // Función para obtener el estilo y configuración según el estado
  const getEstadoConfig = (estado) => {
    const estadoUpper = estado?.toUpperCase() || 'ACTIVE';
    
    switch (estadoUpper) {
      case 'CANCELED':
      case 'CANCELLED':
        return {
          texto: 'Cancelada',
          color: 'error',
          icon: <CancelIcon fontSize="small" />,
          bgColor: '#ffebee',
          textColor: '#c62828',
          borderColor: '#ef5350'
        };
      case 'WAITLIST':
        return {
          texto: 'En Lista de Espera',
          color: 'warning',
          icon: <AccessTimeIcon fontSize="small" />,
          bgColor: '#fff8e1',
          textColor: '#f57c00',
          borderColor: '#ffb74d'
        };
      case 'ACTIVE':
      case 'CONFIRMED':
      case 'REGISTERED':
      default:
        return {
          texto: 'Confirmada',
          color: 'success',
          icon: <CheckCircleIcon fontSize="small" />,
          bgColor: '#e8f5e8',
          textColor: '#2e7d32',
          borderColor: '#66bb6a'
        };
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
                inscripcionesFormateadas.map((inscripcion) => {
                  const estadoConfig = getEstadoConfig(inscripcion.estadoInscripcion);
                  const esCancelada = inscripcion.estadoInscripcion?.toUpperCase() === 'CANCELED';
                  
                  return (
                    <Card
                      key={inscripcion.registrationId}
                      sx={{
                        display: "flex",
                        flexDirection: { xs: "column", sm: "row" },
                        alignItems: { xs: "stretch", sm: "center" },
                        borderRadius: 3,
                        boxShadow: esCancelada ? "0 1px 4px rgba(0,0,0,0.1)" : "0 2px 8px rgba(0,0,0,0.12)",
                        border: `2px solid ${estadoConfig.borderColor}`,
                        bgcolor: esCancelada ? "#f5f5f5" : theme.palette.background.paper,
                        overflow: "hidden",
                        opacity: esCancelada ? 0.75 : 1,
                        position: 'relative'
                      }}
                    >
                    <Box sx={{ position: 'relative' }}>
                      <CardMedia
                        component="img"
                        image={inscripcion.imagen}
                        alt={inscripcion.titulo}
                        sx={{
                          width: { xs: "100%", sm: 160 },
                          height: { xs: 160, sm: 140 },
                          objectFit: "cover",
                          filter: esCancelada ? 'grayscale(50%)' : 'none'
                        }}
                      />
                      {esCancelada && (
                        <Box
                          sx={{
                            position: 'absolute',
                            top: 8,
                            right: 8,
                            bgcolor: 'rgba(239, 83, 80, 0.9)',
                            color: 'white',
                            px: 1,
                            py: 0.5,
                            borderRadius: 1,
                            fontSize: '0.75rem',
                            fontWeight: 600,
                            textTransform: 'uppercase',
                            letterSpacing: 0.5
                          }}
                        >
                          Cancelada
                        </Box>
                      )}
                    </Box>
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
                          icon={estadoConfig.icon}
                          label={estadoConfig.texto}
                          size="small"
                          sx={{ 
                            fontWeight: 600,
                            bgcolor: estadoConfig.bgColor,
                            color: estadoConfig.textColor,
                            border: `1px solid ${estadoConfig.borderColor}`,
                            '& .MuiChip-icon': {
                              color: estadoConfig.textColor
                            }
                          }}
                        />
                      </Stack>
                      <Typography variant="h6" fontWeight={700} sx={{ mb: 0.5 }}>
                        {inscripcion.titulo}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                        {inscripcion.descripcion}
                      </Typography>
                      {inscripcion.fechaInicio && (
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                          <b>Fecha del evento:</b> {new Date(inscripcion.fechaInicio).toLocaleString("es-AR", {
                            day: "numeric",
                            month: "short",
                            year: "numeric",
                            hour: "2-digit",
                            minute: "2-digit"
                          })}
                        </Typography>
                      )}
                      {inscripcion.lugar && (
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                          <b>Ubicación:</b> {inscripcion.lugar}
                        </Typography>
                      )}
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
                        {!esCancelada ? (
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
                        ) : (
                          <Typography 
                            variant="body2" 
                            sx={{ 
                              color: '#666',
                              fontStyle: 'italic',
                              display: 'flex',
                              alignItems: 'center',
                              mt: 1
                            }}
                          >
                            Inscripción cancelada
                          </Typography>
                        )}
                      </Stack>
                    </CardContent>
                  </Card>
                );
                })
              )}
            </Stack>
          </>
        )}
        </>
        )}

        {/* Diálogo de confirmación para cancelación */}
        <ConfirmDialog
          open={openCancelacionDialog}
          onClose={() => {
            setOpenCancelacionDialog(false);
            setInscripcionACancelar(null);
          }}
          onConfirm={proceedWithCancellation}
          title="Cancelar Inscripción"
          message={`¿Estás seguro de que deseas darte de baja del evento <strong>"${inscripcionACancelar?.eventoTitulo}"</strong>?<br/><br/>Esta acción no se puede deshacer.`}
          confirmText="Sí, darme de baja"
          cancelText="No, mantener"
          loading={cancelandoId !== null}
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
      </Box>
    </Box>
  );
};