import { Navigate, Route, Routes } from "react-router-dom";
import { ShellLayout } from "./components/ShellLayout";
import { RequireAdmin } from "./components/RequireAdmin";
import { RequireAuth } from "./components/RequireAuth";
import { RequireCurator } from "./components/RequireCurator";
import { RequireNonAdmin } from "./components/RequireNonAdmin";
import { AdminPage } from "./pages/AdminPage";
import { CuratorPapersPage } from "./pages/CuratorPapersPage";
import { DashboardPage } from "./pages/DashboardPage";
import { LibraryPage } from "./pages/LibraryPage";
import { LoginPage } from "./pages/LoginPage";
import { PaperDetailPage } from "./pages/PaperDetailPage";
import { RecommendationsPage } from "./pages/RecommendationsPage";
import { RegisterPage } from "./pages/RegisterPage";
import { SearchPage } from "./pages/SearchPage";

export default function App() {
  return (
    <Routes>
      <Route element={<ShellLayout />}>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          path="/search"
          element={
            <RequireNonAdmin>
              <SearchPage />
            </RequireNonAdmin>
          }
        />
        <Route
          path="/curator/papers"
          element={
            <RequireAuth>
              <RequireCurator>
                <CuratorPapersPage />
              </RequireCurator>
            </RequireAuth>
          }
        />
        <Route path="/papers/:id" element={<PaperDetailPage />} />
        <Route
          path="/recommendations"
          element={
            <RequireAuth>
              <RequireNonAdmin>
                <RecommendationsPage />
              </RequireNonAdmin>
            </RequireAuth>
          }
        />
        <Route
          path="/library"
          element={
            <RequireAuth>
              <RequireNonAdmin>
                <LibraryPage />
              </RequireNonAdmin>
            </RequireAuth>
          }
        />
        <Route
          path="/admin"
          element={
            <RequireAuth>
              <RequireAdmin>
                <AdminPage />
              </RequireAdmin>
            </RequireAuth>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
