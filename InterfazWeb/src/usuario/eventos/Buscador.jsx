import React from "react";
import { Box } from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import FilterListIcon from "@mui/icons-material/FilterList";
import { TextFieldCustom } from "../../components/TextField";
import { SelectorCustom } from "../../components/Selector";
import { ButtonCustom } from "../../components/Button";

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
      <Box
        sx={{
          flex: 2,
          display: 'flex',
          flexDirection: { xs: 'column', sm: 'row' },
          alignItems: { xs: 'stretch', sm: 'center' },
          gap: { xs: 2, sm: 1 },
          width: '100%',
        }}
      >
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
            minWidth: { xs: '100%', sm: 200 },
            maxWidth: { xs: '100%', sm: 240 },
            width: { xs: '100%', sm: 'auto' },
          }}
        />
        <SelectorCustom
          placeholder="Todos los estados"
          opciones={estados}
          value={estadoSeleccionado}
          onChange={onEstadoChange}
          sx={{
            minWidth: { xs: '100%', sm: 200 },
            maxWidth: { xs: '100%', sm: 240 },
            width: { xs: '100%', sm: 'auto' },
          }}
        />
        <ButtonCustom
          bgColor="#181828"
          color="#fff"
          hoverBgColor="#23234a"
          hoverColor="#fff"
          startIcon={<FilterListIcon />}
          onClick={onFiltroAvanzado}
          sx={{
            width: { xs: '100%', sm: 'auto' },
            mt: { xs: 1, sm: 0 }
          }}
        >
          Filtros avanzados
        </ButtonCustom>
      </Box>
    </Box>
  );
};

