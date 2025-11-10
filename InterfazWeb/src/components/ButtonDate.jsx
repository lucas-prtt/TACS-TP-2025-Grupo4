import React, { useState, useRef } from 'react';
import { Box, Popover, useTheme, Button } from '@mui/material';
import { StaticDatePicker } from '@mui/x-date-pickers/StaticDatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import es from 'date-fns/locale/es';
import { startOfDay } from 'date-fns';

export const ButtonDate =({ 
  value, 
  onChange, 
  sx = {},
  placeholder = "Seleccionar fecha",
  disabled = false,
  error = false
}) =>{
  const theme = useTheme();
  const [anchor, setAnchor] = useState(null);
  const buttonRef = useRef(null);

  const handleClick = (event) => {
    if (!disabled) setAnchor(event.currentTarget);
  };

  const handleClose = () => {
    setAnchor(null);
  };

  const formatDate = (date) => {
    if (!date) return placeholder;
    return date.toLocaleDateString('es-ES', { 
      day: '2-digit', 
      month: '2-digit', 
      year: 'numeric' 
    });
  };

  const handleDateChange = (newValue) => {
    onChange(newValue);
    handleClose();
  };

  // Colores según estado
  let borderColor = "#E5E7EB";
  let bgColor = "#fff";
  let textColor = "#1E293B";
  if (disabled) {
    borderColor = "#E5E7EB";
    bgColor = "#F9FAFB";
    textColor = "#9CA3AF";
  } else if (error) {
    borderColor = "#DC2626";
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={es}>
      <Box sx={{ width: '100%', display: 'flex', ...sx }}>
        <Button
          ref={buttonRef}
          onClick={handleClick}
          fullWidth
          disabled={disabled}
          sx={{
            backgroundColor: bgColor,
            borderRadius: '10px',
            border: `2px solid ${borderColor}`,
            color: textColor,
            height: '48px',
            textTransform: 'none',
            fontWeight: 500,
            fontSize: '1rem',
            justifyContent: 'center',
            alignItems: 'center',
            display: 'flex',
            px: 2,
            transition: 'all 0.2s',
            '&:hover': {
              backgroundColor: disabled ? "#F9FAFB" : "#F3F4F6",
              borderColor: disabled ? "#E5E7EB" : "#C4B5FD",
              color: textColor,
            },
            '&.Mui-focusVisible, &:focus': {
              backgroundColor: "#fff",
              borderColor: "#8B5CF6",
              boxShadow: "0 0 0 2px #8B5CF633"
            }
          }}
        >
          {formatDate(value)}
        </Button>
        <Popover
          open={Boolean(anchor)}
          anchorEl={anchor}
          onClose={handleClose}
          anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'center',
          }}
          transformOrigin={{
            vertical: 'top',
            horizontal: 'center',
          }}
          sx={{
            '& .MuiPaper-root': {
              backgroundColor: "#fff",
              borderRadius: '10px',
              boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.15)',
              border: '2px solid #E5E7EB',
            },
            '& .MuiDayCalendar-weekDayLabel': {
              color: "#1E293B !important",
              opacity: '1 !important',
              fontWeight: '500 !important',
            }
          }}
        >
          <StaticDatePicker
            value={value}
            onChange={handleDateChange}
            displayStaticWrapperAs="desktop"
            renderInput={() => null}
            minDate={startOfDay(new Date())}
            slotProps={{
              layout: {
                sx: {
                  backgroundColor: "#fff",
                  borderRadius: '10px',
                }
              },
              day: {
                sx: {
                  color: "#1E293B",
                  '&.Mui-selected': {
                    backgroundColor: "#8B5CF6 !important",
                    color: "#fff !important"
                  }
                }
              }
            }}
          />
        </Popover>
      </Box>
    </LocalizationProvider>
  );
}
