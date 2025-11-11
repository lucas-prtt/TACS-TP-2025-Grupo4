import React, { useState, useEffect } from 'react';
import { Box, Typography, Card, CardContent, CircularProgress, Alert } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { useAdmin } from '../../hooks/useAdmin';
import { useGetEvents } from '../../hooks/useGetEvents';
import EventIcon from '@mui/icons-material/Event';
import PeopleIcon from '@mui/icons-material/People';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import PercentIcon from '@mui/icons-material/Percent';

export const Estadisticas = () => {
  const theme = useTheme();
  const { getStats, loading: statsLoading, error } = useAdmin();
  const { getEvents, getEventParticipants, loading: eventsLoading } = useGetEvents();
  const [stats, setStats] = useState(null);
  const [periodStats, setPeriodStats] = useState({
    currentMonthEvents: 0,
    currentMonthRegistrations: 0,
    currentYearEvents: 0,
    currentYearRegistrations: 0
  });

  const loading = statsLoading || eventsLoading;

  // Calcular estadísticas de períodos
  const calculatePeriodStats = async () => {
    try {
      const events = await getEvents();
      const now = new Date();
      const currentMonth = now.getMonth();
      const currentYear = now.getFullYear();

      let monthEvents = 0;
      let monthRegistrations = 0;
      let yearEvents = 0;
      let yearRegistrations = 0;

      for (const event of events) {
        const eventDate = new Date(event.startDateTime);
        const eventMonth = eventDate.getMonth();
        const eventYear = eventDate.getFullYear();

        // Contar eventos del año actual
        if (eventYear === currentYear) {
          yearEvents++;
          
          // Obtener inscripciones para este evento
          try {
            const registrations = await getEventParticipants(event.id, { limit: 1000 });
            const registrationsCount = Array.isArray(registrations) ? registrations.length : 0;
            yearRegistrations += registrationsCount;

            // Contar eventos del mes actual
            if (eventMonth === currentMonth) {
              monthEvents++;
              monthRegistrations += registrationsCount;
            }
          } catch (err) {
          }
        }
      }

      setPeriodStats({
        currentMonthEvents: monthEvents,
        currentMonthRegistrations: monthRegistrations,
        currentYearEvents: yearEvents,
        currentYearRegistrations: yearRegistrations
      });
    } catch (err) {
    }
  };

  useEffect(() => {
    const loadAllStats = async () => {
      try {
        const data = await getStats();
        setStats(data);
        await calculatePeriodStats();
      } catch (err) {
      }
    };

    loadAllStats();
  }, []);

  // Formatear porcentaje
  const formatPercentage = (value) => {
    return `${(value * 100).toFixed(1)}%`;
  };

  // Componente de tarjeta de estadística
  const StatCard = ({ title, value, icon: Icon, color, subtitle }) => (
    <Card
      sx={{
        height: '100%',
        background: `linear-gradient(135deg, ${color}15 0%, ${color}05 100%)`,
        border: `2px solid ${color}30`,
        borderRadius: 3,
        transition: 'transform 0.2s, box-shadow 0.2s',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: `0 8px 24px ${color}30`
        }
      }}
    >
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', mb: 2 }}>
          <Box>
            <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500, mb: 0.5 }}>
              {title}
            </Typography>
            <Typography variant="h3" sx={{ fontWeight: 700, color: color }}>
              {value}
            </Typography>
            {subtitle && (
              <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5, display: 'block' }}>
                {subtitle}
              </Typography>
            )}
          </Box>
          <Box
            sx={{
              bgcolor: color,
              borderRadius: 2,
              p: 1.5,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            <Icon sx={{ fontSize: 32, color: '#fff' }} />
          </Box>
        </Box>
      </CardContent>
    </Card>
  );

  return (
    <Box>
      <Typography variant="h4" fontWeight={700} sx={{ mb: 1 }}>
        Estadísticas del Sistema
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Visualizá métricas clave sobre eventos, inscripciones y conversiones
      </Typography>

        {/* Loading */}
        {loading && !stats && (
          <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 400 }}>
            <CircularProgress size={48} />
          </Box>
        )}

        {/* Error */}
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

      {/* Estadísticas */}
      {stats && (
        <>
          {/* Tarjetas principales */}
          <Box sx={{ display: 'flex', gap: 2, width: '100%', flexWrap: { xs: 'wrap', lg: 'nowrap' }, mb: 4 }}>
              <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 8px)', lg: '1 1 0' }, minWidth: { xs: 0, lg: 0 } }}>
                <StatCard
                  title="Total de Eventos"
                  value={stats.eventsCount.toLocaleString()}
                  icon={EventIcon}
                  color="#8B5CF6"
                  subtitle="Eventos creados en el sistema"
                />
              </Box>
              <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 8px)', lg: '1 1 0' }, minWidth: { xs: 0, lg: 0 } }}>
                <StatCard
                  title="Inscripciones Totales"
                  value={stats.registrationsCount.toLocaleString()}
                  icon={PeopleIcon}
                  color="#10B981"
                  subtitle="Participantes registrados"
                />
              </Box>
              <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 8px)', lg: '1 1 0' }, minWidth: { xs: 0, lg: 0 } }}>
                <StatCard
                  title="Promociones desde Waitlist"
                  value={stats.waitlistPromotions.toLocaleString()}
                  icon={TrendingUpIcon}
                  color="#3B82F6"
                  subtitle="Usuarios promovidos a confirmados"
                />
              </Box>
              <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 8px)', lg: '1 1 0' }, minWidth: { xs: 0, lg: 0 } }}>
                <StatCard
                  title="Tasa de Conversión"
                  value={formatPercentage(stats.waitlistConversionRate)}
                  icon={PercentIcon}
                  color="#F59E0B"
                  subtitle="Waitlist → Confirmados"
                />
              </Box>
          </Box>

          {/* Tarjetas secundarias con análisis */}
          <Box sx={{ display: 'flex', gap: 3, width: '100%' }}>
              {/* Análisis de eventos */}
              <Box sx={{ flex: 1, minWidth: 0 }}>
                <Card
                  sx={{
                    borderRadius: 3,
                    boxShadow: '0 2px 8px rgba(0,0,0,0.07)',
                    border: '1px solid #e0e0e0',
                    height: '100%'
                  }}
                >
                  <CardContent>
                    <Typography variant="h6" fontWeight={600} sx={{ mb: 2 }}>
                      📊 Análisis de Eventos
                    </Typography>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                      <Box>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                          Promedio de inscripciones por evento
                        </Typography>
                        <Typography variant="h5" fontWeight={700} color="primary">
                          {stats.eventsCount > 0 
                            ? (stats.registrationsCount / stats.eventsCount).toFixed(1)
                            : '0'}
                        </Typography>
                      </Box>
                      <Box>
                        <Typography variant="body2" color="text.secondary">
                          {stats.eventsCount === 0 
                            ? 'No hay eventos creados aún'
                            : stats.eventsCount === 1
                            ? 'Hay 1 evento en el sistema'
                            : `Hay ${stats.eventsCount} eventos en el sistema`}
                        </Typography>
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Box>

              {/* Estadísticas del mes actual */}
              <Box sx={{ flex: 1, minWidth: 0 }}>
                <Card
                  sx={{
                    borderRadius: 3,
                    boxShadow: '0 2px 8px rgba(0,0,0,0.07)',
                    border: '1px solid #e0e0e0',
                    height: '100%'
                  }}
                >
                  <CardContent>
                    <Typography variant="h6" fontWeight={600} sx={{ mb: 2 }}>
                      📅 Mes Actual
                    </Typography>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                      <Box>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                          Eventos creados
                        </Typography>
                        <Typography variant="h5" fontWeight={700} color="primary">
                          {periodStats.currentMonthEvents.toLocaleString()}
                        </Typography>
                      </Box>
                      <Box>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                          Inscripciones registradas
                        </Typography>
                        <Typography variant="h5" fontWeight={700} color="success.main">
                          {periodStats.currentMonthRegistrations.toLocaleString()}
                        </Typography>
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Box>

              {/* Estadísticas del año actual */}
              <Box sx={{ flex: 1, minWidth: 0 }}>
                <Card
                  sx={{
                    borderRadius: 3,
                    boxShadow: '0 2px 8px rgba(0,0,0,0.07)',
                    border: '1px solid #e0e0e0',
                    height: '100%'
                  }}
                >
                  <CardContent>
                    <Typography variant="h6" fontWeight={600} sx={{ mb: 2 }}>
                      📆 Año Actual
                    </Typography>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                      <Box>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                          Eventos creados
                        </Typography>
                        <Typography variant="h5" fontWeight={700} color="primary">
                          {periodStats.currentYearEvents.toLocaleString()}
                        </Typography>
                      </Box>
                      <Box>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                          Inscripciones registradas
                        </Typography>
                        <Typography variant="h5" fontWeight={700} color="success.main">
                          {periodStats.currentYearRegistrations.toLocaleString()}
                        </Typography>
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Box>
          </Box>
          </>
        )}
    </Box>
  );
};
