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

// Definí los roles así:
const isOrganizador = false;
const isAdmin = false;
const isUser = true;

// Definí los ítems del menú y los roles que pueden verlos
const NAV_ITEMS = [
  {
    key: "dashboard",
    label: "Dashboard",
    icon: <HomeIcon />,
    path: "/dashboard",
    show: isUser || isOrganizador || isAdmin
  },
  {
    key: "calendario",
    label: "Calendario",
    icon: <CalendarTodayIcon />,
    path: "/calendario",
    show: isUser || isOrganizador || isAdmin
  },
  {
    key: "eventos",
    label: "Eventos",
    icon: <ListAltIcon />,
    path: "/eventos",
    show: isUser || isOrganizador || isAdmin
  },
  {
    key: "crearEventos",
    label: "Crear Evento",
    icon: <AddIcon />,
    path: "/crearEventos",
    show: isOrganizador || isAdmin
  },
  {
    key: "analiticas",
    label: "Analíticas",
    icon: <BarChartIcon />,
    path: "/analiticas",
    show: isAdmin
  },
  {
    key: "asistentes",
    label: "Asistentes",
    icon: <PeopleIcon />,
    path: "/asistentes",
    show: isOrganizador || isAdmin
  },
  {
    key: "configuracion",
    label: "Configuración",
    icon: <SettingsIcon />,
    path: "/configuracion",
    show: isAdmin
  }
];

const NavbarContent = ({ navigate, theme, onClick, showMenuButton }) => (
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
      {NAV_ITEMS.filter(item => item.show).map(item => (
        <ListItem button key={item.key} onClick={() => { navigate(item.path); onClick && onClick(); }}>
          <ListItemIcon>
            {React.cloneElement(item.icon, { sx: { color: theme.palette.icon.primary } })}
          </ListItemIcon>
          <ListItemText primary={item.label} />
        </ListItem>
      ))}
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


