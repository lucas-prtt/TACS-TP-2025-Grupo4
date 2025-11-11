import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
  CircularProgress
} from '@mui/material';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';

/**
 * Componente de diálogo de confirmación reutilizable
 * 
 * @param {boolean} open - Controla si el diálogo está abierto
 * @param {function} onClose - Función para cerrar el diálogo
 * @param {function} onConfirm - Función a ejecutar cuando se confirma
 * @param {string} title - Título del diálogo
 * @param {string} message - Mensaje del diálogo (puede contener HTML)
 * @param {string} confirmText - Texto del botón de confirmación (default: "Confirmar")
 * @param {string} cancelText - Texto del botón de cancelación (default: "Cancelar")
 * @param {boolean} loading - Indica si se está procesando la acción
 * @param {string} loadingText - Texto a mostrar mientras se procesa (default: "Procesando...")
 * @param {string} type - Tipo de diálogo: 'warning', 'error', 'success', 'info' (default: 'info')
 */
export const ConfirmDialog = ({
  open,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = 'Confirmar',
  cancelText = 'Cancelar',
  loading = false,
  loadingText = 'Procesando...',
  type = 'info'
}) => {
  // Configuración de iconos y colores según el tipo
  const getTypeConfig = () => {
    switch (type) {
      case 'warning':
        return {
          icon: <WarningAmberIcon sx={{ color: '#f57c00' }} />,
          color: '#f57c00',
          hoverColor: '#e65100'
        };
      case 'error':
        return {
          icon: <ErrorOutlineIcon sx={{ color: '#d32f2f' }} />,
          color: '#d32f2f',
          hoverColor: '#c62828'
        };
      case 'success':
        return {
          icon: <CheckCircleOutlineIcon sx={{ color: '#388e3c' }} />,
          color: '#388e3c',
          hoverColor: '#2e7d32'
        };
      case 'info':
      default:
        return {
          icon: <InfoOutlinedIcon sx={{ color: '#1976d2' }} />,
          color: '#1976d2',
          hoverColor: '#1565c0'
        };
    }
  };

  const config = getTypeConfig();

  return (
    <Dialog
      open={open}
      onClose={loading ? undefined : onClose}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        {config.icon}
        {title}
      </DialogTitle>
      <DialogContent>
        <DialogContentText
          dangerouslySetInnerHTML={{ __html: message }}
        />
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        {cancelText && (
          <Button
            onClick={onClose}
            disabled={loading}
            sx={{ 
              color: '#666',
              '&:hover': { backgroundColor: '#f5f5f5' }
            }}
          >
            {cancelText}
          </Button>
        )}
        <Button
          onClick={onConfirm}
          variant="contained"
          disabled={loading}
          sx={{
            backgroundColor: config.color,
            '&:hover': { backgroundColor: config.hoverColor },
            '&:disabled': {
              backgroundColor: '#ccc'
            }
          }}
        >
          {loading ? (
            <>
              <CircularProgress size={16} color="inherit" sx={{ mr: 1 }} />
              {loadingText}
            </>
          ) : (
            confirmText
          )}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
