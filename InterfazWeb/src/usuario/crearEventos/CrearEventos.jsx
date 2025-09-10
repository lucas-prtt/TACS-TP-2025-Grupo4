import { Box } from '@mui/material';
import { useTheme } from '@mui/material/styles';    
import { TextFieldCustom } from "../../components/TextField"; 
import { NavbarApp } from '../../components/NavbarApp';
export const CrearEventos = () => {
    const theme = useTheme();
    return (
         <Box minHeight="100vh" sx={{ display: 'flex', flexDirection: 'row' }}>
            <Box sx={{ width: '250px', flexShrink: 0 }}>
                <NavbarApp />
            </Box>
             <Box sx={{ flexGrow: 1, p: 3, bgcolor: theme.palette.background.primary ,display: 'flex', justifyContent: 'center', alignItems: 'stretch'}}>
                 <TextFieldCustom placeholder="Título del evento" />
             </Box>
         </Box>
    );
};

