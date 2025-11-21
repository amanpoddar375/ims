import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService } from '../../core/services/post.service';
import { Post } from '../../models/post';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section *ngIf="post" class="card">
      <header class="header">
        <h2>{{post.title}}</h2>
        <span class="badge">{{post.status}}</span>
      </header>
      <p>{{post.description}}</p>
      <p><strong>Type:</strong> {{post.type}} | <strong>Priority:</strong> {{post.priority || 'N/A'}}</p>
      <p><strong>Author:</strong> {{post.authorUsername}}</p>
      <button *ngIf="post.status === 'RESOLVED'" (click)="closePost()">Mark Closed</button>
    </section>
  `,
  styles: [`
    .card { padding:1.5rem; border:1px solid #e2e8f0; border-radius:8px; background:white; }
    .header { display:flex; justify-content:space-between; align-items:center; }
    .badge { background:#f1f5f9; padding:0.2rem 0.5rem; border-radius:4px; }
    button { padding:0.5rem 1rem; background:#22c55e; color:white; border:none; border-radius:4px; cursor:pointer; }
  `]
})
export class PostDetailComponent implements OnInit {
  post?: Post;

  constructor(private route: ActivatedRoute, private postService: PostService, private router: Router) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.postService.get(id).subscribe(res => this.post = res.data);
  }

  closePost() {
    if (!this.post) return;
    this.postService.updateStatus(this.post.id, 'CLOSED').subscribe(() => this.router.navigate(['/']));
  }
}
