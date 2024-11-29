import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { HttpClient, HttpEventType, HttpRequest } from '@angular/common/http';
import { FileUploadModel } from '../model/file-upload.model';
import { last, map, tap } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { DocumentUploadService } from '../services/document-upload.service';


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
        files: Array<FileUploadModel> = [];
		token: string = '';

	constructor(private route: ActivatedRoute, private http: HttpClient,private documentUploadService: DocumentUploadService, private spinner:NgxSpinnerService) {}

	  ngOnDestroy() {
	  }
	  ngOnInit() {
		this.route.paramMap.subscribe(params => {
			this.token = params.get('name') || ''; 
		  });
	  }
	  ngAfterViewInit() {
	  }
	 
	  uploaded = false;
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

	  getDocumentUpload(){
		 this.spinner.show();
		 this.documentUploadService.getDocumentUpload().subscribe(data => {
	// 	if(data !=null && data.length>0){

	// 	  this.ELEMENT_DATA = Object.assign([],data);
	// 	   this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
	// 	   this.dataSource.sort = this.sort;
	// 	   this.dataSource.paginator = this.paginator;
	// 	}else{
	// 	  this.ELEMENT_DATA = Object.assign([]);
	// 	   this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
	// 	   this.dataSource.sort = this.sort;
	// 	   this.dataSource.paginator = this.paginator;
	// 	}
	// 	   this.spinner.hide();
	// 	}, error => {
	// 	 this.spinner.hide();
	// 	 if(error.status == 0) {
	// 	  this.notifyService.showError("Internal Server Error/Connection not established", "")
	// 	 }else if(error.status==403){
	// 	   this.router.navigate(['/forbidden']);
	// 	 }else if (error.error && error.error.message) {
	// 	   this.errorMsg = error.error.message;
	// 	   console.log("Error:" + this.errorMsg);
	// 	   this.notifyService.showError(this.errorMsg, "");
	// 	 } else {
	// 	   if (error.status == 500 && error.statusText == "Internal Server Error") {
	// 		 this.errorMsg = error.statusText + "! Please login again or contact your Help Desk.";
	// 	   } else {
	// 		 let str;
	// 		 if (error.status == 400) {
	// 		   str = error.error;
	// 		 } else {
	// 		   str = error.message;
	// 		   str = str.substring(str.indexOf(":") + 1);
	// 		 }
	// 		 console.log("Error:" + str);
	// 		 this.errorMsg = str;
	// 	   }
	// 	   if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
	// 	 }
		});

	   }
  }  