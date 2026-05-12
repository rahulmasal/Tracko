import { useRef, useCallback } from 'react';
import Box from '@mui/material/Box';
import { AgGridReact } from 'ag-grid-react';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-alpine.css';

interface DataTableProps {
  rowData: unknown[];
  columnDefs: unknown[];
  defaultColDef?: Record<string, unknown>;
  onRowClick?: (data: unknown) => void;
  pagination?: boolean;
  pageSize?: number;
  loading?: boolean;
  height?: string | number;
}

export default function DataTable({
  rowData, columnDefs, defaultColDef, onRowClick,
  pagination = true, pageSize = 20, loading = false, height = '100%',
}: DataTableProps) {
  const gridRef = useRef<AgGridReact>(null);

  const defaultColumnDef = {
    sortable: true,
    filter: true,
    resizable: true,
    suppressMenu: true,
    cellStyle: { display: 'flex', alignItems: 'center' },
    ...defaultColDef,
  };

  return (
    <Box className="ag-theme-alpine" sx={{ width: '100%', height }}>
      <AgGridReact
        ref={gridRef}
        rowData={rowData}
        columnDefs={columnDefs}
        defaultColDef={defaultColumnDef}
        onRowClicked={onRowClick ? (e) => onRowClick(e.data) : undefined}
        pagination={pagination}
        paginationPageSize={pageSize}
        loading={loading}
        animateRows
        domLayout="autoHeight"
        suppressCellFocus
      />
    </Box>
  );
}
