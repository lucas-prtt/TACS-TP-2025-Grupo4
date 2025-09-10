import * as React from 'react';
import { Box, Typography, List, ListItem, ListItemIcon, ListItemText, Divider, useTheme } from '@mui/material';
import HomeIcon from '@mui/icons-material/HomeOutlined';
import CalendarTodayIcon from '@mui/icons-material/CalendarMonthOutlined';
import ListAltIcon from '@mui/icons-material/ListOutlined';
import AddIcon from '@mui/icons-material/Add';
import BarChartIcon from '@mui/icons-material/AssessmentOutlined';
import PeopleIcon from '@mui/icons-material/PeopleOutlined';
import SettingsIcon from '@mui/icons-material/SettingsOutlined';
import { useNavigate } from 'react-router-dom';

// No se necesita Toolbar ni styled para la navbar vertical
export const NavbarApp = () => {

const navigate = useNavigate();
const theme = useTheme();
  return (
    <Box
      sx={{
        width: 250,
        height: '100vh',
        bgcolor: 'background.paper',
        borderRight: '1px solid',
        borderColor: 'divider',
        display: 'flex',
        flexDirection: 'column',
        position: 'fixed',
        left: 0,
        top: 0,
        zIndex: 1200,
        p: 2,
      }}
    >
      <Box sx={{ mb: 1 }}>
        <Typography variant="h5" fontWeight={700} sx={{ lineHeight: 1 }}>
          EventManager
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Gestión de eventos
        </Typography>
      </Box>
      <Divider sx={{ mb: 2 }} />
      <List>
        <ListItem button onClick={() => navigate('/dashboard')}>
          <ListItemIcon>
            <HomeIcon sx={{ color: theme.palette.icon.primary }}/>
          </ListItemIcon>
          <ListItemText primary="Dashboard" />
        </ListItem>
        <ListItem button onClick={() => navigate('/calendario')}>
          <ListItemIcon>
            <CalendarTodayIcon sx={{ color: theme.palette.icon.primary }}/>
          </ListItemIcon>
          <ListItemText primary="Calendario" />
        </ListItem>
        <ListItem button onClick={() => navigate('/eventos')}>
          <ListItemIcon>
            <ListAltIcon sx={{ color: theme.palette.icon.primary }} />
          </ListItemIcon>
          <ListItemText primary="Eventos" />
        </ListItem>
        <ListItem button onClick={() => navigate('/crearEventos')}>
          <ListItemIcon >
            <AddIcon sx={{ color: theme.palette.icon.primary }}/>
          </ListItemIcon>
          <ListItemText primary="Crear Evento" />
        </ListItem>
        <ListItem button onClick={() => navigate('/analiticas')}>
          <ListItemIcon >
            <BarChartIcon sx={{ color: theme.palette.icon.primary }} />
          </ListItemIcon>
          <ListItemText primary="Analíticas" />
        </ListItem>
        <ListItem button onClick={() => navigate('/asistentes')}>
          <ListItemIcon >
            <PeopleIcon sx={{ color: theme.palette.icon.primary }}/>
          </ListItemIcon>
          <ListItemText primary="Asistentes" />
        </ListItem>
        <ListItem button onClick={() => navigate('/configuracion')}>
          <ListItemIcon >
            <SettingsIcon sx={{ color: theme.palette.icon.primary }}/>
          </ListItemIcon>
          <ListItemText primary="Configuración" />
        </ListItem>
      </List>
    </Box>
  );
}


