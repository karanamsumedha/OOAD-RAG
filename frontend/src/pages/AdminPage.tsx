import { LinearProgress, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import api from "@/services/api";

type UserRow = {
  id: number;
  fullName: string;
  email: string;
  roles: string[];
  createdAt: string;
};

type Report = {
  totalUsers: number;
  totalPapers: number;
  totalLibraryItems: number;
  totalFeedback: number;
  totalInteractions: number;
};

export function AdminPage() {
  const [users, setUsers] = useState<UserRow[]>([]);
  const [report, setReport] = useState<Report | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    void (async () => {
      try {
        const [u, r] = await Promise.all([api.get<UserRow[]>("/admin/users"), api.get<Report>("/admin/reports")]);
        setUsers(u.data);
        setReport(r.data);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  if (loading) return <LinearProgress />;

  return (
    <Stack spacing={3}>
      <Typography variant="h4">Admin dashboard</Typography>
      {report ? (
        <Paper sx={{ p: 2 }}>
          <Typography variant="subtitle1" fontWeight={700} gutterBottom>
            Usage snapshot
          </Typography>
          <Stack direction={{ xs: "column", sm: "row" }} spacing={2} flexWrap="wrap">
            <Typography variant="body2">Users: {report.totalUsers}</Typography>
            <Typography variant="body2">Papers: {report.totalPapers}</Typography>
            <Typography variant="body2">Library items: {report.totalLibraryItems}</Typography>
            <Typography variant="body2">Feedback: {report.totalFeedback}</Typography>
            <Typography variant="body2">Interactions: {report.totalInteractions}</Typography>
          </Stack>
        </Paper>
      ) : null}

      <Paper sx={{ overflow: "auto" }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Roles</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((u) => (
              <TableRow key={u.id}>
                <TableCell>{u.fullName}</TableCell>
                <TableCell>{u.email}</TableCell>
                <TableCell>{u.roles.join(", ")}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    </Stack>
  );
}
