import React from "react";
import { Box } from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import FilterListIcon from "@mui/icons-material/FilterList";
import { TextFieldCustom } from "../../components/TextField";
import { SelectorCustom } from "../../components/Selector";
import { ButtonCustom } from "../../components/Button";
import { useTheme } from '@mui/material/styles';
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
   const theme = useTheme()
  const inputHeight = 47; // altura estándar más grande

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
            sx={{
              height: inputHeight,
              '& .MuiOutlinedInput-root': {
                height: inputHeight,
                minHeight: inputHeight,
                padding: 0,
                alignItems: 'center'
              },
              '& .MuiOutlinedInput-input': {
                height: inputHeight,
                minHeight: inputHeight,
                padding: '0 14px',
                display: 'flex',
                alignItems: 'center'
              }
            }}
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
            height: inputHeight,
            '& .MuiOutlinedInput-root': {
              height: inputHeight,
              minHeight: inputHeight,
              padding: 0,
              alignItems: 'center'
            },
            '& .MuiSelect-select': {
              height: inputHeight,
              minHeight: inputHeight,
              display: 'flex',
              alignItems: 'center',
              padding: '0 14px'
            }
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
            height: inputHeight,
            '& .MuiOutlinedInput-root': {
              height: inputHeight,
              minHeight: inputHeight,
              padding: 0,
              alignItems: 'center'
            },
            '& .MuiSelect-select': {
              height: inputHeight,
              minHeight: inputHeight,
              display: 'flex',
              alignItems: 'center',
              padding: '0 14px'
            }
          }}
        />
        {/* Botón de filtros avanzados deshabilitado temporalmente
        <ButtonCustom
          bgColor={theme.palette.secondary.main}
          color={theme.palette.secondary.contrastText}
          hoverBgColor={theme.palette.secondary.dark}
          hoverColor={theme.palette.secondary.contrastText}
          startIcon={<FilterListIcon />}
          onClick={onFiltroAvanzado}
          sx={{
            width: { xs: '100%', sm: 'auto' },
            mt: { xs: 1, sm: 0 },
            height: inputHeight,
            minWidth: 0,
            fontWeight: 700,
            fontSize: 16,
            borderRadius: '10px',
            px: 2,
            py: 0,
            display: 'flex',
            alignItems: 'center'
          }}
        >
          Filtros avanzados
        </ButtonCustom>
        */}
      </Box>
    </Box>
  );
};

