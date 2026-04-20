import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
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
import { useEffect, useMemo, useState } from "react";
import api from "@/services/api";

type PaperDto = {
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

type PagedResponse<T> = {
  items: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};

type FormState = {
  title: string;
  authors: string;
  publicationYear: string;
  domain: string;
  journal: string;
  doi: string;
  url: string;
  abstractText: string;
  keywords: string;
};

const emptyForm: FormState = {
  title: "",
  authors: "",
  publicationYear: "",
  domain: "",
  journal: "",
  doi: "",
  url: "",
  abstractText: "",
  keywords: "",
};

export function CuratorPapersPage() {
  const [rows, setRows] = useState<PaperDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<PaperDto | null>(null);
  const [form, setForm] = useState<FormState>(emptyForm);
  const [query, setQuery] = useState("");

  const title = useMemo(() => (editing ? "Update paper metadata" : "Add new paper"), [editing]);

  async function load() {
    setLoading(true);
    setError(null);
    try {
      const { data } = await api.get<PagedResponse<PaperDto>>("/papers", {
        params: { q: query || undefined, page: 0, size: 50 },
      });
      setRows(data.items);
    } catch {
      setError("Could not load papers.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function openAdd() {
    setEditing(null);
    setForm(emptyForm);
    setOpen(true);
  }

  function openEdit(p: PaperDto) {
    setEditing(p);
    setForm({
      title: p.title,
      authors: p.authors,
      publicationYear: String(p.publicationYear),
      domain: p.domain,
      journal: p.journal ?? "",
      doi: p.doi ?? "",
      url: p.url ?? "",
      abstractText: p.abstractText ?? "",
      keywords: p.keywords,
    });
    setOpen(true);
  }

  async function save() {
    const payload = {
      title: form.title.trim(),
      authors: form.authors.trim(),
      publicationYear: Number(form.publicationYear),
      domain: form.domain.trim(),
      journal: form.journal.trim() || null,
      doi: form.doi.trim() || null,
      url: form.url.trim() || null,
      abstractText: form.abstractText.trim() || null,
      keywords: form.keywords.trim(),
    };
    try {
      if (editing) {
        await api.put(`/papers/${editing.id}`, payload);
      } else {
        await api.post("/papers", payload);
      }
      setOpen(false);
      await load();
    } catch {
      setError("Could not save paper. Check required fields.");
    }
  }

  async function remove(id: number) {
    if (!window.confirm("Delete this paper?")) return;
    try {
      await api.delete(`/papers/${id}`);
      await load();
    } catch {
      setError("Could not delete paper (might be referenced).");
    }
  }

  return (
    <Stack spacing={2}>
      <Typography variant="h4">Curator paper management</Typography>
      <Typography color="text.secondary">
        Add new papers, edit metadata, and remove outdated records.
      </Typography>

      <Stack direction={{ xs: "column", sm: "row" }} spacing={1}>
        <TextField
          label="Search papers"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          sx={{ minWidth: 280 }}
        />
        <Button variant="outlined" onClick={() => void load()} disabled={loading}>
          {loading ? "Loading..." : "Refresh"}
        </Button>
        <Button variant="contained" onClick={openAdd}>
          Add paper
        </Button>
      </Stack>

      {error ? <Alert severity="error">{error}</Alert> : null}

      <Paper sx={{ overflow: "auto" }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Title</TableCell>
              <TableCell>Year</TableCell>
              <TableCell>Domain</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((p) => (
              <TableRow key={p.id}>
                <TableCell>
                  <Typography fontWeight={600}>{p.title}</Typography>
                  <Typography variant="caption" color="text.secondary">
                    {p.authors}
                  </Typography>
                </TableCell>
                <TableCell>{p.publicationYear}</TableCell>
                <TableCell>{p.domain}</TableCell>
                <TableCell align="right">
                  <Stack direction="row" spacing={1} justifyContent="flex-end">
                    <Button size="small" variant="outlined" onClick={() => openEdit(p)}>
                      Edit
                    </Button>
                    <Button size="small" color="error" variant="outlined" onClick={() => void remove(p.id)}>
                      Delete
                    </Button>
                  </Stack>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="md">
        <DialogTitle>{title}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <TextField label="Title" value={form.title} onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))} />
            <TextField label="Authors" value={form.authors} onChange={(e) => setForm((f) => ({ ...f, authors: e.target.value }))} />
            <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
              <TextField label="Year" type="number" value={form.publicationYear} onChange={(e) => setForm((f) => ({ ...f, publicationYear: e.target.value }))} />
              <TextField label="Domain" value={form.domain} onChange={(e) => setForm((f) => ({ ...f, domain: e.target.value }))} />
            </Stack>
            <TextField label="Journal" value={form.journal} onChange={(e) => setForm((f) => ({ ...f, journal: e.target.value }))} />
            <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
              <TextField label="DOI" value={form.doi} onChange={(e) => setForm((f) => ({ ...f, doi: e.target.value }))} />
              <TextField label="URL" value={form.url} onChange={(e) => setForm((f) => ({ ...f, url: e.target.value }))} />
            </Stack>
            <TextField label="Keywords (comma separated)" value={form.keywords} onChange={(e) => setForm((f) => ({ ...f, keywords: e.target.value }))} />
            <TextField
              label="Abstract"
              multiline
              minRows={3}
              value={form.abstractText}
              onChange={(e) => setForm((f) => ({ ...f, abstractText: e.target.value }))}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => void save()}>
            {editing ? "Update" : "Create"}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}

