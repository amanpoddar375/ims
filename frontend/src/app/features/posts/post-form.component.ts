import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PostService } from '../../core/services/post.service';
import { Post } from '../../models/post';
import { HttpErrorService } from '../../core/services/http-error.service';

@Component({
  selector: 'app-post-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section class="card">
      <h2>New Post</h2>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <label>Title
          <input formControlName="title" />
        </label>
        <label>Description
          <textarea formControlName="description"></textarea>
        </label>
        <label>Type
          <select formControlName="type">
            <option value="ISSUE">ISSUE</option>
            <option value="COMPLAINT">COMPLAINT</option>
            <option value="SUGGESTION">SUGGESTION</option>
            <option value="GENERAL">GENERAL</option>
            <option value="OTHER">OTHER</option>
          </select>
        </label>
        <label>Priority
          <select formControlName="priority">
            <option value="">N/A</option>
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
            <option value="CRITICAL">CRITICAL</option>
          </select>
        </label>
        <label>Attachment URL
          <input formControlName="attachmentUrl" />
        </label>
        <button type="submit" [disabled]="form.invalid">Save</button>
        <p class="error" *ngIf="error">{{error}}</p>
      </form>
    </section>
  `,
  styles: [`
    textarea { min-height:120px; }
    .card { max-width:640px; margin:1rem auto; padding:1.5rem; background:white; border-radius:8px; border:1px solid #e2e8f0; }
    label { display:flex; flex-direction:column; gap:0.25rem; margin-bottom:0.75rem; font-weight:600; }
    input, textarea, select { padding:0.5rem; border:1px solid #cbd5e1; border-radius:4px; }
    button { padding:0.6rem 1rem; background:#2563eb; color:white; border:none; border-radius:4px; cursor:pointer; font-weight:600; }
    .error { color:#dc2626; }
  `]
})
export class PostFormComponent {
  error = '';
  form = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
    description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(5000)]],
    type: ['ISSUE', Validators.required],
    priority: [''],
    attachmentUrl: ['']
  });

  constructor(private fb: FormBuilder, private postService: PostService, private router: Router, private err: HttpErrorService) {}

  submit() {
    if (this.form.invalid) return;
    const value = this.form.value;
    const payload: Partial<Post> = {
      title: value.title,
      description: value.description,
      type: value.type as Post['type'],
      priority: value.priority ? value.priority as Post['priority'] : undefined,
      attachmentUrl: value.attachmentUrl || undefined
    };
    this.postService.create(payload).subscribe({
      next: () => this.router.navigate(['/']),
      error: e => this.error = this.err.format(e)
    });
  }
}
