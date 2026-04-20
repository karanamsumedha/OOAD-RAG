import { Alert, Box, Button, LinearProgress, Paper, Stack, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { Link as RouterLink } from "react-router-dom";
import api from "@/services/api";
import { useAuth } from "@/context/AuthContext";

type Rec = {
  paperId: number;
  title: string;
  authors: string;
  publicationYear: number;
  domain: string;
  score: number;
  reason: string;
};

export function RecommendationsPage() {
  const { user } = useAuth();
  const [items, setItems] = useState<Rec[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function load() {
    if (!user) return;
    setLoading(true);
    setError(null);
    try {
      const { data } = await api.get<Rec[]>(`/recommendations/${user.userId}`);
      setItems(data);
    } catch {
      setError("Could not load recommendations right now.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
    const timer = window.setInterval(() => {
      void load();
    }, 30000);
    return () => window.clearInterval(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user]);

  if (!user) return null;

  if (loading) {
    return <LinearProgress />;
  }

  return (
    <Stack spacing={2}>
      <Typography variant="h4">Recommended for you</Typography>
      <Typography color="text.secondary">
        Scores combine domain and keyword overlap with your recent interactions.
      </Typography>
      <Box>
        <Button variant="outlined" size="small" onClick={() => void load()} disabled={loading}>
          Refresh
        </Button>
      </Box>
      {error ? <Alert severity="error">{error}</Alert> : null}
      {items.map((r) => (
        <Paper key={r.paperId} sx={{ p: 2 }}>
          <Typography variant="h6">{r.title}</Typography>
          <Typography variant="body2" color="text.secondary">
            {r.authors} · {r.publicationYear} · {r.domain}
          </Typography>
          <Box sx={{ mt: 1, display: "flex", justifyContent: "space-between", gap: 2, flexWrap: "wrap" }}>
            <Typography variant="body2" color="primary.light">
              {r.reason}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              score {r.score.toFixed(2)}
            </Typography>
          </Box>
          <Box sx={{ mt: 1 }}>
            <Typography component={RouterLink} to={`/papers/${r.paperId}`} color="secondary.light" sx={{ textDecoration: "none" }}>
              View paper →
            </Typography>
          </Box>
        </Paper>
      ))}
      {items.length === 0 ? (
        <Paper sx={{ p: 3 }}>
          <Typography color="text.secondary">
            Interact with a few papers (search, view, save, rate) and recommendations will appear here.
          </Typography>
        </Paper>
      ) : null}
    </Stack>
  );
}
