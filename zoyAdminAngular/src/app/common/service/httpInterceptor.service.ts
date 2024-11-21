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
		//	const tokenString=this.getZoyadminApi()=='yes'? this.getToken():this.getExternalApi();
			let headers = new HttpHeaders();
			console.log(this.getZoyadminApi()," this.getExternalApi()", this.getExternalApi());
           if(this.getZoyadminApi()=='no'){
			headers = new HttpHeaders({
				'Content-Type': 'application/json',
				//'Content-Type':'text/csv',
				'Authorization': this.getExternalApi(),
				'Access-Control-Allow-Origin': '*'
			})
		   }else{
			headers = new HttpHeaders({
				'Authorization': this.getToken(),
				'Access-Control-Allow-Origin': '*'
			})
		   }
			
			const request = req.clone({
                headers: headers
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
	
	getZoyadminApi(){
		return sessionStorage.getItem('zoyadminapi') as string;
	}
	getExternalApi(){
		return sessionStorage.getItem('exterApiToken') as string;
	}

}
