import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const header = auth.authorizationHeader;
  if (header) {
    req = req.clone({ setHeaders: { Authorization: `Basic ${header}` } });
  }
  return next(req);
};
