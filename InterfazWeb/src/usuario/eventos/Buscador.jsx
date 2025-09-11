import React from "react";
import { Box, Button } from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import FilterListIcon from "@mui/icons-material/FilterList";
import { TextFieldCustom } from "../../components/TextField";
import { SelectorCustom } from "../../components/Selector";

export const Buscador = ({
  categorias = [],
  estados = [],
  categoriaSeleccionada = "",
  estadoSeleccionado = "",
  onCategoriaChange,
  onEstadoChange,
  searchValue = "",
  onSearchChange,
  onFiltroAvanzado,
}) => {
  return (
    <Box
      sx={{
        width: '100%',
        bgcolor: '#fff',
        borderRadius: 3,
        boxShadow: '0 2px 8px rgba(0,0,0,0.07)',
        p: 2,
        mb: 3,
        display: 'flex',
        flexDirection: { xs: 'column', sm: 'row' },
        alignItems: { xs: 'stretch', sm: 'center' },
        gap: 2,
        border: '1px solid #eee'
      }}
    >
      <Box sx={{ flex: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
        <Box sx={{ flex: 1, minWidth: 180 }}>
          <TextFieldCustom
            placeholder="Buscar eventos..."
            value={searchValue}
            onChange={onSearchChange}
            
          />
        </Box>
        <SelectorCustom
          placeholder="Todas las categorías"
          opciones={categorias}
          value={categoriaSeleccionada}
          onChange={onCategoriaChange}
          sx={{
            minWidth: 200,
            maxWidth: 240,
          }}
        />
        <SelectorCustom
          placeholder="Todos los estados"
          opciones={estados}
          value={estadoSeleccionado}
          onChange={onEstadoChange}
          sx={{
            minWidth: 200,
            maxWidth: 240,
          }}
        />
        <Button
          variant="contained"
          sx={{
            background: '#181828',
            color: '#fff',
            borderRadius: 2,
            px: 3,
            py: 1.2,
            fontWeight: 600,
            fontSize: 15,
            textTransform: 'none',
            boxShadow: 'none',
            '&:hover': { background: '#23234a' }
          }}
          startIcon={<FilterListIcon />}
          onClick={onFiltroAvanzado}
        >
          Filtros avanzados
        </Button>
      </Box>
    </Box>
  );
};

