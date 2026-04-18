import { Alert, Box, Button, Link, Paper, Stack, TextField, Typography } from "@mui/material";
import { Link as RouterLink, useNavigate } from "react-router-dom";
import { useState } from "react";
import axios from "axios";
import { useAuth } from "@/context/AuthContext";

export function RegisterPage() {
  const { register } = useAuth();
  const nav = useNavigate();
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErr(null);
    setLoading(true);
    try {
      await register(fullName, email, password);
      nav("/", { replace: true });
    } catch (error: unknown) {
      if (axios.isAxiosError(error)) {
        const backendMessage = (error.response?.data as { message?: string } | undefined)?.message;
        setErr(backendMessage ?? "Could not register. Please check your details and try again.");
      } else {
        setErr("Could not register. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <Box maxWidth={440} mx="auto" mt={4}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          Create account
        </Typography>
        <Typography color="text.secondary" sx={{ mb: 3 }}>
          Researchers get search, recommendations, library, and citation tools.
        </Typography>
        {err ? (
          <Alert severity="error" sx={{ mb: 2 }}>
            {err}
          </Alert>
        ) : null}
        <form onSubmit={onSubmit}>
          <Stack spacing={2}>
            <TextField label="Full name" value={fullName} onChange={(e) => setFullName(e.target.value)} required fullWidth />
            <TextField label="Email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required fullWidth />
            <TextField
              label="Password (min 8 characters)"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              fullWidth
              inputProps={{ minLength: 8 }}
            />
            <Button type="submit" variant="contained" size="large" disabled={loading}>
              {loading ? "Creating…" : "Create account"}
            </Button>
          </Stack>
        </form>
        <Typography sx={{ mt: 2 }} variant="body2" color="text.secondary">
          Already have an account?{" "}
          <Link component={RouterLink} to="/login">
            Sign in
          </Link>
        </Typography>
      </Paper>
    </Box>
  );
}
