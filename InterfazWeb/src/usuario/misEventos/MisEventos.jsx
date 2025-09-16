import React from "react";
import { Box, Typography, Card, CardContent, CardMedia, Stack, Chip } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import VisibilityIcon from "@mui/icons-material/Visibility";
import LogoutIcon from "@mui/icons-material/Logout";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { ButtonCustom } from "../../components/Button";
import { NavbarApp } from "../../components/NavbarApp";
import { datosEventos } from "../eventos/datosEventos";
import { useNavigate } from "react-router-dom";

export const MisEventos = () => {
  const theme = useTheme();
  const navigate = useNavigate();

  const eventos = datosEventos;

  const handleVer = (id) => {
    navigate(`/evento/${id}`);
  };

  const handleEditar = (id) => {
    navigate(`/editar-evento/${id}`);
  };

  const handleEliminar = (id) => {
    // Lógica para eliminar evento
  };

  // Estilos de botones igual que en CardEvento
  const sxVer = {
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
  };

  const sxEditar = {
    border: '2px solid #F59E0B',
    color: '#F59E0B',
    backgroundColor: '#fff',
    fontWeight: 700,
    borderRadius: '10px',
    px: 2,
    minHeight: { xs: 34, md: 44 },
    py: { xs: 0.5, md: 1 },
    fontSize: { xs: 15, md: 16 },
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
    minHeight: { xs: 34, md: 44 },
    '&:hover': {
      backgroundColor: '#FEE2E2',
      color: '#B91C1C',
      borderColor: '#B91C1C'
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
          Mis Eventos
        </Typography>
        <Stack spacing={3}>
          {eventos.length === 0 ? (
            <Typography variant="body1" color="text.secondary">
              No tenés eventos propios.
            </Typography>
          ) : (
            eventos.map((evento) => (
              <Card
                key={evento.id}
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
                  image={evento.imagen}
                  alt={evento.titulo}
                  sx={{
                    width: { xs: "100%", sm: 160 },
                    height: { xs: 160, sm: 140 },
                    objectFit: "cover"
                  }}
                />
                <CardContent sx={{ flex: 1, display: "flex", flexDirection: "column", justifyContent: "center" }}>
                  <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
                    <Chip
                      label={evento.categoria}
                      size="small"
                      sx={{
                        fontWeight: 500,
                        bgcolor: theme.palette.secondary.main,
                        color: theme.palette.secondary.contrastText,
                      }}
                    />
                    <Chip
                      label={evento.estado}
                      size="small"
                      color="primary"
                      sx={{ fontWeight: 500 }}
                    />
                  </Stack>
                  <Typography variant="h6" fontWeight={700} sx={{ mb: 0.5 }}>
                    {evento.titulo}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {evento.descripcion}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                    <b>Fecha:</b> {new Date(evento.fechaInicio).toLocaleString("es-AR", {
                      day: "numeric",
                      month: "short",
                      year: "numeric",
                      hour: "2-digit",
                      minute: "2-digit"
                    })}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    <b>Ubicación:</b> {evento.lugar}
                  </Typography>
                  <Stack direction="row" spacing={2} sx={{ mt: 1 }}>
                    <ButtonCustom
                      variant="outlined"
                      startIcon={<VisibilityIcon />}
                      sx={sxVer}
                      onClick={() => handleVer(evento.id)}
                    >
                      Ver
                    </ButtonCustom>
                    <ButtonCustom
                      variant="outlined"
                      startIcon={<EditIcon />}
                      sx={sxEditar}
                      onClick={() => handleEditar(evento.id)}
                    >
                      Editar
                    </ButtonCustom>
                    <ButtonCustom
                      variant="outlined"
                      startIcon={<DeleteIcon />}
                      sx={sxEliminar}
                      onClick={() => handleEliminar(evento.id)}
                    >
                      Eliminar
                    </ButtonCustom>
                  </Stack>
                </CardContent>
              </Card>
            ))
          )}
        </Stack>
      </Box>
    </Box>
  );
};