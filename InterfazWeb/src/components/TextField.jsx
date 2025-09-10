import React from 'react';
import { TextField } from '@mui/material';
import { useTheme } from '@mui/material/styles';

export const TextFieldCustom = (
    {placeholder = ""}

) => {

    const theme = useTheme();
    return (
        <TextField
            placeholder={placeholder}
            variant="filled"
            name='channelUri'
            fullWidth
            InputProps={{disableUnderline: true}}
            sx={{
                maxWidth: '400px',
                mx: 'auto',
                backgroundColor: theme.palette.textfield,
                borderRadius: '25px',
                boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.15)',
                '& .MuiFilledInput-root': {
                    borderRadius: '25px',
                    backgroundColor: theme.palette.textfield,
                    height: '48px',
                    '&:hover': {
                        backgroundColor: theme.palette.textfield.hoover,
                        color: theme.palette.text.primary,
                        '& .MuiFilledInput-input::placeholder': {
                            color: theme.palette.text.primary,
                            opacity: 1
                        }
                    },
                    '&.Mui-focused': {
                        backgroundColor: theme.palette.textfield.hoover,
                    }
                },
                '& .MuiFilledInput-input': {
                    color: theme.palette.text.primary,
                    padding: '14px 20px',
                    textAlign: 'center',
                    '&::placeholder': {
                        color: theme.palette.text.primary,
                        opacity: 0.7
                    }
                }
            }}
        />
    );
}
