import React from 'react';
import { Box } from '@mui/material';
import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { useTheme } from '@mui/material/styles';
import dayjs from 'dayjs';

export const ButtonTime = ({
  value,
  onChange,
  placeholder = "Seleccionar hora",
  sx = {},
  ...props
}) => {
  const theme = useTheme();

  let pickerValue = null;
  if (value !== null && value !== undefined && value !== "") {
    const temp = dayjs.isDayjs(value) ? value : dayjs(value);
    pickerValue = temp.isValid() ? temp : null;
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Box sx={{ width: '100%', display: 'flex', ...sx }}>
        <TimePicker
          value={pickerValue}
          onChange={onChange}
          ampm={false}
          minutesStep={1}
          {...props}
          slotProps={{
            textField: {
              fullWidth: true,
              variant: "filled",
              InputProps: {
                disableUnderline: true,
                sx: {
                  borderRadius: '10px',
                  backgroundColor: theme.palette.buttonDate.primary,
                  boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.15)',
                  height: '48px',
                  px: 1,
                  alignItems: 'center',
                  '& input': {
                    textAlign: 'center',
                    color: theme.palette.text.primary,
                    fontWeight: 500,
                    fontSize: '1rem',
                    padding: 0,
                    background: 'transparent',
                    cursor: 'pointer',
                  },
                  '&:hover': {
                    backgroundColor: theme.palette.buttonDate.hover,
                  },
                }
              },
              inputProps: {
                placeholder: pickerValue ? undefined : placeholder,
                readOnly: true,
              },
              label: undefined,
            }
          }}
          format="HH:mm"
          label={undefined}
        />
      </Box>
    </LocalizationProvider>
  );
};