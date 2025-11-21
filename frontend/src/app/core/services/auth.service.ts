import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, tap } from 'rxjs';
import { AuthResponse } from '../../models/auth';
import { ApiResponse } from '../../models/api-response';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUser$ = new BehaviorSubject<AuthResponse | null>(null);
  private authHeader: string | null = null;

  constructor(private http: HttpClient) {
    const cachedUser = localStorage.getItem('auth:user');
    const cachedHeader = localStorage.getItem('auth:header');
    if (cachedUser) {
      this.currentUser$.next(JSON.parse(cachedUser));
    }
    if (cachedHeader) {
      this.authHeader = cachedHeader;
    }
  }

  me() {
    return this.http.get<ApiResponse<AuthResponse>>(`${environment.api}/auth/me`).pipe(
      tap(res => this.saveAuth(res.data, this.authHeader))
    );
  }

  login(username: string, password: string) {
    const header = btoa(`${username}:${password}`);
    return this.http.post<ApiResponse<AuthResponse>>(`${environment.api}/auth/login`, { username, password }).pipe(
      tap(res => this.saveAuth(res.data, header))
    );
  }

  register(username: string, password: string, email: string) {
    const header = btoa(`${username}:${password}`);
    return this.http.post<ApiResponse<AuthResponse>>(`${environment.api}/auth/register`, { username, password, email }).pipe(
      tap(res => this.saveAuth(res.data, header))
    );
  }

  logout() {
    this.currentUser$.next(null);
    this.authHeader = null;
    localStorage.removeItem('auth:user');
    localStorage.removeItem('auth:header');
  }

  get user() {
    return this.currentUser$.asObservable();
  }

  get snapshot() {
    return this.currentUser$.value;
  }

  get authorizationHeader() {
    return this.authHeader;
  }

  private saveAuth(user: AuthResponse, header: string | null) {
    this.currentUser$.next(user);
    this.authHeader = header;
    localStorage.setItem('auth:user', JSON.stringify(user));
    if (header) {
      localStorage.setItem('auth:header', header);
    }
  }
}
