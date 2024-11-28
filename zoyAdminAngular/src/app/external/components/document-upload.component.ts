import { AfterViewInit, Component, EventEmitter, inject, Input, OnInit, Output, ViewChild } from '@angular/core';
import { HttpClient, HttpEventType, HttpRequest } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';

import { FormBuilder } from '@angular/forms';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { FileUploadModel } from '../model/file-upload.model';
import { last, map, tap } from 'rxjs';


@Component({
  selector: 'app-document-upload',
  templateUrl: './document-upload.component.html',
  styleUrl: './document-upload.component.css'
})
export class DocumentUploadComponent implements OnInit, AfterViewInit {
    
  @Input() text = "Upload";
  @Input() param = "file";
  @Input() target = "https://file.io";
  @Input() accept = "image/*";
  @Output() complete = new EventEmitter<string>();
  private files: Array<FileUploadModel> = [];
	  constructor(private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private http: HttpClient, private userService: UserService,
		  private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService, private confirmationDialogService:ConfirmationDialogService) {
			  this.authService.checkLoginUserVlidaate();
		  //this.defHomeMenu=defMenuEnable;
	  }

	  ngOnDestroy() {
		
	  }
	  ngOnInit() {
		  //if (this.userNameSession == null || this.userNameSession == undefined || this.userNameSession == '') {
		  //	this.router.navigate(['/']);
		  //}
		  
		  //this.getZoyOwnerList()
	  }
	  ngAfterViewInit() {
		 
	  }
	 
	  onDrop(files: FileList) {
		for (let index = 0; index < files.length; index++) {
		  const file = files[index];
		
		  this.uploadFiles();
		}
	  }
	
	  onClick() {
		const fileUpload = document.getElementById(
		  "fileUpload"
		) as HTMLInputElement;
		fileUpload.onchange = () => {
		  for (let index = 0; index < fileUpload.files.length; index++) {
			const file = fileUpload.files[index];
		  }
		  this.uploadFiles();
		};
		fileUpload.click();
	  }
	  cancelFile(file: FileUploadModel) {
		file.sub.unsubscribe();
		this.removeFileFromArray(file);
	  }
	  retryFile(file: FileUploadModel) {
		this.uploadFile(file);
		file.canRetry = false;
	  }
	  private uploadFile(file: FileUploadModel) {
		const fd = new FormData();
		fd.append(this.param, file.data);
	
		const req = new HttpRequest("POST", this.target, fd, {
		  reportProgress: true
		});
		file.inProgress = true;
		file.sub = this.http
		  .request(req)
		  .pipe(
			map(event => {
			  switch (event.type) {
				case HttpEventType.UploadProgress:
				  file.progress = Math.round((event.loaded * 100) / event.total);
				  break;
				case HttpEventType.Response:
				  return event;
			  }
			}),
			tap(message => {}),
			last(),
		  )
		  .subscribe((event: any) => {
			if (typeof event === "object") {
			  this.removeFileFromArray(file);
			  this.complete.emit(event.body);
			}
		  });
	  }
	
	  private uploadFiles() {
		const fileUpload = document.getElementById(
		  "fileUpload"
		) as HTMLInputElement;
		fileUpload.value = "";
	
		this.files.forEach(file => {
		  this.uploadFile(file);
		});
	  }
	
	  private removeFileFromArray(file: FileUploadModel) {
		const index = this.files.indexOf(file);
		if (index > -1) {
		  this.files.splice(index, 1);
		}
	  }
  }  