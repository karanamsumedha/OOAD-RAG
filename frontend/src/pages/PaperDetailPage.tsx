import {
  Alert,
  Box,
  Button,
  MenuItem,
  Paper as MuiPaper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "@/services/api";
import type { Paper as PaperDto } from "./SearchPage";
import { useAuth } from "@/context/AuthContext";

export function PaperDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const [paper, setPaper] = useState<PaperDto | null>(null);
  const [citation, setCitation] = useState<string | null>(null);
  const [format, setFormat] = useState<"APA" | "IEEE">("APA");
  const [msg, setMsg] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    void api.get<PaperDto>(`/papers/${id}`).then((r) => setPaper(r.data));
  }, [id]);

  async function addToLibrary() {
    if (!id || !user) return;
    setMsg(null);
    try {
      await api.post("/library/add", { paperId: Number(id) });
      setMsg("Saved to your library.");
    } catch {
      setMsg("Could not save (already saved or server error).");
    }
  }

  async function genCitation() {
    if (!id || !user) return;
    setMsg(null);
    try {
      const { data } = await api.post<{ renderedText: string }>("/citation/generate", {
        paperId: Number(id),
        format,
      });
      setCitation(data.renderedText);
    } catch {
      setMsg("Sign in required to generate citations.");
    }
  }

  async function sendFeedback() {
    if (!id || !user) return;
    setMsg(null);
    try {
      await api.post("/feedback", { paperId: Number(id), rating: 5, commentText: "Useful paper" });
      setMsg("Thanks for your feedback.");
    } catch {
      setMsg("Could not submit feedback.");
    }
  }

  if (!paper) {
    return <Typography>Loading…</Typography>;
  }

  return (
    <Stack spacing={2}>
      <Typography variant="h4">{paper.title}</Typography>
      <Typography color="text.secondary">
        {paper.authors} · {paper.publicationYear} · {paper.domain}
      </Typography>
      {paper.journal ? (
        <Typography variant="body2" color="text.secondary">
          {paper.journal}
          {paper.doi ? ` · DOI ${paper.doi}` : ""}
        </Typography>
      ) : null}
      {paper.abstractText ? (
        <MuiPaper sx={{ p: 2 }}>
          <Typography variant="subtitle2" color="text.secondary" gutterBottom>
            Abstract
          </Typography>
          <Typography>{paper.abstractText}</Typography>
        </MuiPaper>
      ) : null}

      <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
        <Button variant="contained" onClick={() => void addToLibrary()} disabled={!user}>
          Save to library
        </Button>
        <Button variant="outlined" onClick={() => void sendFeedback()} disabled={!user}>
          Rate 5★
        </Button>
      </Stack>

      {msg ? <Alert severity="info">{msg}</Alert> : null}

      <MuiPaper sx={{ p: 2 }}>
        <Typography variant="subtitle1" fontWeight={700} gutterBottom>
          Citation generator
        </Typography>
        <Stack direction={{ xs: "column", sm: "row" }} spacing={2} alignItems={{ sm: "center" }}>
          <TextField select label="Format" value={format} onChange={(e) => setFormat(e.target.value as "APA" | "IEEE")} sx={{ minWidth: 160 }}>
            <MenuItem value="APA">APA</MenuItem>
            <MenuItem value="IEEE">IEEE</MenuItem>
          </TextField>
          <Button variant="contained" onClick={() => void genCitation()} disabled={!user}>
            Generate
          </Button>
        </Stack>
        {citation ? (
          <Box
            sx={{
              mt: 2,
              p: 2,
              borderRadius: 2,
              bgcolor: "rgba(0,0,0,0.25)",
              fontFamily: '"JetBrains Mono", monospace',
              fontSize: 14,
              whiteSpace: "pre-wrap",
            }}
          >
            {citation}
          </Box>
        ) : null}
      </MuiPaper>
    </Stack>
  );
}
