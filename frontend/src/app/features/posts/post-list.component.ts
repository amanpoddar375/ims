import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PostService } from '../../core/services/post.service';
import { Post } from '../../models/post';

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section>
      <h2>Posts</h2>
      <div class="grid">
        <article *ngFor="let post of posts" class="card">
          <header>
            <h3>{{post.title}}</h3>
            <span class="badge">{{post.status}}</span>
          </header>
          <p>{{post.description}}</p>
          <div class="meta">
            <span>Type: {{post.type}}</span>
            <span>Priority: {{post.priority || 'N/A'}} </span>
          </div>
          <a [routerLink]="['/posts', post.id]">View</a>
        </article>
      </div>
    </section>
  `,
  styles: [`
    .grid { display:grid; gap:1rem; grid-template-columns:repeat(auto-fit,minmax(260px,1fr)); }
    .card { border:1px solid #e2e8f0; padding:1rem; border-radius:8px; background:white; }
    header { display:flex; justify-content:space-between; align-items:center; }
    .badge { background:#ecfeff; color:#0ea5e9; padding:0.2rem 0.6rem; border-radius:4px; font-size:0.85rem; }
    .meta { display:flex; gap:1rem; color:#475569; font-size:0.9rem; margin:0.5rem 0; }
    a { font-weight:600; }
  `]
})
export class PostListComponent implements OnInit {
  posts: Post[] = [];

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.postService.list().subscribe(res => this.posts = res.data.content);
  }
}
