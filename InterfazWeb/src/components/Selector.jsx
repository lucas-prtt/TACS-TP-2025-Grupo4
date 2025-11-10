import React from 'react';
import { MenuItem, Select, FormControl, InputLabel } from '@mui/material';
import { useTheme } from '@mui/material/styles';

export const SelectorCustom = ({
  placeholder = "",
  opciones = [],
  value,
  onChange,
  name = "selector",
  label = "",
  fullWidth = false,
  ...props
}) => {
  const theme = useTheme();

  return (
    <FormControl fullWidth={fullWidth} variant="outlined" sx={{
      '& .MuiOutlinedInput-root': {
        borderRadius: '10px',
        backgroundColor: theme.palette.background.default,
        height: '48px',
        '&:hover': {
          backgroundColor: theme.palette.primary.dark,
          color: theme.palette.primary.contrastText,
        },
        '&.Mui-focused': {
          backgroundColor: theme.palette.primary.main, // Fondo primario en focus
          color: theme.palette.primary.contrastText,   // Letra contraste en focus
          '& .MuiSelect-select': {
            color: theme.palette.primary.contrastText, // Letra contraste en focus
          }
        }
      },
      '& .MuiSelect-select': {
        padding: '14px 20px',
        textAlign: 'center',
        color: value === "" ? theme.palette.text.secondary : theme.palette.text.primary,
        fontWeight: value === "" ? 400 : 500,
        opacity: value === "" ? 0.8 : 1,
        transition: 'color 0.2s',
        '&:hover': {
          color: theme.palette.primary.contrastText,
          opacity: 1,
        }
      }
    }}>
      {label && <InputLabel>{label}</InputLabel>}
      <Select
        value={value}
        onChange={onChange}
        name={name}
        label={label}
        displayEmpty
        {...props}
      >
        <MenuItem value="">
          {placeholder}
        </MenuItem>
        {opciones.map((opcion, idx) => (
          <MenuItem key={idx} value={opcion}>
            {opcion}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
};
