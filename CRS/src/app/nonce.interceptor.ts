import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class NonceInterceptor implements HttpInterceptor {

  private key = 'gson-statistics';

  constructor() {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const nonce = localStorage.getItem(this.key);
    var cloned = req;
    if(nonce != null){
      cloned = req.clone({
        setHeaders: { 'X-GSON-STATISTICS': nonce }
      });
    }

    return next.handle(cloned);
  }
}