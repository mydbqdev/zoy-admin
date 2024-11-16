import { Observable, of} from 'rxjs';
import { ResponseStore } from '../models/response.model';
import { ApplicationSession } from '../shared/model/application-session';
import { User } from '../shared/user';
import { SignupDetails } from '../shared/signup-details';
/*
@Injectable({
	providedIn: 'root'
})*/
export abstract class AuthService {

	//constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient) { }

	/*signin(request: Request): Observable<any> {
		return this.http.post<any>(this.baseUrl + 'signin', request, {headers: new HttpHeaders({ 'Content-Type': 'application/json' })}).pipe(map((resp) => {
			sessionStorage.setItem('user', request.empEmail);
			sessionStorage.setItem('token', 'Bearer ' + resp.token);
			return resp;
		}));
	}

	

	signout() {
		sessionStorage.removeItem('user');
		sessionStorage.removeItem('token');

		this.router.navigateByUrl('signin');
	}

	isUserSignedin() {
		return sessionStorage.getItem('token') !== null;
	}

	getSignedinUser() {
		return sessionStorage.getItem('user') as string;
	}

	getToken() {
		let token = sessionStorage.getItem('token') as string;
		return token;
	}*/

	public sessionSnapshot:ApplicationSession;
    public redirectUrl:string;
    public message:string;

    public isLoggedIn(redirectUrl?:string):Observable<boolean>{
            this.redirectUrl=redirectUrl;
            return of(false);

    }
	public getAuthUser(user:User) : Observable<any>{
        return of(true);
    }

    public subscribeFcmNotification(subscribe:string) : Observable<any>{
        return of(true);
    }
    public getUserSignupDetails(data:any) : Observable<any>{
        return of(true);
    }
     public checkLoginUser() : void{
	 }
	 public checkLoginUserVlidaate() :void{}

     public checkLogout() : void{
	}
	setSessionStore(res: ResponseStore):void{}
	public signupUser(user:SignupDetails) : Observable<any>{
        return of(true);
    }
	
}
