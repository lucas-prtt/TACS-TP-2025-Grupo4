import React from "react";
import { Button } from "@mui/material";

/**
 * Botón personalizado con estilos y colores configurables.
 * 
 * Props:
 * - bgColor: color de fondo (default: '#181828')
 * - color: color de letra (default: '#fff')
 * - hoverBgColor: color de fondo al hover (default: '#23234a')
 * - hoverColor: color de letra al hover (default: '#fff')
 * - startIcon: icono opcional a la izquierda
 * - endIcon: icono opcional a la derecha
 * - children: contenido del botón
 * - sx: estilos extra
 * - ...props: otros props de MUI Button
 */
export const ButtonCustom = ({
  bgColor = '#181828',
  color = '#fff',
  hoverBgColor = '#23234a',
  hoverColor = '#fff',
  startIcon,
  endIcon,
  children,
  sx = {},
  ...props
}) => (
  <Button
    variant="contained"
    startIcon={startIcon}
    endIcon={endIcon}
    sx={{
      background: bgColor,
      color: color,
      borderRadius: '10px',
      px: 3,
      py: 1.2,
      fontWeight: 600,
      fontSize: 15,
      textTransform: 'none',
      boxShadow: 'none',
      width: { xs: '100%', sm: 'auto' },
      mt: { xs: 1, sm: 0 },
      '&:hover': {
        background: hoverBgColor,
        color: hoverColor,
      },
      ...sx,
    }}
    {...props}
  >
    {children}
  </Button>
);
