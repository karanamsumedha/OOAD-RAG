import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Paper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useState } from "react";
import { Link as RouterLink } from "react-router-dom";
import api from "@/services/api";

type RagSource = {
  paperId: number;
  title: string;
  authors: string;
  publicationYear: number;
  domain: string;
  url: string | null;
};

type RagResponse = {
  question: string;
  answer: string;
  sources: RagSource[];
};

export function RagPage() {
  const [question, setQuestion] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<RagResponse | null>(null);

  async function runQuery() {
    if (question.trim().length < 3) {
      setError("Please enter at least 3 characters.");
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const { data } = await api.post<RagResponse>("/rag/query", { question });
      setResult(data);
    } catch {
      setError("RAG query failed. Please retry.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Stack spacing={3}>
      <Box>
        <Typography variant="h4" gutterBottom>
          Ask RAG
        </Typography>
        <Typography color="text.secondary">
          Ask a research question. The system retrieves relevant papers and builds a concise grounded response.
        </Typography>
      </Box>

      <Paper sx={{ p: 2 }}>
        <Stack spacing={2}>
          <TextField
            label="Your question"
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            multiline
            minRows={3}
            placeholder="Example: What are the latest techniques for personalized paper recommendation?"
          />
          <Box>
            <Button variant="contained" onClick={() => void runQuery()} disabled={loading}>
              {loading ? "Running..." : "Run RAG query"}
            </Button>
          </Box>
        </Stack>
      </Paper>

      {loading ? (
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          <CircularProgress size={18} />
          <Typography variant="body2" color="text.secondary">
            Retrieving papers and synthesizing answer...
          </Typography>
        </Box>
      ) : null}
      {error ? <Alert severity="error">{error}</Alert> : null}

      {result ? (
        <Paper sx={{ p: 2 }}>
          <Typography variant="subtitle1" fontWeight={700}>
            Answer
          </Typography>
          <Typography sx={{ whiteSpace: "pre-wrap", mt: 1 }}>{result.answer}</Typography>
          <Typography variant="subtitle1" fontWeight={700} sx={{ mt: 3 }}>
            Retrieved Sources
          </Typography>
          <Stack spacing={1} sx={{ mt: 1 }}>
            {result.sources.map((s) => (
              <Paper key={s.paperId} sx={{ p: 1.5 }}>
                <Typography component={RouterLink} to={`/papers/${s.paperId}`} sx={{ textDecoration: "none" }}>
                  {s.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {s.authors} · {s.publicationYear} · {s.domain}
                </Typography>
              </Paper>
            ))}
          </Stack>
        </Paper>
      ) : null}
    </Stack>
  );
}

