import {
  Button,
  LinearProgress,
  MenuItem,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import { Link as RouterLink } from "react-router-dom";
import api from "@/services/api";
import { useAuth } from "@/context/AuthContext";

type LibItem = {
  id: number;
  paperId: number;
  paperTitle: string;
  authors: string;
  publicationYear: number;
  domain: string;
  status: "NOT_STARTED" | "IN_PROGRESS" | "COMPLETED";
  progressPercent: number;
  savedAt: string;
};

export function LibraryPage() {
  const { user } = useAuth();
  const [rows, setRows] = useState<LibItem[]>([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    if (!user) return;
    const { data } = await api.get<LibItem[]>(`/library/${user.userId}`);
    setRows(data);
  }

  useEffect(() => {
    if (!user) return;
    void (async () => {
      try {
        await load();
      } finally {
        setLoading(false);
      }
    })();
  }, [user]);

  async function updateProgress(row: LibItem, status: LibItem["status"], progressPercent: number) {
    await api.put("/library/progress", {
      libraryItemId: row.id,
      status,
      progressPercent,
    });
    await load();
  }

  if (!user) return null;
  if (loading) return <LinearProgress />;

  return (
    <Stack spacing={2}>
      <Typography variant="h4">My library</Typography>
      <Paper sx={{ overflow: "auto" }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Paper</TableCell>
              <TableCell>Status</TableCell>
              <TableCell align="right">Progress</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((r) => (
              <TableRow key={r.id}>
                <TableCell>
                  <Typography fontWeight={600}>{r.paperTitle}</Typography>
                  <Typography variant="caption" color="text.secondary">
                    {r.authors} · {r.domain}
                  </Typography>
                </TableCell>
                <TableCell sx={{ minWidth: 160 }}>
                  <TextField
                    select
                    size="small"
                    fullWidth
                    value={r.status}
                    onChange={(e) =>
                      void updateProgress(r, e.target.value as LibItem["status"], r.progressPercent)
                    }
                  >
                    <MenuItem value="NOT_STARTED">Not started</MenuItem>
                    <MenuItem value="IN_PROGRESS">In progress</MenuItem>
                    <MenuItem value="COMPLETED">Completed</MenuItem>
                  </TextField>
                </TableCell>
                <TableCell align="right" sx={{ minWidth: 120 }}>
                  <TextField
                    size="small"
                    type="number"
                    value={r.progressPercent}
                    inputProps={{ min: 0, max: 100 }}
                    onChange={(e) =>
                      void updateProgress(r, r.status, Math.min(100, Math.max(0, Number(e.target.value))))
                    }
                  />
                </TableCell>
                <TableCell align="right">
                  <Button component={RouterLink} to={`/papers/${r.paperId}`} size="small" variant="outlined">
                    Open
                  </Button>
                </TableCell>
              </TableRow>
            ))}
            {rows.length === 0 ? (
              <TableRow>
                <TableCell colSpan={4}>
                  <Typography color="text.secondary">No saved papers yet.</Typography>
                </TableCell>
              </TableRow>
            ) : null}
          </TableBody>
        </Table>
      </Paper>
    </Stack>
  );
}
