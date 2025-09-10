import { Box, Grid } from "@mui/material";
import { CardEvento } from "./CardEvento";
import { datosEventos } from "./datosEventos";
import { NavbarApp } from "../../components/NavbarApp";
import { useTheme } from '@mui/material/styles';

export const Eventos = () => {
    const theme = useTheme();
    // El ancho de la navbar debe coincidir con el width de NavbarApp (250px)
    return (
        <Box minHeight="100vh" sx={{ display: 'flex', flexDirection: 'row' , bgcolor: theme.palette.background.primary}}>
            {/* Espacio reservado para la navbar */}
            <Box sx={{ width: '250px', flexShrink: 0 }}>
                <NavbarApp />
            </Box>
            <Box flex={1} p={3}>
                <Grid container spacing={3} justifyContent="center" alignItems="stretch">
                    {datosEventos.map((evento) => (
                        <Grid item xs={12} sm={6} md={4} lg={4} key={evento.titulo} display="flex" justifyContent="center" alignItems="stretch">
                            <CardEvento evento={evento} />
                        </Grid>
                    ))}
                </Grid>
            </Box>
        </Box>
    );
};
