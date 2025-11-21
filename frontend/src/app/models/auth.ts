export interface AuthResponse {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
  token?: string | null;
}
