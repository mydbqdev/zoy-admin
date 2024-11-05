import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
 
@Injectable()
export class HttpInterceptorService implements HttpInterceptor {
 
    constructor(private router:Router) { }
 
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (this.isUserSignedin() && this.getToken()) {
            const request = req.clone({
                headers: new HttpHeaders({
                    'Authorization': this.getToken()
                })
            });
            return next.handle(request).pipe(
				catchError(err => {
					if(err instanceof HttpErrorResponse && err.status === 401) {
						this.signout();
					}
					return throwError(err);
				})
			);
        }
       
		return next.handle(req); 
    }

    signout() {
		sessionStorage.removeItem('user');
		sessionStorage.removeItem('token');
		this.router.navigateByUrl('/');
	}

	isUserSignedin() {
		return sessionStorage.getItem('token') !== null;
	}

	getSignedinUser() {
		return sessionStorage.getItem('user') as string;
	}

	getToken() {
		return sessionStorage.getItem('token') as string;
	}

}
