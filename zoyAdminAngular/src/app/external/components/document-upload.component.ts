import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { HttpClient, HttpEventType, HttpRequest } from '@angular/common/http';
import { FileUploadModel } from '../model/file-upload.model';
import { last, map, tap } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { DocumentUploadService } from '../services/document-upload.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';


@Component({
  selector: 'app-document-upload',
  templateUrl: './document-upload.component.html',
  styleUrl: './document-upload.component.css'
})
export class DocumentUploadComponent implements OnInit, AfterViewInit {
		@Input() text = "Upload";
		@Input() param = "file";
		@Output() complete = new EventEmitter<string>();
        files: Array<FileUploadModel> = [];
		token: string = '';
		uploaded = false;
		verify = false;
		fileToUpload: File | null = null;
	    target: string;

	constructor(private route: ActivatedRoute, private http: HttpClient,private documentUploadService: DocumentUploadService, 
		private spinner:NgxSpinnerService,private notifyService:NotificationService,) {}

	 
	ngOnDestroy() {
	}
	ngOnInit() {
	  this.getVerify();
	}
	ngAfterViewInit() {
	}
   
	submit() {
	  this.uploaded = true;
	  }
	 
	  handleFileInput(files: FileList) {
		this.fileToUpload = files.item(0);
		console.log("handleFileInput" ,this.handleFileInput);
	}
	uploadFileToActivity() {
		this.documentUploadService.uploadDocumentUpload(this.fileToUpload).subscribe(data => {
		  // do something, if upload success
		  }, error => {
			console.log(error);
		  });
	  }
	  onDrop(files: FileList): void {
		for (let index = 0; index < files.length; index++) {
		  const file = files[index];
		  const fileExtension = file.name.split('.').pop()?.toLowerCase();
		  if (fileExtension !== "pdf") {
			console.error(`Invalid file extension (${file.name}): Only .pdf files are allowed.`);
			this.notifyService.showError(`Invalid file extension (${file.name}): Only .pdf files are allowed.`, "");
			continue;
		  }
		  if (file.type !== "application/pdf") {
			console.error(`Invalid file type (${file.name}): Only PDFs are allowed.`);
			this.notifyService.showError(`Invalid file type (${file.name}): Only PDFs are allowed.`, "");
			continue;
		  }
		  if (file.size > 5 * 1024 * 1024) {
			console.error(`File too large (${file.name}): Maximum size is 5MB.`);
			this.notifyService.showError(`File too large (${file.name}): Maximum size is 5MB.`, "");
			continue;
		  }
		  this.files.push({
			data: file,
			state: "in",
			inProgress: false,
			progress: 0,
			canRetry: false,
			canCancel: true,
			sub: undefined
		  });
		  console.log(`File accepted: ${file.name}`);
		  this.notifyService.showSuccess(`File "${file.name}" added successfully`, "");
		}
		this.uploadFiles();
	  }
	  
	  onClick(): void {
		const fileUpload = document.getElementById("fileUpload") as HTMLInputElement;
		if (fileUpload) {
		  fileUpload.onchange = () => {
			const files = fileUpload.files;
			if (files) {
			  for (let index = 0; index < files.length; index++) {
				const file = files[index];
				
				const fileExtension = file.name.split('.').pop()?.toLowerCase();
				if (fileExtension !== "pdf") {
				  console.error(`Invalid file extension (${file.name}): Only .pdf files are allowed.`);
				  this.notifyService.showError(`Invalid file format : Only .pdf files are allowed.`, "");
				  continue;
				}
				if (file.type !== "application/pdf") {
				  console.error(`Invalid file type (${file.name}): Only PDFs are allowed.`);
				  this.notifyService.showError(`Invalid file type : Only PDFs are allowed.`, "");
				  continue;
				}
	  
				if (file.size > 5 * 1024 * 1024) {
				  console.error(`File too large (${file.name}): Maximum size is 5MB.`);
				  this.notifyService.showError(`File too large, Choose filesize is  below 5MB.`, "");
				  continue;
				}
				this.files.push({
				  data: file,
				  state: "in",
				  inProgress: false,
				  progress: 0,
				  canRetry: false,
				  canCancel: true,
				  sub: undefined
				});
				const reader = new FileReader();
				reader.onload = (event: any) => {
				  console.log(`File Content (${file.name}):`, event.target.result);
				  this.notifyService.showSuccess("File Uploaded Successfully (${file.data.name})", "");
				};
				reader.onerror = (error) => {
				  console.error(`Error reading file (${file.name}):`, error);
				  this.notifyService.showError("Error reading file", "");
				};
				reader.readAsText(file);
			  }
			}
			this.uploadFiles();
			console.log("Uploaded Files:", this.files);
		  };
		  fileUpload.click();
		} else {
		  console.error("File upload element not found!");
		}
	  }
	  
	  private uploadFile(file: FileUploadModel) {
		const fileFormat= new FormData();
		fileFormat.append(this.param, file.data);
	
		const req = new HttpRequest("POST", this.target, fileFormat, {
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

	  uploadDocumentUpload(fileToUpload:any){
		this.submit();
		 this.spinner.show();
		 this.documentUploadService.uploadDocumentUpload(this.fileToUpload).subscribe(data => {
		  
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

	   getVerify(){
		this.route.paramMap.subscribe(params => {
			this.token = params.get('name') || ''; 
			console.log(this.token); 
		  });
		 this.spinner.show();
		 this.documentUploadService.getVerify(this.token).subscribe(data => {
		
		   this.spinner.hide();
		}, error => {
			this.verify=false;
			console.log("error",error); 
		});
	   
	   }
  }  