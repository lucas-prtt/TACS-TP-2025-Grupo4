import { createTheme } from "@mui/material";

export const main = createTheme({
  typography: {
    fontFamily: "'Plus Jakarta Sans', 'Roboto', 'Arial', sans-serif",
  },
  palette: {
    mode: "light",
    background: {
      default: "#F9FAFB",
      paper: "#F3F4F6"
    },
    text: {
      primary: "#1E293B",
      secondary: "#6B7280",
      disabled: "#9CA3AF"
    },
    primary: {
      main: "#8B5CF6",      // violeta
      dark: "#7C3AED",
      contrastText: "#FFFFFF"
    },
    secondary: {
      main: "#F59E0B",      // naranja
      dark: "#D97706",
      contrastText: "#FFFFFF"
    },
    error: { main: "#DC2626" },
    success: { main: "#16A34A" },
    warning: { main: "#FBBF24" }
  },
  components: {
    MuiTextField: {
      defaultProps: {
        variant: "outlined"
      }
    },
    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          borderRadius: 10,
          backgroundColor: "#fff",
          color: "#1E293B",
          '& .MuiOutlinedInput-notchedOutline': {
            borderColor: "#E5E7EB", // gris claro
          },
          '&:hover .MuiOutlinedInput-notchedOutline': {
            borderColor: "#C4B5FD", // violeta claro
          },
          '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
            borderColor: "#8B5CF6", // primario
            boxShadow: "0 0 0 2px #8B5CF633"
          },
          '&.Mui-disabled': {
            backgroundColor: "#F9FAFB",
            color: "#9CA3AF",
            '& .MuiOutlinedInput-notchedOutline': {
              borderColor: "#E5E7EB"
            }
          },
          '&.Mui-error .MuiOutlinedInput-notchedOutline': {
            borderColor: "#DC2626"
          }
        },
        input: {
          color: "#1E293B",
          '&::placeholder': {
            color: "#9CA3AF",
            opacity: 1
          }
        }
      }
    },
    MuiSelect: {
      styleOverrides: {
        root: {
          color: "#1E293B",
          '&.Mui-disabled': {
            backgroundColor: "#F9FAFB",
            color: "#9CA3AF"
          }
        },
        icon: {
          color: "#6B7280", // gris medio
          transition: "color 0.2s",
          // hover y focus sobre el icono
          '.Mui-focused &': {
            color: "#8B5CF6"
          },
          '.Mui-hover &': {
            color: "#8B5CF6"
          }
        }
      }
    },
    MuiInputLabel: {
      styleOverrides: {
        root: {
          color: "#6B7280",
          '&.Mui-focused': {
            color: "#8B5CF6"
          },
          '&.Mui-error': {
            color: "#DC2626"
          },
          '&.Mui-disabled': {
            color: "#9CA3AF"
          }
        }
      }
    },
    MuiFormHelperText: {
      styleOverrides: {
        root: {
          color: "#6B7280",
          '&.Mui-error': {
            color: "#DC2626"
          },
          '&.Mui-disabled': {
            color: "#9CA3AF"
          }
        }
      }
    }
  }
});
