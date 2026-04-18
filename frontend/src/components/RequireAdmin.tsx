import { Navigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";

export function RequireAdmin({ children }: { children: React.ReactNode }) {
  const { user } = useAuth();
  if (!user?.roles.includes("ROLE_ADMIN")) {
    return <Navigate to="/" replace />;
  }
  return <>{children}</>;
}
