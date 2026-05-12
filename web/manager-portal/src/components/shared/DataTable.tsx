import { useRef, useCallback } from 'react';
import Box from '@mui/material/Box';
import { AgGridReact } from 'ag-grid-react';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-alpine.css';
import { PAGINATION } from '../../utils/constants';

interface DataTableProps {
  rowData: unknown[];
  columnDefs: unknown[];
  defaultColDef?: Record<string, unknown>;
  onRowClick?: (data: unknown) => void;
  pagination?: boolean;
  pageSize?: number;
  loading?: boolean;
  height?: string | number;
  frameworkComponents?: Record<string, unknown>;
}

export default function DataTable({
  rowData,
  columnDefs,
  defaultColDef,
  onRowClick,
  pagination = true,
  pageSize = PAGINATION.pageSize,
  loading = false,
  height = '100%',
}: DataTableProps) {
  const gridRef = useRef<AgGridReact>(null);

  const onGridReady = useCallback(() => {
    gridRef.current?.api?.sizeColumnsToFit();
  }, []);

  const handleExportCsv = useCallback(() => {
    gridRef.current?.api?.exportDataAsCsv();
  }, []);

  const defaultColumnDef = {
    sortable: true,
    filter: true,
    resizable: true,
    suppressMenu: true,
    cellStyle: { display: 'flex', alignItems: 'center' },
    ...defaultColDef,
  };

  return (
    <Box
      className="ag-theme-alpine"
      sx={{
        width: '100%',
        height: height,
        '& .ag-header-cell-text': { fontWeight: 600 },
      }}
    >
      <AgGridReact
        ref={gridRef}
        rowData={rowData}
        columnDefs={columnDefs}
        defaultColDef={defaultColumnDef}
        onGridReady={onGridReady}
        onRowClicked={onRowClick ? (e) => onRowClick(e.data) : undefined}
        pagination={pagination}
        paginationPageSize={pageSize}
        paginationPageSizeSelector={PAGINATION.pageSizeOptions}
        loading={loading}
        animateRows
        enableCellTextSelection
        ensureDomOrder
        domLayout="autoHeight"
        suppressCellFocus
      />
    </Box>
  );
}

export { DataTable };
