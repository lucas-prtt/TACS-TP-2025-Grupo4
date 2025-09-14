import React from 'react';
import { MenuItem, Select, FormControl } from '@mui/material';
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
    <FormControl fullWidth={fullWidth} variant="filled" sx={{
      backgroundColor: theme.palette.selector.primary,
      borderRadius: '10px',
      boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.15)',
      '& .MuiFilledInput-root': {
        borderRadius: '10px',
        backgroundColor: theme.palette.selector.primary,
        height: '48px',
        '&:hover': {
          backgroundColor: theme.palette.selector.hover,
          color: theme.palette.text.primary,
        },
        '&.Mui-focused': {
          backgroundColor: theme.palette.selector.hover,
        }
      },
      '& .MuiFilledInput-input': {
        color: theme.palette.text.primary,
        padding: '14px 20px',
        textAlign: 'center',
      }
    }}>
      <Select
        value={value}
        onChange={onChange}
        name={name}
        variant="filled"
        disableUnderline
        displayEmpty
        inputProps={{
          sx: {
            textAlign: 'center',
            color: value === "" ? "#bdbdbd" : theme.palette.text.primary,
            fontWeight: value === "" ? 400 : 500,
            opacity: value === "" ? 0.8 : 1,
            transition: 'color 0.2s',
            '&:hover': {
              color: theme.palette.text.primary,
              opacity: 1,
            }
          }
        }}
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