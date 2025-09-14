import React from 'react';
import { TextField } from '@mui/material';

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
    allowFloat = false,
    ...props
}) => {
    return (
        <TextField
            placeholder={placeholder}
            label={label}
            variant="outlined" // Usa outlined para aplicar el theme
            fullWidth={fullWidth}
            multiline={multiline}
            rows={rows}
            minRows={minRows}
            maxRows={maxRows}
            type={onlyNumbers ? "number" : "text"}
            inputProps={
                onlyNumbers
                    ? allowFloat
                        ? { step: 0.1, inputMode: "decimal" }
                        : { step: 1, inputMode: "numeric", pattern: "[0-9]*" }
                    : undefined
            }
            sx={sx}
            {...props}
        />
    );
}
