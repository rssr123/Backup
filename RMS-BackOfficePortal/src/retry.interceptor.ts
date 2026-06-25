// retry.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, timer } from 'rxjs';
import { catchError, retryWhen, mergeMap } from 'rxjs/operators';

// @Injectable()
// export class RetryInterceptor implements HttpInterceptor {

  // intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  //   return next.handle(req).pipe(
  //     retryWhen(errors =>
  //       errors.pipe(
  //         mergeMap((error, retryCount) => {
  //           if (retryCount < 2 && error instanceof HttpErrorResponse && error.status === 502) {
  //             // Retry up to 2 times with 0.5s delay
  //             return timer(500);
  //           }
  //           return throwError(() => error);
  //         })
  //       )
  //     )
  //   );
  // }
// }
