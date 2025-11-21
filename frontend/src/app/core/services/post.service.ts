import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiResponse, PageResponse } from '../../models/api-response';
import { Post } from '../../models/post';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PostService {
  constructor(private http: HttpClient) {}

  list(page = 0, size = 20) {
    return this.http.get<ApiResponse<PageResponse<Post>>>(`${environment.api}/posts`, { params: { page, size } });
  }

  get(id: number) {
    return this.http.get<ApiResponse<Post>>(`${environment.api}/posts/${id}`);
  }

  create(payload: Partial<Post>) {
    return this.http.post<ApiResponse<Post>>(`${environment.api}/posts`, payload);
  }

  update(id: number, payload: Partial<Post>) {
    return this.http.put<ApiResponse<Post>>(`${environment.api}/posts/${id}`, payload);
  }

  delete(id: number) {
    return this.http.delete<ApiResponse<void>>(`${environment.api}/posts/${id}`);
  }

  updateStatus(id: number, newStatus: string, reason?: string) {
    return this.http.patch<ApiResponse<Post>>(`${environment.api}/posts/${id}/status`, { newStatus, reason });
  }
}
