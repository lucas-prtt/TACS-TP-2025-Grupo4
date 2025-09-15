import React, { useState } from "react";
import { Box, Typography, IconButton, Divider } from "@mui/material";
import CalendarMonthOutlinedIcon from "@mui/icons-material/CalendarMonthOutlined";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import { es } from "date-fns/locale";
import { format, addMonths, subMonths, startOfMonth, endOfMonth, startOfWeek, endOfWeek, addDays, isSameMonth, isSameDay } from "date-fns";
import { datosEventos } from "../eventos/datosEventos";
import { NavbarApp } from "../../components/NavbarApp"; // Importa la Navbar

const diasSemana = ["Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"];

export const Calendario = () => {
  const [fechaActual, setFechaActual] = useState(new Date());
  const [fechaSeleccionada, setFechaSeleccionada] = useState(new Date());

  // Eventos agrupados por fecha (YYYY-MM-DD)
  const eventosPorFecha = {};
  datosEventos.forEach(ev => {
    const fecha = ev.fechaInicio.split("T")[0];
    if (!eventosPorFecha[fecha]) eventosPorFecha[fecha] = [];
    eventosPorFecha[fecha].push(ev);
  });

  // Helpers para el calendario
  const mesInicio = startOfMonth(fechaActual);
  const mesFin = endOfMonth(fechaActual);
  const semanaInicio = startOfWeek(mesInicio, { weekStartsOn: 0 });
  const semanaFin = endOfWeek(mesFin, { weekStartsOn: 0 });

  const dias = [];
  let dia = semanaInicio;
  while (dia <= semanaFin) {
    dias.push(dia);
    dia = addDays(dia, 1);
  }

  // Eventos del día seleccionado
  const fechaSelStr = format(fechaSeleccionada, "yyyy-MM-dd");
  const eventosHoy = eventosPorFecha[fechaSelStr] || [];

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
        }}
      >
        <Box
          sx={{
            width: '100%',
            maxWidth: 1100,
            margin: '0 auto',
            display: "flex",
            flexDirection: { xs: "column", md: "row" },
            gap: 2,
            alignItems: { xs: "center", md: "flex-start" },
            justifyContent: "center"
          }}
        >
          {/* Calendario */}
          <Box
            id="calendario-box"
            sx={{
              flex: 2,
              bgcolor: "#fff",
              borderRadius: 3,
              p: 3,
              boxShadow: "0 2px 8px rgba(0,0,0,0.07)",
              border: "1px solid #e0e0e0",
              minWidth: { xs: "85vw", sm: 320, md: 400 }, // achica en mobile
              maxWidth: { xs: "95vw", md: 600 },
              width: { xs: "100%", md: "auto" },
              mb: { xs: 2, md: 0 },
              minHeight: { xs: "unset", md: 600 },
              height: { md: "auto" }
            }}
          >
            {/* Header */}
            <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
              <Typography variant="h6" fontWeight={600} sx={{ flex: 1 }}>
                {format(fechaActual, "MMMM yyyy", { locale: es }).replace(/^\w/, c => c.toUpperCase())}
              </Typography>
              <IconButton onClick={() => setFechaActual(subMonths(fechaActual, 1))}>
                <ArrowBackIosNewIcon fontSize="small" />
              </IconButton>
              <IconButton onClick={() => setFechaActual(addMonths(fechaActual, 1))}>
                <ArrowForwardIosIcon fontSize="small" />
              </IconButton>
            </Box>
            {/* Días de la semana */}
            <Box sx={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", mb: 1 }}>
              {diasSemana.map(dia => (
                <Typography key={dia} align="center" color="text.secondary" sx={{ fontWeight: 500 }}>
                  {dia}
                </Typography>
              ))}
            </Box>
            {/* Días del mes */}
            <Box sx={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: 1 }}>
              {dias.map((d, i) => {
                const isCurrentMonth = isSameMonth(d, fechaActual);
                const isSelected = isSameDay(d, fechaSeleccionada);
                const fechaStr = format(d, "yyyy-MM-dd");
                const tieneEvento = !!eventosPorFecha[fechaStr];
                return (
                  <Box
                    key={i}
                    onClick={() => isCurrentMonth && setFechaSeleccionada(d)}
                    sx={{
                      aspectRatio: "1/1",
                      bgcolor: isSelected ? "#ededf7" : isCurrentMonth ? "#fafafd" : "#f3f3f3",
                      border: isSelected ? "2px solid #8884ff" : "1px solid #e0e0e0",
                      borderRadius: 2,
                      cursor: isCurrentMonth ? "pointer" : "default",
                      opacity: isCurrentMonth ? 1 : 0.5,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      fontWeight: 500,
                      fontSize: 18,
                      position: "relative",
                      transition: "border 0.2s"
                    }}
                  >
                    {format(d, "d")}
                    {tieneEvento && (
                      <Box sx={{
                        position: "absolute",
                        bottom: 7,
                        left: "50%",
                        transform: "translateX(-50%)",
                        width: 8,
                        height: 8,
                        bgcolor: "#8884ff",
                        borderRadius: "50%",
                      }} />
                    )}
                  </Box>
                );
              })}
            </Box>
          </Box>
          {/* Panel de eventos del día */}
          <Box
            id="eventos-box"
            sx={{
              flex: 1,
              bgcolor: "#fff",
              borderRadius: 3,
              p: 3,
              boxShadow: "0 2px 8px rgba(0,0,0,0.07)",
              border: "1px solid #e0e0e0",
              minWidth: { xs: "85vw", sm: 220, md: 280 }, // achica en mobile igual que calendario
              maxWidth: { xs: "95vw", md: 350 },
              width: { xs: "100%", md: "auto" },
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              mx: { xs: 0, md: 0 },
              minHeight: { xs: "unset", md: 600 }, // Igual altura mínima que el calendario
              height: { md: "auto" }
            }}
          >
            <Typography variant="h6" fontWeight={600} mb={2} align="center">
              Eventos del {format(fechaSeleccionada, "d 'de' MMMM", { locale: es })}
            </Typography>
            <Divider sx={{ width: "100%", mb: 2 }} />
            {eventosHoy.length === 0 ? (
              <Box sx={{ textAlign: "center", color: "#888", mt: 6 }}>
                <CalendarMonthOutlinedIcon sx={{ fontSize: 48, mb: 1 }} />
                <Typography>No hay eventos programados<br />para esta fecha</Typography>
              </Box>
            ) : (
              <Box sx={{ width: "100%" }}>
                {eventosHoy.map(ev => (
                  <Box key={ev.titulo} sx={{
                    mb: 2,
                    p: 2,
                    borderRadius: 2,
                    bgcolor: "#f6f6fa",
                    border: "1px solid #ececec"
                  }}>
                    <Typography fontWeight={600}>{ev.titulo}</Typography>
                    <Typography variant="body2" color="text.secondary">{ev.descripcion}</Typography>
                    <Typography variant="caption" color="text.secondary">
                      {format(new Date(ev.fechaInicio), "HH:mm", { locale: es })} hs · {ev.lugar}
                    </Typography>
                  </Box>
                ))}
              </Box>
            )}
          </Box>
        </Box>
      </Box>
    </Box>
  );
};
