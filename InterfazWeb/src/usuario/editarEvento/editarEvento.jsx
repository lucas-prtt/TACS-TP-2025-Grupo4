import React, { useState } from "react";
import { Box, Typography, Divider, Stack, Button } from "@mui/material";
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
import { useNavigate } from "react-router-dom";

export const EditarEvento = ({ evento }) => {
  const theme = useTheme();
  const navigate = useNavigate();

  // Estados iniciales con los datos del evento recibido
  const [titulo, setTitulo] = useState(evento?.titulo || "");
  const [descripcion, setDescripcion] = useState(evento?.descripcion || "");
  const [categoria, setCategoria] = useState(evento?.categoria || "");
  const [estado, setEstado] = useState(evento?.estado || "");
  const [organizador, setOrganizador] = useState(evento?.organizador || "");
  const [tags, setTags] = useState(evento?.tags?.join(", ") || "");
  const [fecha, setFecha] = useState(evento?.fechaInicio ? new Date(evento.fechaInicio) : null);
  const [horaInicio, setHoraInicio] = useState(evento?.fechaInicio ? new Date(evento.fechaInicio) : null);
  const [horaFin, setHoraFin] = useState(evento?.fechaFin ? new Date(evento.fechaFin) : null);
  const [lugar, setLugar] = useState(evento?.lugar || "");
  const [maxParticipantes, setMaxParticipantes] = useState(evento?.max_participantes || "");
  const [precio, setPrecio] = useState(evento?.precio || "");

  const handleGuardar = () => {
    // Aquí iría la lógica para guardar los cambios (API o estado global)
    // Por ahora solo navega hacia atrás
    navigate(-1);
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
                    value={titulo}
                    onChange={e => setTitulo(e.target.value)}
                    placeholder="Ej: Conferencia de Tecnología 2025"
                  />
                </Box>
                <Box mb={2}>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Descripción *
                  </Typography>
                  <TextFieldCustom
                    value={descripcion}
                    onChange={e => setDescripcion(e.target.value)}
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
                      opciones={["Tecnología", "Música", "Deporte", "Arte", "Gastronomía"]}
                      value={categoria}
                      onChange={e => setCategoria(e.target.value)}
                      fullWidth
                    />
                  </Box>
                  <Box sx={{ flex: 1, width: { xs: '100%', sm: 'auto' } }}>
                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                      Estado *
                    </Typography>
                    <SelectorCustom
                      placeholder="Selecciona el estado"
                      opciones={["Programado", "Activo", "Finalizado"]}
                      value={estado}
                      onChange={e => setEstado(e.target.value)}
                      fullWidth
                    />
                  </Box>
                </Box>
                {/* Organizador */}
                <Box mb={2}>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Organizador *
                  </Typography>
                  <TextFieldCustom
                    value={organizador}
                    onChange={e => setOrganizador(e.target.value)}
                    placeholder="Nombre del organizador o empresa"
                  />
                </Box>
                {/* Etiquetas */}
                <Box>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Etiquetas *
                  </Typography>
                  <TextFieldCustom
                    value={tags}
                    onChange={e => setTags(e.target.value)}
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
                <Box sx={{ display: 'flex', gap: 1, mb: 1, width: '100%' }}>
                  <Box sx={{ flex: 1, minWidth: 0 }}>
                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                      Inicio *
                    </Typography>
                    <ButtonTime
                      placeholder="Inicio *"
                      value={horaInicio}
                      onChange={setHoraInicio}
                      fullWidth
                    />
                  </Box>
                  <Box sx={{ flex: 1, minWidth: 0 }}>
                    <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                      Fin *
                    </Typography>
                    <ButtonTime
                      placeholder="Fin *"
                      value={horaFin}
                      onChange={setHoraFin}
                      fullWidth
                    />
                  </Box>
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
                  value={lugar}
                  onChange={e => setLugar(e.target.value)}
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
                    value={maxParticipantes}
                    onChange={e => setMaxParticipantes(e.target.value)}
                    placeholder="Número máximo de participantes"
                    fullWidth
                    onlyNumbers={true}
                  />
                </Box>
                <Box>
                  <Typography variant="caption" color={theme.palette.text.primary} sx={{ fontSize: '0.85rem', fontWeight: 500, mb: 0.5, display: 'block' }}>
                    Precio (opcional) *
                  </Typography>
                  <TextFieldCustom
                    value={precio}
                    onChange={e => setPrecio(e.target.value)}
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
              startIcon={<SaveIcon />}
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
            >
              Guardar Cambios
            </ButtonCustom>
          </Box>
        </Box>
      </Box>
    </Box>
  );
};