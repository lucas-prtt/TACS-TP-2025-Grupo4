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
  placeholder = "Seleccionar fecha"
}) =>{
  const theme = useTheme();
  const [anchor, setAnchor] = useState(null);
  const buttonRef = useRef(null);

  const handleClick = (event) => {
    setAnchor(event.currentTarget);
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

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={es}>
      <Box sx={{ width: '100%', display: 'flex', ...sx }}>
        <Button
          ref={buttonRef}
          onClick={handleClick}
          fullWidth
          sx={{
            backgroundColor: theme.palette.buttonDate.primary,
            borderRadius: '10px',
            boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.15)',
            color: theme.palette.text.primary,
            height: '48px',
            textTransform: 'none',
            fontWeight: 500,
            fontSize: '1rem',
            justifyContent: 'center',
            alignItems: 'center',
            display: 'flex',
            px: 2,
            '&:hover': {
              backgroundColor: theme.palette.buttonDate.hover,
              color: theme.palette.text.primary,
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
              backgroundColor: theme.palette.buttonDate.primary,
              borderRadius: '10px',
              boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.15)',
              border: 'none',
            },
            '& .MuiDayCalendar-weekDayLabel': {
              color: `${theme.palette.text.primary} !important`,
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
                  backgroundColor: theme.palette.buttonDate.primary,
                  borderRadius: '10px',
                }
              },
              day: {
                sx: {
                  color: theme.palette.text.primary,
                  '&.Mui-selected': {
                    backgroundColor: "#0a60b1ff" + " !important",
                    color: "#ffffff !important"
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