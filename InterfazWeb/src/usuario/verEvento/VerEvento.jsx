import React from "react";
import { Box, Typography, Chip, Divider, Stack, Button } from "@mui/material";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import PlaceIcon from "@mui/icons-material/Place";
import PeopleIcon from "@mui/icons-material/People";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { useNavigate } from "react-router-dom";
import { ButtonCustom } from "../../components/Button";
import { NavbarApp } from "../../components/NavbarApp";
import { Mapa } from "./Mapa";

export const VerEvento = ({ evento }) => {
  const navigate = useNavigate();

  if (!evento) return null;

  React.useEffect(() => {
    document.body.style.overflow = "auto";
    return () => { document.body.style.overflow = "auto"; };
  }, []);

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
          <Stack direction="row" spacing={1} sx={{ mb: 1 }}>
            <Chip label={evento.categoria} color="primary" />
            <Chip label={evento.estado} color="success" />
          </Stack>
          {/* Título */}
          <Typography variant="h4" fontWeight={700} sx={{ mb: 2 }}>
            {evento.titulo}
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
            <img
              src={evento.imagen}
              alt={evento.titulo}
              style={{ width: "100%", height: "100%", objectFit: "cover", display: "block" }}
            />
          </Box>
          {/* Descripción */}
          <Typography variant="h6" fontWeight={600} sx={{ mb: 1 }}>
            Descripción
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 2 }}>
            {evento.descripcion}
          </Typography>
          <Divider sx={{ mb: 2 }} />
          {/* Precio */}
          <Typography variant="h6" fontWeight={600} sx={{ mb: 1 }}>
            Precio
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            ${evento.precio}
          </Typography>
          {/* Capacidad */}
          <Typography variant="h6" fontWeight={600} sx={{ mb: 1 }}>
            Capacidad
          </Typography>
          <Stack direction="row" spacing={2} alignItems="center" sx={{ mb: 2 }}>
            <PeopleIcon color="action" />
            <Typography variant="body2" color="text.secondary">
              {evento.min_participantes} / {evento.max_participantes} asistentes
            </Typography>
          </Stack>
          {/* Fecha y hora */}
          <Typography variant="h6" fontWeight={600} sx={{ mb: 1 }}>
            Fecha y Hora
          </Typography>
          <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
            <CalendarMonthIcon color="action" />
            <Typography variant="body2" color="text.secondary">
              {new Date(evento.fechaInicio).toLocaleString("es-AR", {
                day: "numeric",
                month: "short",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit"
              })}
              {evento.fechaFin &&
                <>{" "} - {new Date(evento.fechaFin).toLocaleString("es-AR", {
                  hour: "2-digit",
                  minute: "2-digit"
                })} hs</>
              }
            </Typography>
          </Stack>
          {/* Ubicación */}
          <Typography variant="h6" fontWeight={600} sx={{ mb: 1 }}>
            Ubicación
          </Typography>
          <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
            <PlaceIcon color="action" />
            <Typography variant="body2" color="text.secondary">
              {evento.lugar}
            </Typography>
          </Stack>
          {/* Mapa */}
          <Mapa direccion={evento.lugar} />
          {/* Tags */}
          <Stack direction="row" spacing={1} sx={{ my: 2 }}>
            {evento.tags && evento.tags.map(tag => (
              <Chip key={tag} label={tag} variant="outlined" />
            ))}
          </Stack>
          {/* Botón inscribirse */}
          <ButtonCustom
            bgColor="#181828"
            color="#fff"
            hoverBgColor="#23234a"
            hoverColor="#fff"
            sx={{ alignSelf: "center", minWidth: 180, fontWeight: 700, fontSize: 16, mt: 1 }}
            onClick={() => {/* lógica de inscripción */}}
          >
            Inscribirse
          </ButtonCustom>
        </Box>
      </Box>
    </Box>
  );
};