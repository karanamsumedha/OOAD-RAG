import MenuBookRoundedIcon from "@mui/icons-material/MenuBookRounded";
import {
  AppBar,
  Box,
  Button,
  Container,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import DashboardRoundedIcon from "@mui/icons-material/DashboardRounded";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import AutoAwesomeRoundedIcon from "@mui/icons-material/AutoAwesomeRounded";
import LibraryBooksRoundedIcon from "@mui/icons-material/LibraryBooksRounded";
import AdminPanelSettingsRoundedIcon from "@mui/icons-material/AdminPanelSettingsRounded";
import { useState } from "react";
import { Link as RouterLink, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";

const drawerWidth = 260;

export function ShellLayout() {
  const { user, logout } = useAuth();
  const theme = useTheme();
  const narrow = useMediaQuery(theme.breakpoints.down("md"));
  const [open, setOpen] = useState(false);
  const loc = useLocation();

  const nav = [
    { to: "/", label: "Dashboard", icon: <DashboardRoundedIcon />, needAuth: true },
    { to: "/search", label: "Search papers", icon: <SearchRoundedIcon />, needAuth: false },
    { to: "/recommendations", label: "For you", icon: <AutoAwesomeRoundedIcon />, needAuth: true },
    { to: "/library", label: "My library", icon: <LibraryBooksRoundedIcon />, needAuth: true },
    { to: "/admin", label: "Admin", icon: <AdminPanelSettingsRoundedIcon />, needAuth: true, admin: true },
  ];

  const drawer = (
    <Box sx={{ pt: 2, px: 1 }}>
      <Box sx={{ px: 2, pb: 2, display: "flex", alignItems: "center", gap: 1 }}>
        <MenuBookRoundedIcon color="primary" sx={{ fontSize: 32 }} />
        <Box>
          <Typography variant="subtitle1" fontWeight={700}>
            RAG Platform
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Papers · citations · you
          </Typography>
        </Box>
      </Box>
      <List>
        {nav
          .filter((n) => {
            if (n.admin && !user?.roles.includes("ROLE_ADMIN")) return false;
            if (n.needAuth && !user) return false;
            return true;
          })
          .map((item) => (
            <ListItemButton
              key={item.to}
              component={RouterLink}
              to={item.to}
              selected={loc.pathname === item.to}
              onClick={() => narrow && setOpen(false)}
              sx={{ borderRadius: 2, mx: 1, mb: 0.5 }}
            >
              <ListItemIcon sx={{ minWidth: 40, color: "primary.light" }}>{item.icon}</ListItemIcon>
              <ListItemText primary={item.label} />
            </ListItemButton>
          ))}
      </List>
    </Box>
  );

  return (
    <Box sx={{ display: "flex", minHeight: "100vh" }}>
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          ml: { md: `${drawerWidth}px` },
          width: { md: `calc(100% - ${drawerWidth}px)` },
          background: "rgba(11,15,26,0.75)",
          backdropFilter: "blur(12px)",
          borderBottom: "1px solid rgba(255,255,255,0.06)",
        }}
      >
        <Toolbar>
          {narrow && (
            <IconButton color="inherit" edge="start" onClick={() => setOpen(true)} sx={{ mr: 1 }}>
              <MenuIcon />
            </IconButton>
          )}
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>
            Research workspace
          </Typography>
          {user ? (
            <Typography variant="body2" color="text.secondary" sx={{ mr: 2 }}>
              {user.fullName}
            </Typography>
          ) : null}
          {user ? (
            <Button color="inherit" variant="outlined" onClick={logout} sx={{ borderColor: "rgba(255,255,255,0.2)" }}>
              Log out
            </Button>
          ) : (
            <Button component={RouterLink} to="/login" color="primary" variant="contained">
              Sign in
            </Button>
          )}
        </Toolbar>
      </AppBar>

      <Drawer
        variant={narrow ? "temporary" : "permanent"}
        open={narrow ? open : true}
        onClose={() => setOpen(false)}
        ModalProps={{ keepMounted: true }}
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          "& .MuiDrawer-paper": {
            width: drawerWidth,
            boxSizing: "border-box",
            borderRight: "1px solid rgba(255,255,255,0.06)",
            background: "linear-gradient(180deg, #0f1424 0%, #0b0f1a 100%)",
          },
        }}
      >
        {drawer}
      </Drawer>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: { xs: 2, md: 4 },
          pt: { xs: 10, md: 11 },
          width: "100%",
          minHeight: "100vh",
          background:
            "radial-gradient(1200px 600px at 10% -10%, rgba(124,156,255,0.18), transparent 60%), radial-gradient(900px 500px at 90% 0%, rgba(199,146,234,0.12), transparent 55%), #0b0f1a",
        }}
      >
        <Container maxWidth="lg">
          <Outlet />
        </Container>
      </Box>
    </Box>
  );
}
