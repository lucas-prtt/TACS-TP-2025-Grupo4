import React from "react";
import { Box } from "@mui/material";

export const Mapa = ({ direccion }) => {
  // Codifica la dirección para usar en la URL de Google Maps
  const query = encodeURIComponent(direccion);
  const mapsUrl = `https://www.google.com/maps/search/?api=1&query=${query}`;
  const embedUrl = `https://www.google.com/maps?q=${query}&output=embed`;

  return (
    <Box sx={{ width: "100%", height: 200, borderRadius: 2, overflow: "hidden", mt: 2, cursor: "pointer" }}>
      <a href={mapsUrl} target="_blank" rel="noopener noreferrer" style={{ display: "block", width: "100%", height: "100%" }}>
        <iframe
          title="Mapa"
          src={embedUrl}
          width="100%"
          height="100%"
          style={{ border: 0, pointerEvents: "none" }}
          allowFullScreen=""
          loading="lazy"
        />
      </a>
    </Box>
  );
};
