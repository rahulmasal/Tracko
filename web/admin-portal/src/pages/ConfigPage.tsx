import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import { useState } from 'react';
import AttendanceConfig from '../components/config/AttendanceConfig';
import SecurityPolicyConfig from '../components/config/SecurityPolicyConfig';
import ScoreFormulaConfig from '../components/config/ScoreFormulaConfig';
import QuotaConfig from '../components/config/QuotaConfig';
import LeavePolicyConfig from '../components/config/LeavePolicyConfig';

export default function ConfigPage() {
  const [tab, setTab] = useState(0);
  return (
    <Box>
      <Typography variant="h5" fontWeight={600} sx={{ mb: 3 }}>Configuration</Typography>
      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 3 }}>
        <Tab label="Attendance" />
        <Tab label="Security" />
        <Tab label="Score Formula" />
        <Tab label="Quotation" />
        <Tab label="Leave Policy" />
      </Tabs>
      {tab === 0 && <AttendanceConfig />}
      {tab === 1 && <SecurityPolicyConfig />}
      {tab === 2 && <ScoreFormulaConfig />}
      {tab === 3 && <QuotaConfig />}
      {tab === 4 && <LeavePolicyConfig />}
    </Box>
  );
}
