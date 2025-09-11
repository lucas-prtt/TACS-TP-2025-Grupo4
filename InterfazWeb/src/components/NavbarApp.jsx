import * as React from 'react';
import {
  Box, Typography, List, ListItem, ListItemIcon, ListItemText, Divider, useTheme, Drawer, IconButton
} from '@mui/material';
import HomeIcon from '@mui/icons-material/HomeOutlined';
import CalendarTodayIcon from '@mui/icons-material/CalendarMonthOutlined';
import ListAltIcon from '@mui/icons-material/ListOutlined';
import AddIcon from '@mui/icons-material/Add';
import BarChartIcon from '@mui/icons-material/AssessmentOutlined';
import PeopleIcon from '@mui/icons-material/PeopleOutlined';
import SettingsIcon from '@mui/icons-material/SettingsOutlined';
import MenuIcon from '@mui/icons-material/Menu';
import { useNavigate } from 'react-router-dom';

const drawerWidth = 250;
const NavbarContent = ({ navigate, theme, onClick, showMenuButton}) => (
  <Box
    sx={{
      width: drawerWidth,
      height: '100vh',
      bgcolor: theme.palette.background.primary,
      borderRight: '1px solid',
      borderColor: 'divider',
      display: 'flex',
      flexDirection: 'column',
      p: 2,
    }}
  >
    <Box sx={{ mb: 1, display: 'flex', alignItems: 'center', gap: 1 }}>
      {/* Botón hamburguesa SOLO en mobile, alineado a la izquierda */}
      {showMenuButton && (
        <IconButton
          color="inherit"
          aria-label="close drawer"
          edge="start"
          onClick={onClick}
          sx={{ display: { xs: 'block', md: 'none' }, mr: 1 }}
        >
          <MenuIcon />
        </IconButton>
      )}
      <Box>
        <Typography variant="h5" fontWeight={700} sx={{ lineHeight: 1 }}>
          EventManager
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Gestión de eventos
        </Typography>
      </Box>
    </Box>
    <Divider sx={{ mb: 2 }} />
    <List>
      <ListItem button onClick={() => { navigate('/dashboard'); onClick && onClick(); }}>
        <ListItemIcon>
          <HomeIcon sx={{ color: theme.palette.icon.primary }} />
        </ListItemIcon>
        <ListItemText primary="Dashboard" />
      </ListItem>
      <ListItem button onClick={() => { navigate('/calendario'); onClick && onClick(); }}>
        <ListItemIcon>
          <CalendarTodayIcon sx={{ color: theme.palette.icon.primary }} />
        </ListItemIcon>
        <ListItemText primary="Calendario" />
      </ListItem>
      <ListItem button onClick={() => { navigate('/eventos'); onClick && onClick(); }}>
        <ListItemIcon>
          <ListAltIcon sx={{ color: theme.palette.icon.primary }} />
        </ListItemIcon>
        <ListItemText primary="Eventos" />
      </ListItem>
      <ListItem button onClick={() => { navigate('/crearEventos'); onClick && onClick(); }}>
        <ListItemIcon>
          <AddIcon sx={{ color: theme.palette.icon.primary }} />
        </ListItemIcon>
        <ListItemText primary="Crear Evento" />
      </ListItem>
      <ListItem button onClick={() => { navigate('/analiticas'); onClick && onClick(); }}>
        <ListItemIcon>
          <BarChartIcon sx={{ color: theme.palette.icon.primary }} />
        </ListItemIcon>
        <ListItemText primary="Analíticas" />
      </ListItem>
      <ListItem button onClick={() => { navigate('/asistentes'); onClick && onClick(); }}>
        <ListItemIcon>
          <PeopleIcon sx={{ color: theme.palette.icon.primary }} />
        </ListItemIcon>
        <ListItemText primary="Asistentes" />
      </ListItem>
      <ListItem button onClick={() => { navigate('/configuracion'); onClick && onClick(); }}>
        <ListItemIcon>
          <SettingsIcon sx={{ color: theme.palette.icon.primary }} />
        </ListItemIcon>
        <ListItemText primary="Configuración" />
      </ListItem>
    </List>
  </Box>
);

export const NavbarApp = () => {
  const navigate = useNavigate();
  const theme = useTheme();
  const [mobileOpen, setMobileOpen] = React.useState(false);

  return (
    <>
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={() => setMobileOpen(false)}
        ModalProps={{
          keepMounted: true,
        }}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
        }}
      >
        <NavbarContent
          navigate={navigate}
          theme={theme}
          onClick={() => setMobileOpen(false)}
          showMenuButton={true}
        />
      </Drawer>

      <Box
        sx={{
          display: { xs: 'none', md: 'block' },
          width: drawerWidth,
          flexShrink: 0,
          position: 'fixed', 
          top: 0,
          left: 0,
          height: '100vh',
          zIndex: 1200, 
        }}
      >
        <NavbarContent navigate={navigate} theme={theme} showMenuButton={false} />
      </Box>
      <Box
        sx={{
          position: 'fixed',
          top: 16,
          left: 16,
          zIndex: 1400,
          display: { xs: 'block', md: 'none' }
        }}
      >
        {!mobileOpen && (
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={() => setMobileOpen(true)}
          >
            <MenuIcon />
          </IconButton>
        )}
      </Box>
    </>
  );
};


