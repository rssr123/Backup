import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
    
@Injectable()
export class CustomInterceptor implements HttpInterceptor {

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

	  	return next.handle(request.clone({withCredentials: true}));
	  }

}