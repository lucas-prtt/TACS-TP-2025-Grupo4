import { Box, Grid } from "@mui/material";
import { CardEvento } from "./CardEvento";
import { datosEventos } from "./datosEventos";
import { NavbarApp } from "../../components/NavbarApp";
import { useTheme } from '@mui/material/styles';

export const Eventos = () => {
    const theme = useTheme();
    return (
        <Box minHeight="100vh" sx={{ display: 'flex', flexDirection: 'row', bgcolor: theme.palette.background.primary }}>
            <Box
                sx={{
                    width: { xs: 0, md: '250px' },
                    flexShrink: 0,
                    display: { xs: 'none', md: 'block' }
                }}
            >
                <NavbarApp />
            </Box>
            <Box
                sx={{
                    display: { xs: 'block', md: 'none' },
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    zIndex: 1300
                }}
            >
                <NavbarApp />
            </Box>
            {/* Contenido principal */}
            <Box
                flex={1}
                p={3}
                sx={{
                    width: '100%',
                    pt: { xs: 4, md: 3 }, 
                    pl: { xs: 5, md: 0 }, 
                    display: 'flex',
                    justifyContent: 'center',
                }}
            >
                
                <Grid
                    container
                    spacing={3}
                    justifyContent="center"
                    alignItems="stretch"
                    sx={{ maxWidth: 1200 }}
                >
                    {datosEventos.map((evento) => (
                        <Grid
                            item
                            xs={12}
                            sm={6}
                            md={4}
                            lg={4}
                            key={evento.titulo}
                            display="flex"
                            justifyContent="center"
                            alignItems="stretch"
                        >
                            <CardEvento evento={evento} />
                        </Grid>
                    ))}
                </Grid>
            </Box>
        </Box>
    );
};
