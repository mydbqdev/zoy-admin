import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Title, Meta } from '@angular/platform-browser';
import { AuthService } from './common/service/auth.service';
import { CanonicalService } from './common/shared/CanonicalService';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Myne Portal';

  isSignedin = false;

	signedinUser: string = '';

	greeting: any[] = [];

	constructor(private titleService:Title,private meta:Meta,private route: ActivatedRoute, private router: Router, private http: HttpClient, private authService: AuthService,private canonicalService:CanonicalService) {}

	ngOnInit() {

		this.titleService.setTitle(this.title);
        this.meta.addTags([
        {name:'keywords',content:this.title},
        {name:'description',content:this.title},
        {name:'viewport',content:'width=device-width, initial-scale=1'},
        {charset:'UTF-8'}
      ]);
		this.canonicalService.setCanonicalURL();	
			
	}
}
