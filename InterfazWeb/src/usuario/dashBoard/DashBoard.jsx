import {Box} from "@mui/material";
import {useTheme} from "@mui/material/styles";
export const DashBoard = () => {

    const theme = useTheme();
    
    return (
        <Box
         display="flex"
            flexDirection="column"
            alignItems="center"
            justifyContent="flex-start"
            minHeight="100vh"
         sx={{ 
                background: theme.palette.background.primary,
         }}
        >
        </Box>
    )
}