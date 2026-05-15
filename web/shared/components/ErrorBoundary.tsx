import React, { Component, ErrorInfo, ReactNode } from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export default class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('ErrorBoundary caught:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <Box
          sx={{
            minHeight: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            bgcolor: 'background.default',
            p: 2,
          }}
        >
          <Card sx={{ maxWidth: 500, width: '100%' }}>
            <CardContent sx={{ p: 4, textAlign: 'center' }}>
              <Typography variant="h5" color="error" fontWeight={700} gutterBottom>
                Something went wrong
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                An unexpected error occurred. Please try refreshing the page.
              </Typography>
              {this.state.error && (
                <Typography
                  variant="caption"
                  sx={{
                    display: 'block',
                    mb: 3,
                    p: 2,
                    bgcolor: 'grey.100',
                    borderRadius: 1,
                    fontFamily: 'monospace',
                    textAlign: 'left',
                    overflow: 'auto',
                  }}
                >
                  {this.state.error.message}
                </Typography>
              )}
              <Button
                variant="contained"
                onClick={() => window.location.reload()}
              >
                Refresh Page
              </Button>
            </CardContent>
          </Card>
        </Box>
      );
    }

    return this.props.children;
  }
}
