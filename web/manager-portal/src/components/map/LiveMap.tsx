import { useState, useEffect, useCallback } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Typography from '@mui/material/Typography';
import Chip from '@mui/material/Chip';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import {
  MapContainer, TileLayer, Marker, Popup, Circle, Polyline, useMap,
} from 'react-leaflet';
import L from 'leaflet';
import api from '../../services/api';
import { MAP_CONFIG } from '../../utils/constants';
import { formatTime } from '../../utils/helpers';

interface Engineer {
  userId: number;
  userName: string;
  latitude: number;
  longitude: number;
  status: string;
  batteryLevel: number;
  lastPing: string;
  accuracy?: number;
}

interface Geofence {
  id: number;
  name: string;
  latitude: number;
  longitude: number;
  radiusMeters: number;
  type: string;
}

const statusColors: Record<string, string> = {
  'on-site': '#22c55e',
  traveling: '#3b82f6',
  offline: '#6b7280',
  break: '#f59e0b',
};

function createMarkerIcon(status: string, isSelected: boolean) {
  const color = statusColors[status] || '#6b7280';
  const size = isSelected ? 18 : 14;
  return L.divIcon({
    className: '',
    html: `<div style="
      width:${size}px;height:${size}px;
      background:${color};
      border:3px solid ${isSelected ? '#fff' : 'rgba(255,255,255,0.8)'};
      border-radius:50%;
      box-shadow:0 2px 6px rgba(0,0,0,0.3);
      transition: all 0.2s;
    "></div>`,
    iconSize: [size, size],
    iconAnchor: [size / 2, size / 2],
  });
}

function MapUpdater({ center }: { center: [number, number] }) {
  const map = useMap();
  useEffect(() => { map.setView(center); }, [center, map]);
  return null;
}

export default function LiveMap() {
  const [engineers, setEngineers] = useState<Engineer[]>([]);
  const [geofences, setGeofences] = useState<Geofence[]>([]);
  const [selectedEngineer, setSelectedEngineer] = useState<Engineer | null>(null);
  const [routePoints, setRoutePoints] = useState<[number, number][]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    try {
      const [locRes, geoRes] = await Promise.all([
        api.get('/tracking/live'),
        api.get('/geofences'),
      ]);
      setEngineers(locRes.data);
      setGeofences(geoRes.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load map data');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 15000);
    return () => clearInterval(interval);
  }, [fetchData]);

  useEffect(() => {
    if (selectedEngineer) {
      api.get('/tracking/route', {
        params: { userId: selectedEngineer.userId, minutes: 60 },
      }).then((res) => {
        const points = (res.data as { latitude: number; longitude: number }[]).map(
          (p) => [p.latitude, p.longitude] as [number, number],
        );
        setRoutePoints(points);
      }).catch(() => setRoutePoints([]));
    } else {
      setRoutePoints([]);
    }
  }, [selectedEngineer]);

  const mapCenter: [number, number] = engineers.length > 0
    ? [engineers[0].latitude, engineers[0].longitude]
    : MAP_CONFIG.defaultCenter;

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}><CircularProgress /></Box>;
  }

  if (error) {
    return <Alert severity="error" sx={{ m: 2 }}>{error}</Alert>;
  }

  return (
    <Box sx={{ display: 'flex', gap: 2, height: 'calc(100vh - 180px)' }}>
      <Card sx={{ flex: 1, overflow: 'hidden', position: 'relative' }}>
        <MapContainer
          center={mapCenter}
          zoom={MAP_CONFIG.defaultZoom}
          style={{ height: '100%', width: '100%' }}
        >
          <TileLayer url={MAP_CONFIG.tileUrl} attribution={MAP_CONFIG.attribution} />
          <MapUpdater center={mapCenter} />

          {geofences.map((gf) => (
            <Circle
              key={gf.id}
              center={[gf.latitude, gf.longitude]}
              radius={gf.radius_meters || gf.radiusMeters}
              pathOptions={{
                color: '#3b82f6',
                fillColor: '#3b82f6',
                fillOpacity: 0.1,
                weight: 2,
              }}
            >
              <Popup>{gf.name}</Popup>
            </Circle>
          ))}

          {engineers.map((eng) => (
            <Marker
              key={eng.userId}
              position={[eng.latitude, eng.longitude]}
              icon={createMarkerIcon(eng.status, selectedEngineer?.userId === eng.userId)}
              eventHandlers={{ click: () => setSelectedEngineer(eng) }}
            >
              <Popup>
                <Box sx={{ minWidth: 180 }}>
                  <Typography variant="subtitle2" fontWeight={600}>{eng.userName}</Typography>
                  <Typography variant="caption" display="block">Status: {eng.status}</Typography>
                  <Typography variant="caption" display="block">Battery: {eng.batteryLevel}%</Typography>
                  <Typography variant="caption" display="block">Last ping: {formatTime(eng.lastPing)}</Typography>
                </Box>
              </Popup>
            </Marker>
          ))}

          {routePoints.length > 1 && (
            <Polyline positions={routePoints} pathOptions={{ color: '#3b82f6', weight: 3, opacity: 0.6 }} />
          )}
        </MapContainer>

        <Box sx={{ position: 'absolute', bottom: 16, left: 16, zIndex: 1000, bgcolor: 'background.paper', p: 1.5, borderRadius: 1, boxShadow: 2 }}>
          <Typography variant="caption" fontWeight={600} gutterBottom>Legend</Typography>
          {Object.entries(statusColors).map(([key, color]) => (
            <Box key={key} sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.5 }}>
              <Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: color }} />
              <Typography variant="caption" sx={{ textTransform: 'capitalize' }}>{key}</Typography>
            </Box>
          ))}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.5 }}>
            <Box sx={{ width: 10, height: 10, borderRadius: '50%', border: '2px dashed #3b82f6' }} />
            <Typography variant="caption">Geofence</Typography>
          </Box>
        </Box>
      </Card>

      {selectedEngineer && (
        <Card sx={{ width: 280, p: 2, overflow: 'auto' }}>
          <Typography variant="subtitle1" fontWeight={600}>{selectedEngineer.userName}</Typography>
          <Box sx={{ mt: 2 }}>
            <Typography variant="caption" color="text.secondary">Status</Typography>
            <Chip
              label={selectedEngineer.status}
              size="small"
              sx={{
                ml: 1,
                bgcolor: `${statusColors[selectedEngineer.status] || '#6b7280'}20`,
                color: statusColors[selectedEngineer.status] || '#6b7280',
                fontWeight: 600,
              }}
            />
          </Box>
          <Box sx={{ mt: 1 }}>
            <Typography variant="caption" color="text.secondary">Battery</Typography>
            <Typography variant="body2">{selectedEngineer.batteryLevel}%</Typography>
          </Box>
          <Box sx={{ mt: 1 }}>
            <Typography variant="caption" color="text.secondary">Last Ping</Typography>
            <Typography variant="body2">{formatTime(selectedEngineer.lastPing)}</Typography>
          </Box>
          {selectedEngineer.accuracy && (
            <Box sx={{ mt: 1 }}>
              <Typography variant="caption" color="text.secondary">Accuracy</Typography>
              <Typography variant="body2">{selectedEngineer.accuracy}m</Typography>
            </Box>
          )}
          <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
            <Chip label={`${engineers.filter((e) => e.status === 'on-site').length} on-site`} size="small" color="success" variant="outlined" />
            <Chip label={`${engineers.filter((e) => e.status === 'traveling').length} traveling`} size="small" color="info" variant="outlined" />
          </Box>
        </Card>
      )}
    </Box>
  );
}
