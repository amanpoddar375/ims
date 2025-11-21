import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { HttpErrorService } from '../../core/services/http-error.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section class="card">
      <h2>Login</h2>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <label>Username
          <input formControlName="username" />
        </label>
        <label>Password
          <input type="password" formControlName="password" />
        </label>
        <button type="submit" [disabled]="form.invalid">Login</button>
        <p class="error" *ngIf="error">{{error}}</p>
      </form>
    </section>
  `,
  styles: [`
    .card { max-width:360px; margin:2rem auto; padding:1.5rem; border:1px solid #e2e8f0; border-radius:8px; background:white; }
    label { display:flex; flex-direction:column; gap:0.25rem; margin-bottom:0.75rem; font-weight:600; }
    input { padding:0.5rem; border:1px solid #cbd5e1; border-radius:4px; }
    button { width:100%; padding:0.6rem; background:#2563eb; color:white; border:none; border-radius:4px; cursor:pointer; font-weight:600; }
    .error { color:#dc2626; margin-top:0.5rem; }
  `]
})
export class LoginComponent {
  error = '';
  form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private err: HttpErrorService) {}

  submit() {
    if (this.form.invalid) return;
    const { username, password } = this.form.value;
    this.auth.login(username!, password!).subscribe({
      next: () => this.router.navigate(['/']),
      error: e => this.error = this.err.format(e)
    });
  }
}
