import { Box, Button, Paper, Stack, Typography } from "@mui/material";
import { Link as RouterLink } from "react-router-dom";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import AutoAwesomeRoundedIcon from "@mui/icons-material/AutoAwesomeRounded";
import LibraryBooksRoundedIcon from "@mui/icons-material/LibraryBooksRounded";
import { useAuth } from "@/context/AuthContext";

export function DashboardPage() {
  const { user } = useAuth();

  return (
    <Stack spacing={3}>
      <Box>
        <Typography variant="h3" gutterBottom>
          Hello{user ? `, ${user.fullName.split(" ")[0]}` : ""}
        </Typography>
        <Typography color="text.secondary" maxWidth={720}>
          Discover papers with smart filters, keep a personal library with reading progress, generate APA or IEEE citations, and
          get recommendations that adapt as you interact with the catalog.
        </Typography>
      </Box>

      <Box
        sx={{
          display: "grid",
          gap: 2,
          gridTemplateColumns: { xs: "1fr", md: "repeat(3, 1fr)" },
        }}
      >
        <Box>
          <Paper sx={{ p: 3, height: "100%" }}>
            <SearchRoundedIcon color="primary" sx={{ fontSize: 36, mb: 1 }} />
            <Typography variant="h6">Search & filter</Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Keyword search plus domain, year, and author filters.
            </Typography>
            <Button component={RouterLink} to="/search" variant="outlined" fullWidth>
              Open search
            </Button>
          </Paper>
        </Box>
        <Box>
          <Paper sx={{ p: 3, height: "100%" }}>
            <AutoAwesomeRoundedIcon color="secondary" sx={{ fontSize: 36, mb: 1 }} />
            <Typography variant="h6">Recommendations</Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Content-based scoring from your history and paper metadata.
            </Typography>
            <Button component={RouterLink} to="/recommendations" variant="outlined" fullWidth disabled={!user}>
              View for you
            </Button>
          </Paper>
        </Box>
        <Box>
          <Paper sx={{ p: 3, height: "100%" }}>
            <LibraryBooksRoundedIcon color="primary" sx={{ fontSize: 36, mb: 1 }} />
            <Typography variant="h6">Your library</Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Save papers and track reading progress end-to-end.
            </Typography>
            <Button component={RouterLink} to="/library" variant="outlined" fullWidth disabled={!user}>
              Open library
            </Button>
          </Paper>
        </Box>
      </Box>
    </Stack>
  );
}
