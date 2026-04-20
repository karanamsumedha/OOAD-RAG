import { Navigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";

export function RequireNonAdmin({ children }: { children: React.ReactNode }) {
  const { user } = useAuth();
  if (user?.roles.includes("ROLE_ADMIN")) {
    return <Navigate to="/admin" replace />;
  }
  return <>{children}</>;
}

