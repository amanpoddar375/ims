import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login.component';
import { PostListComponent } from './features/posts/post-list.component';
import { PostDetailComponent } from './features/posts/post-detail.component';
import { PostFormComponent } from './features/posts/post-form.component';
import { AdminDashboardComponent } from './features/admin/admin-dashboard.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', component: PostListComponent, canActivate: [authGuard] },
  { path: 'posts/new', component: PostFormComponent, canActivate: [authGuard] },
  { path: 'posts/:id', component: PostDetailComponent, canActivate: [authGuard] },
  { path: 'admin', component: AdminDashboardComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
