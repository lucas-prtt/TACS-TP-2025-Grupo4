import React from 'react';
import { TextField } from '@mui/material';
import { useTheme } from '@mui/material/styles';

export const TextFieldCustom = ({
    placeholder = "",
    label = "",
    fullWidth = true,
    multiline = false,
    rows,
    minRows,
    maxRows,
    sx = {},
    onlyNumbers = false,
    allowFloat = false, // NUEVO PROP
    ...props
}) => {
    const theme = useTheme();
    return (
        <TextField
            placeholder={placeholder}
            label={label}
            variant="filled"
            name='channelUri'
            fullWidth={fullWidth}
            multiline={multiline}
            rows={rows}
            minRows={minRows}
            maxRows={maxRows}
            type={onlyNumbers ? "number" : "text"}
            inputProps={
                onlyNumbers
                    ? allowFloat
                        ? { step: 0.1, inputMode: "decimal" } // step 0.1 para flotante
                        : { step: 1, inputMode: "numeric", pattern: "[0-9]*" }
                    : undefined
            }
            InputProps={{ disableUnderline: true }}
            sx={{
                backgroundColor: theme.palette.textfield.primary,
                borderRadius: '10px',
                boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.15)',
                '& .MuiFilledInput-root': {
                    borderRadius: '10px',
                    backgroundColor: theme.palette.textfield.primary,
                    height: multiline ? 'auto' : '48px',
                    '&:hover': {
                        backgroundColor: theme.palette.textfield.hover,
                        color: theme.palette.text.primary,
                        '& .MuiFilledInput-input::placeholder': {
                            color: theme.palette.text.primary,
                            opacity: 1
                        }
                    },
                    '&.Mui-focused': {
                        backgroundColor: theme.palette.textfield.hover,
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
                },
                ...sx
            }}
            {...props}
        />
    );
}
