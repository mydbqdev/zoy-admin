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
		@Input() accept: string = "application/pdf";
		@Output() complete = new EventEmitter<string>();
        // files: Array<FileUploadModel> = [];
		token: string = '';
		uploaded = false;
		verify = false;
		fileToUpload: File | null = null;
	    target: string;
		files: FileUploadModel[] = [];
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
		  }, error => {
			console.log(error);
		  });
	  }
	  onDrop(files: FileList): void {
		for (let index = 0; index < files.length; index++) {
		  const file = files[index];
		  const fileExtension = file.name.includes(".")? file.name.split('.').pop()?.toLowerCase(): null;
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
		 
		}
		this.uploadFiles();
	  }
	  
	  onClick(): void {
		const fileUpload = document.getElementById("fileUpload") as HTMLInputElement;
	  
		if (!fileUpload) {
		  console.error("File upload element not found!");
		  return;
		}
	  
		fileUpload.accept = this.accept;
	  
		fileUpload.onchange = () => {
		  const files = fileUpload.files;
	  
		  if (files) {
			let totalSize = this.files.reduce((acc, file) => acc + file.data.size, 0);
	  
			for (let index = 0; index < files.length; index++) {
			  const file = files[index];
			  const fileName = file.name;
			  const fileExtension = fileName.includes(".")
				? fileName.split(".").pop()?.toLowerCase()
				: null;
	  
			  if (!fileExtension) {
				console.error(`Invalid file name (${fileName}): No extension found.`);
				this.notifyService.showError(`Invalid file format: Missing file extension.`, "");
				continue;
			  }
	  
			  if (fileExtension !== "pdf") {
				console.error(`Invalid file extension (${fileName}): Only .pdf files are allowed.`);
				this.notifyService.showError(`Invalid file format: Only .pdf files are allowed.`, "");
				continue;
			  }
	  
			  if (file.type !== "application/pdf") {
				console.error(`Invalid file type (${fileName}): Only PDFs are allowed.`);
				this.notifyService.showError(`Invalid file type: Only PDFs are allowed.`, "");
				continue;
			  }
	  
			  if (totalSize + file.size > 5 * 1024 * 1024) {
				console.error(`Total file size exceeded: Maximum cumulative size is 5MB.`);
				this.notifyService.showError(`Total file size exceeded: Maximum cumulative size is 5MB.`, "");
				break;
			  }
	  
			  this.files.push({
				data: file,
				state: "in",
				inProgress: false,
				progress: 0,
				canRetry: false,
				canCancel: true,
				sub: undefined,
			  });
	  
			  totalSize += file.size;
	  
			  const reader = new FileReader();
			  reader.onload = (event: any) => {
			  };
			  reader.onerror = (error) => {
				console.error(`Error reading file (${fileName}):`, error);
				this.notifyService.showError("Error reading file", "");
			  };
			  reader.readAsText(file);
			}
	  
			console.log("Uploaded Files:", this.files);
			this.uploadFiles();
		  }
		};
	  
		fileUpload.click();
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
	
	   removeFileFromArray(file: FileUploadModel) {
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