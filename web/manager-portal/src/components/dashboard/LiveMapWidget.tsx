import { useEffect } from 'react';
import Box from '@mui/material/Box';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import { MAP_CONFIG } from '../../utils/constants';
import { formatTime } from '../../utils/helpers';

interface LiveLocation {
  userId: number;
  userName: string;
  latitude: number;
  longitude: number;
  status: string;
  batteryLevel: number;
  lastPing: string;
}

interface LiveMapWidgetProps {
  locations: LiveLocation[];
}

const getMarkerIcon = (status: string) => {
  const colors: Record<string, string> = {
    'on-site': '#22c55e',
    traveling: '#3b82f6',
    offline: '#6b7280',
  };
  const color = colors[status] || '#6b7280';
  return L.divIcon({
    className: 'custom-marker',
    html: `<div style="width:12px;height:12px;background:${color};border:2px solid white;border-radius:50%;box-shadow:0 1px 3px rgba(0,0,0,0.3);"></div>`,
    iconSize: [12, 12],
    iconAnchor: [6, 6],
  });
};

export default function LiveMapWidget({ locations }: LiveMapWidgetProps) {
  useEffect(() => {
    // Fix leaflet icon issue with bundlers
    delete (L.Icon.Default.prototype as Record<string, unknown>)._getIconUrl;
    L.Icon.Default.mergeOptions({
      iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
    });
  }, []);

  if (locations.length === 0) {
    return (
      <Box sx={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        No location data available
      </Box>
    );
  }

  return (
    <MapContainer
      center={MAP_CONFIG.defaultCenter}
      zoom={MAP_CONFIG.defaultZoom}
      style={{ height: '100%', width: '100%' }}
      zoomControl={false}
    >
      <TileLayer url={MAP_CONFIG.tileUrl} attribution={MAP_CONFIG.attribution} />
      {locations.map((loc) => (
        <Marker
          key={loc.userId}
          position={[loc.latitude, loc.longitude]}
          icon={getMarkerIcon(loc.status)}
        >
          <Popup>
            <strong>{loc.userName}</strong><br />
            Status: {loc.status}<br />
            Battery: {loc.batteryLevel}%<br />
            Last ping: {formatTime(loc.lastPing)}
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  );
}
