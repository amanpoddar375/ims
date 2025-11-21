import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class HttpErrorService {
  format(error: any): string {
    if (error?.error?.message) return error.error.message;
    if (error?.message) return error.message;
    return 'Unexpected error';
  }
}
