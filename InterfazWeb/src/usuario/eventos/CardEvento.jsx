import React from 'react';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import IconButton from '@mui/material/IconButton';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import VisibilityIcon from '@mui/icons-material/Visibility';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import PlaceIcon from '@mui/icons-material/Place';
import PeopleIcon from '@mui/icons-material/People';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

export const CardEvento = ({ evento }) => {
  // Formatear fecha y hora
  let fecha = evento.fechaInicio;
  let fechaFormateada = '';
  try {
    fechaFormateada = format(new Date(fecha), "d 'de' MMM yyyy · HH:mm", { locale: es });
  } catch {
    fechaFormateada = fecha;
  }

  // Mostrar hasta 2 categorías y un chip "+1" si hay más
  const categorias = evento.categorias || [];
  const maxChips = 2;
  const extraCats = categorias.length - maxChips;

  return (
    <Card sx={{ width: 410, minHeight: 520, display: 'flex', flexDirection: 'column', borderRadius: 3, boxShadow: 2, p: 1 }}>
      <Box sx={{ position: 'relative' }}>
        <CardMedia
          component="img"
          height="170"
          image={evento.imagen}
          alt={evento.titulo}
          sx={{ borderRadius: 2, objectFit: 'cover' }}
        />
        {/* Chips de estado/categoría arriba a la izquierda y derecha */}
        <Box sx={{ position: 'absolute', top: 12, left: 12, display: 'flex', gap: 1 }}>
          {evento.categorias && evento.categorias[0] && (
            <Chip label={evento.categorias[0].toLowerCase()} size="small" color="primary" sx={{ textTransform: 'capitalize', fontWeight: 500 }} />
          )}
        </Box>
        <Box sx={{ position: 'absolute', top: 12, right: 12, display: 'flex', gap: 1 }}>
          {evento.estado && (
            <Chip label={evento.estado} size="small" color="default" sx={{ textTransform: 'capitalize', fontWeight: 500 }} />
          )}
        </Box>
      </Box>
  <CardContent sx={{ pb: 1, flex: 1, display: 'flex', flexDirection: 'column' }}>
        <Typography gutterBottom variant="h6" component="div" sx={{ fontWeight: 600 }}>
          {evento.titulo}
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
          {evento.descripcion}
        </Typography>
        <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 0.5 }}>
          <CalendarMonthIcon fontSize="small" color="action" />
          <Typography variant="body2" color="text.secondary">
            {fechaFormateada}
          </Typography>
        </Stack>
        <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 0.5 }}>
          <PlaceIcon fontSize="small" color="action" />
          <Typography variant="body2" color="text.secondary">
            {evento.lugar}
          </Typography>
        </Stack>
        <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
          <PeopleIcon fontSize="small" color="action" />
          <Typography variant="body2" color="text.secondary">
            {evento.min_participantes} / {evento.max_participantes} asistentes
          </Typography>
        </Stack>
        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mb: 1 }}>
          {categorias.slice(0, maxChips).map((cat, i) => (
            <Chip key={cat} label={cat.toLowerCase()} size="small" variant="outlined" />
          ))}
          {extraCats > 0 && (
            <Chip label={`+${extraCats}`} size="small" variant="outlined" />
          )}
        </Box>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Box />
          <Typography variant="h6" sx={{ fontWeight: 700 }}>
            ${evento.precio}
          </Typography>
        </Box>
        <Stack direction="row" spacing={1} sx={{ mt: 1 }}>
          <Button variant="outlined" size="small" startIcon={<VisibilityIcon />}>
            Ver
          </Button>
          <Button variant="outlined" size="small" startIcon={<EditIcon />}>
            Editar
          </Button>
          <IconButton color="error" size="small">
            <DeleteIcon />
          </IconButton>
        </Stack>
      </CardContent>
    </Card>
  );
};
