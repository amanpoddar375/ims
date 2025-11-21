import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <nav class="nav">
      <a routerLink="/">Posts</a>
      <a routerLink="/posts/new">New Post</a>
      <a routerLink="/admin">Admin</a>
      <span class="spacer"></span>
      <ng-container *ngIf="auth.user | async as user; else guest">
        <span>{{user.username}} ({{user.role}})</span>
        <button type="button" (click)="logout()">Logout</button>
      </ng-container>
      <ng-template #guest>
        <a routerLink="/login">Login</a>
      </ng-template>
    </nav>
  `,
  styles: [`
    .nav { display:flex; gap:1rem; align-items:center; padding:0.75rem 1rem; background:#0f172a; color:#fff; }
    a { color:#e2e8f0; text-decoration:none; font-weight:600; }
    .spacer { flex:1; }
    button { background:#ef4444; color:white; border:none; padding:0.4rem 0.75rem; border-radius:4px; cursor:pointer; }
  `]
})
export class NavComponent {
  constructor(public auth: AuthService, private router: Router) {}

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
