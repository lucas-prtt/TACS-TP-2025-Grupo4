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
  disabled = false,
  error = false,
  ...props
}) => {
  const theme = useTheme();

  let pickerValue = null;
  if (value !== null && value !== undefined && value !== "") {
    const temp = dayjs.isDayjs(value) ? value : dayjs(value);
    pickerValue = temp.isValid() ? temp : null;
  }

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
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Box sx={{ width: '100%', display: 'flex', ...sx }}>
        <TimePicker
          value={pickerValue}
          onChange={onChange}
          ampm={false}
          minutesStep={1}
          disabled={disabled}
          {...props}
          slotProps={{
            textField: {
              fullWidth: true,
              variant: "outlined",
              error: error,
              InputProps: {
                sx: {
                  borderRadius: '10px',
                  backgroundColor: bgColor,
                  border: `2px solid ${borderColor}`,
                  height: '48px',
                  px: 1,
                  alignItems: 'center',
                  color: textColor,
                  '& input': {
                    textAlign: 'center',
                    color: textColor,
                    fontWeight: 500,
                    fontSize: '1rem',
                    padding: 0,
                    background: 'transparent',
                    cursor: 'pointer',
                    '&::placeholder': {
                      color: "#9CA3AF",
                      opacity: 1
                    }
                  },
                  '&:hover': {
                    backgroundColor: disabled ? "#F9FAFB" : "#F3F4F6",
                    borderColor: disabled ? "#E5E7EB" : "#C4B5FD",
                  },
                  '&.Mui-focused': {
                    backgroundColor: "#fff",
                    borderColor: "#8B5CF6",
                    boxShadow: "0 0 0 2px #8B5CF633"
                  },
                  '&.Mui-error': {
                    borderColor: "#DC2626"
                  }
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
