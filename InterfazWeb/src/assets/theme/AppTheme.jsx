import { ThemeProvider } from "@emotion/react";
import { CssBaseline } from "@mui/material";
import { main } from "./theme";
export const AppTheme = ({ children }) => {
    return (
        <ThemeProvider theme={main}>
            <CssBaseline />
            {children}
        </ThemeProvider>
    );
};
