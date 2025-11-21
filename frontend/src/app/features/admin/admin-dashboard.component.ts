import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostService } from '../../core/services/post.service';
import { Post } from '../../models/post';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section>
      <h2>Admin Dashboard</h2>
      <p class="muted">All posts (admin visibility)</p>
      <table class="table" *ngIf="posts.length; else empty">
        <thead>
          <tr><th>Title</th><th>Status</th><th>Author</th><th>Type</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let post of posts">
            <td>{{post.title}}</td>
            <td>{{post.status}}</td>
            <td>{{post.authorUsername}}</td>
            <td>{{post.type}}</td>
          </tr>
        </tbody>
      </table>
      <ng-template #empty><p>No posts yet.</p></ng-template>
    </section>
  `,
  styles: [`
    .table { width:100%; border-collapse:collapse; }
    th, td { border:1px solid #e2e8f0; padding:0.5rem; }
    th { background:#f8fafc; text-align:left; }
    .muted { color:#64748b; }
  `]
})
export class AdminDashboardComponent implements OnInit {
  posts: Post[] = [];

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.postService.list().subscribe(res => this.posts = res.data.content);
  }
}
