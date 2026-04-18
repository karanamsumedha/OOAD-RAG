import {
  Box,
  Button,
  Chip,
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

export type Paper = {
  id: number;
  title: string;
  authors: string;
  publicationYear: number;
  domain: string;
  journal: string | null;
  doi: string | null;
  url: string | null;
  abstractText: string | null;
  keywords: string;
};

export function SearchPage() {
  const [q, setQ] = useState("");
  const [domain, setDomain] = useState("");
  const [year, setYear] = useState("");
  const [author, setAuthor] = useState("");
  const [rows, setRows] = useState<Paper[]>([]);
  const [loading, setLoading] = useState(false);

  async function run() {
    setLoading(true);
    try {
      const { data } = await api.get<Paper[]>("/papers", {
        params: {
          q: q || undefined,
          domain: domain || undefined,
          year: year ? Number(year) : undefined,
          author: author || undefined,
        },
      });
      setRows(data);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void run();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <Stack spacing={3}>
      <Box>
        <Typography variant="h4" gutterBottom>
          Search papers
        </Typography>
        <Typography color="text.secondary">
          Filters combine with AND semantics. Leave fields empty to ignore them.
        </Typography>
      </Box>

      <Paper sx={{ p: 2 }}>
        <Stack direction={{ xs: "column", md: "row" }} spacing={2} flexWrap="wrap">
          <TextField label="Keywords" value={q} onChange={(e) => setQ(e.target.value)} fullWidth sx={{ flex: 2 }} />
          <TextField label="Domain" value={domain} onChange={(e) => setDomain(e.target.value)} sx={{ flex: 1 }} />
          <TextField label="Year" value={year} onChange={(e) => setYear(e.target.value)} type="number" sx={{ width: 120 }} />
          <TextField label="Author contains" value={author} onChange={(e) => setAuthor(e.target.value)} sx={{ flex: 1 }} />
          <Button variant="contained" onClick={() => void run()} disabled={loading} sx={{ minWidth: 140 }}>
            {loading ? "Searching…" : "Search"}
          </Button>
        </Stack>
      </Paper>

      <Paper sx={{ overflow: "auto" }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Title</TableCell>
              <TableCell>Year</TableCell>
              <TableCell>Domain</TableCell>
              <TableCell align="right">Open</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((p) => (
              <TableRow key={p.id} hover>
                <TableCell>
                  <Typography fontWeight={600}>{p.title}</Typography>
                  <Typography variant="caption" color="text.secondary" display="block">
                    {p.authors}
                  </Typography>
                  <Box sx={{ mt: 0.5, display: "flex", gap: 0.5, flexWrap: "wrap" }}>
                    {p.keywords.split(",").slice(0, 4).map((k) => (
                      <Chip key={k + p.id} size="small" label={k.trim()} variant="outlined" />
                    ))}
                  </Box>
                </TableCell>
                <TableCell>{p.publicationYear}</TableCell>
                <TableCell>{p.domain}</TableCell>
                <TableCell align="right">
                  <Button component={RouterLink} to={`/papers/${p.id}`} size="small" variant="outlined">
                    Details
                  </Button>
                </TableCell>
              </TableRow>
            ))}
            {rows.length === 0 ? (
              <TableRow>
                <TableCell colSpan={4}>
                  <Typography color="text.secondary">No results. Try broader keywords.</Typography>
                </TableCell>
              </TableRow>
            ) : null}
          </TableBody>
        </Table>
      </Paper>
    </Stack>
  );
}
