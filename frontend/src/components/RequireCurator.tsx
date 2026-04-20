import { Navigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";

export function RequireCurator({ children }: { children: React.ReactNode }) {
  const { user } = useAuth();
  if (!user?.roles.includes("ROLE_CURATOR")) {
    return <Navigate to="/" replace />;
  }
  return <>{children}</>;
}

