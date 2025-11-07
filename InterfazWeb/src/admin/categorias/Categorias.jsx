import { Box, Typography, Paper, Button, TextField, List, ListItem, ListItemText, IconButton, Alert } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { useState, useEffect } from 'react';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { useGetEvents } from '../../hooks/useGetEvents';
import { useAdmin } from '../../hooks/useAdmin';

export const Categorias = () => {
    const theme = useTheme();
    const { getCategories } = useGetEvents();
    const { addCategory, deleteCategory, loading: adminLoading } = useAdmin();
    const [categories, setCategories] = useState([]);
    const [newCategory, setNewCategory] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Cargar categorías al montar el componente
    useEffect(() => {
        loadCategories();
    }, []);

    const loadCategories = async () => {
        try {
            setLoading(true);
            const data = await getCategories({ limit: 1000 });
            setCategories(data);
        } catch (err) {
            setError('Error al cargar las categorías');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleAddCategory = async () => {
        if (!newCategory.trim()) {
            setError('El nombre de la categoría no puede estar vacío');
            return;
        }

        try {
            setLoading(true);
            setError('');
            setSuccess('');
            await addCategory(newCategory.trim());
            setSuccess(`Categoría "${newCategory}" agregada exitosamente`);
            setNewCategory('');
            await loadCategories();
        } catch (err) {
            setError(err.response?.data?.error || 'Error al agregar la categoría');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteCategory = async (categoryTitle) => {
        const confirmed = window.confirm(`¿Estás seguro de que quieres eliminar la categoría "${categoryTitle}"?`);
        if (!confirmed) return;

        try {
            setLoading(true);
            setError('');
            setSuccess('');
            await deleteCategory(categoryTitle);
            setSuccess(`Categoría "${categoryTitle}" eliminada exitosamente`);
            await loadCategories();
        } catch (err) {
            setError(err.response?.data?.error || 'Error al eliminar la categoría');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box>
            <Typography variant="h4" fontWeight={700} mb={3}>
                Gestión de Categorías
            </Typography>

            {/* Mensajes de error y éxito */}
            {error && (
                <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
                    {error}
                </Alert>
            )}
            {success && (
                <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>
                    {success}
                </Alert>
            )}

            {/* Formulario para agregar nueva categoría */}
            <Paper sx={{ p: 3, mb: 3 }}>
                <Typography variant="h6" fontWeight={600} mb={2}>
                    Agregar Nueva Categoría
                </Typography>
                <Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
                    <TextField
                        fullWidth
                        label="Nombre de la categoría"
                        value={newCategory}
                        onChange={(e) => setNewCategory(e.target.value)}
                        onKeyPress={(e) => {
                            if (e.key === 'Enter') {
                                handleAddCategory();
                            }
                        }}
                        disabled={loading}
                        placeholder="Ej: Tecnología, Música, Deportes..."
                    />
                    <Button
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={handleAddCategory}
                        disabled={loading || !newCategory.trim()}
                        sx={{
                            minWidth: 120,
                            height: 56,
                            bgcolor: theme.palette.primary.main,
                            '&:hover': {
                                bgcolor: theme.palette.primary.dark
                            }
                        }}
                    >
                        Agregar
                    </Button>
                </Box>
            </Paper>

            {/* Lista de categorías */}
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" fontWeight={600} mb={2}>
                    Categorías Existentes ({categories.length})
                </Typography>
                {loading && categories.length === 0 ? (
                    <Typography color="text.secondary">Cargando categorías...</Typography>
                ) : categories.length === 0 ? (
                    <Typography color="text.secondary">No hay categorías registradas</Typography>
                ) : (
                    <List>
                        {categories.map((category) => (
                            <ListItem
                                key={category.title}
                                sx={{
                                    borderRadius: 1,
                                    mb: 1,
                                    bgcolor: theme.palette.background.default,
                                    '&:hover': {
                                        bgcolor: theme.palette.action.hover
                                    }
                                }}
                                secondaryAction={
                                    <IconButton 
                                        edge="end" 
                                        aria-label="delete"
                                        onClick={() => handleDeleteCategory(category.title)}
                                        disabled={loading}
                                        sx={{
                                            color: theme.palette.error.main,
                                            '&:hover': {
                                                bgcolor: theme.palette.error.light + '20'
                                            }
                                        }}
                                    >
                                        <DeleteIcon />
                                    </IconButton>
                                }
                            >
                                <ListItemText
                                    primary={category.title}
                                    primaryTypographyProps={{
                                        fontWeight: 500,
                                        fontSize: '1rem'
                                    }}
                                />
                            </ListItem>
                        ))}
                    </List>
                )}
            </Paper>
        </Box>
    );
};
